package io.github.jbreathe.corgi.mapper;

import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.JavaFile;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.ParameterizedTypeName;
import com.squareup.javapoet.TypeName;
import com.squareup.javapoet.TypeSpec;
import io.github.jbreathe.corgi.api.Mapper;
import io.github.jbreathe.corgi.mapper.codegen.Assignment;
import io.github.jbreathe.corgi.mapper.codegen.BooleanExpression;
import io.github.jbreathe.corgi.mapper.codegen.Expression;
import io.github.jbreathe.corgi.mapper.codegen.IfStatement;
import io.github.jbreathe.corgi.mapper.codegen.MethodCall;
import io.github.jbreathe.corgi.mapper.codegen.VarReference;
import io.github.jbreathe.corgi.mapper.model.CustomMethodModifier;
import io.github.jbreathe.corgi.mapper.model.DefaultConstructor;
import io.github.jbreathe.corgi.mapper.model.Getter;
import io.github.jbreathe.corgi.mapper.model.InitMethod;
import io.github.jbreathe.corgi.mapper.model.JavaModelParser;
import io.github.jbreathe.corgi.mapper.model.MappingClass;
import io.github.jbreathe.corgi.mapper.model.MappingMethod;
import io.github.jbreathe.corgi.mapper.model.ModelParsingException;
import io.github.jbreathe.corgi.mapper.model.PreConditionMethod;
import io.github.jbreathe.corgi.mapper.model.ReadMethod;
import io.github.jbreathe.corgi.mapper.model.Setter;
import io.github.jbreathe.corgi.mapper.model.Struct;
import io.github.jbreathe.corgi.mapper.model.WriteMethod;
import io.github.jbreathe.corgi.mapper.model.core.Field;
import io.github.jbreathe.corgi.mapper.model.core.Type;
import io.github.jbreathe.corgi.mapper.model.core.TypeDeclaration;
import io.github.jbreathe.corgi.mapper.model.core.VarDeclaration;
import io.github.jbreathe.corgi.mapper.source.SourceFile;
import io.github.jbreathe.corgi.mapper.source.SourceFilesParser;
import io.github.jbreathe.corgi.mapper.source.SourceFilesSpoonParser;
import io.github.jbreathe.corgi.mapper.source.SourceMethod;
import org.jetbrains.annotations.NotNull;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.TypeElement;
import javax.tools.JavaFileObject;
import java.io.IOException;
import java.io.Writer;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class CorgiProcessor extends AbstractProcessor {
    private Logger logger;
    private JavaModelParser javaModelParser;
    private SourceFilesParser sourceFilesParser;

    @Override
    public synchronized void init(ProcessingEnvironment processingEnv) {
        super.init(processingEnv);
        logger = new Logger(processingEnv.getMessager());
        javaModelParser = new JavaModelParser(processingEnv.getTypeUtils());
        sourceFilesParser = new SourceFilesSpoonParser(processingEnv.getFiler());
    }

    @Override
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
        if (isLoweThanJava8()) {
            logger.error("Source level '" + processingEnv.getSourceVersion() + "' is lower than Java 8");
            return false;
        }
        try {
            processRound(roundEnv);
            return true;
        } catch (ModelParsingException e) {
            logger.error(e.getMessage(), e.getElement());
            return false;
        } catch (ProcessingException e) {
            logger.error(e.getMessage());
            return false;
        }
    }

    @Override
    public SourceVersion getSupportedSourceVersion() {
        return SourceVersion.latestSupported();
    }

    @Override
    public Set<String> getSupportedAnnotationTypes() {
        return Set.of(Mapper.class.getName());
    }

    private void processRound(RoundEnvironment roundEnv) {
        Set<? extends Element> mapperElements = roundEnv.getElementsAnnotatedWith(Mapper.class);
        for (Element mapperElement : mapperElements) {
            if (mapperElement.getKind() != ElementKind.INTERFACE) {
                continue;
            }

            // parse (collect all information from annotations)
            CustomMethodModifier customMethodModifier = getCustomMethodModifier();
            MappingClass mappingClass = javaModelParser.parseToInternalRepresentation((TypeElement) mapperElement, customMethodModifier);

            // process and generate implementation
            TypeSpec.Builder mapperBuilder = generateMapperImpl(mappingClass);

            for (MappingMethod mappingMethod : mappingClass.getMappingMethods()) {
                MethodSpec.Builder methodBuilder = generatePublicMethod(mappingMethod);
                for (VarDeclaration parameter : mappingMethod.getParameters()) {
                    methodBuilder.addParameter(typeName(parameter.getTypeDeclaration()), parameter.getName());
                }

                Expression initializer = generateInitializer(mappingClass, mappingMethod);
                VarReference consumerVarReference = mappingMethod.getConsumerVar().createReference();
                Assignment assignment = consumerVarReference.initWith(initializer);
                methodBuilder.addStatement(assignment.asCode());

                List<Field> fields = mappingMethod.getFieldsSource().getFields();

                for (Field field : fields) {
                    MethodCall readCall = generateReadCall(mappingClass, mappingMethod, field);
                    MethodCall writeCall = generateWriteCall(mappingClass, mappingMethod, readCall, field);
                    if (mappingMethod.withCustomPreCondition()) {
                        BooleanExpression preCondition = generatePreCondition(mappingClass, mappingMethod, readCall, field);
                        IfStatement ifStatement = preCondition.wrapWithIf();
                        methodBuilder.beginControlFlow(ifStatement.asCode())
                                .addStatement(writeCall.asCode())
                                .endControlFlow();
                    } else {
                        methodBuilder.addStatement(writeCall.asCode());
                    }
                }

                methodBuilder.addStatement(consumerVarReference.asResultStatement().asCode());
                mapperBuilder.addMethod(methodBuilder.build());
            }

            // parse interface and copy private methods in Java 9 implementation
            if (isHigherThanJava8()) {
                SourceFile sourceFile = sourceFilesParser.parse(mappingClass.getType());
                List<SourceMethod> privateMethods = sourceFile.getPrivateMethods();
                for (SourceMethod privateMethod : privateMethods) {
                    MethodSpec.Builder privateMethodBuilder = generatePrivateMethodsSignature(privateMethod)
                            .addCode(privateMethod.getBody());
                    mapperBuilder.addMethod(privateMethodBuilder.build());
                }
                JavaFile javaFile = generateJavaFile(mappingClass, mapperBuilder.build());
                String rawSource = replaceAllImportsInFile(javaFile, sourceFile.getImports());
                saveRawSourceFile(mappingClass, rawSource);
            } else {
                JavaFile javaFile = generateJavaFile(mappingClass, mapperBuilder.build());
                saveSourceFile(javaFile);
            }
        }
    }

    @NotNull
    private Expression generateInitializer(MappingClass mappingClass, MappingMethod mappingMethod) {
        if (mappingMethod.withCustomInit()) {
            String initName = mappingMethod.getInitName();
            InitMethod initMethod = mappingClass.findInitMethod(initName);
            if (initMethod == null) {
                throw new ProcessingException("No methods found '" + initName + "' references to");
            }
            return initMethod.generateCall(mappingMethod);
        } else {
            DefaultConstructor defaultConstructor = mappingMethod.getConsumer().getDefaultConstructor();
            if (defaultConstructor == null) {
                throw new ProcessingException("Default constructor in type '" + mappingMethod.getConsumer().getType() + "' not found");
            }
            return defaultConstructor.generateCall();
        }
    }

    @NotNull
    private MethodCall generateReadCall(MappingClass mappingClass, MappingMethod mappingMethod, Field field) {
        if (mappingMethod.withCustomRead()) {
            String readName = mappingMethod.getReadName();
            ReadMethod readMethod = mappingClass.findReadMethod(readName);
            if (readMethod == null) {
                throw new ProcessingException("No methods found '" + readName + "' references to");
            }
            return readMethod.generateCall(mappingMethod, field);
        } else {
            Struct producer = mappingMethod.getProducer();
            Getter readMethod = producer.findGetter(field);
            if (readMethod == null) {
                throw new ProcessingException("Getter for field '" + field.getName() + "' not found in type '" + producer.getType() + "'");
            }
            return readMethod.generateCall(mappingMethod.getProducerVar().createReference());
        }
    }

    @NotNull
    private MethodCall generateWriteCall(MappingClass mappingClass, MappingMethod mappingMethod, MethodCall readCall, Field field) {
        if (mappingMethod.withCustomWrite()) {
            String writeName = mappingMethod.getWriteName();
            WriteMethod writeMethod = mappingClass.findWriteMethod(writeName);
            if (writeMethod == null) {
                throw new ProcessingException("No methods found '" + writeName + "' references to");
            }
            return writeMethod.generateCall(mappingMethod, readCall, field);
        } else {
            Struct consumer = mappingMethod.getConsumer();
            Setter writeMethod = consumer.findSetter(field);
            if (writeMethod == null) {
                throw new ProcessingException("Setter for field '" + field.getName() + "' not found in type '" + consumer.getType() + "'");
            }
            return writeMethod.generateCall(mappingMethod.getConsumerVar().createReference(), readCall);
        }
    }

    @NotNull
    private BooleanExpression generatePreCondition(MappingClass mappingClass, MappingMethod mappingMethod, MethodCall readCall, Field field) {
        PreConditionMethod preConditionMethod = mappingClass.findPreConditionMethod(mappingMethod.getPreConditionName());
        if (preConditionMethod == null) {
            throw new ProcessingException("No methods found '" + mappingMethod.getPreConditionName() + "' references to");
        }
        return preConditionMethod.generateCall(mappingMethod, readCall, field);
    }

    // Generation with JavaPoet

    private JavaFile generateJavaFile(MappingClass mappingClass, TypeSpec mapperImpl) {
        return JavaFile.builder(mappingClass.getType().getPackage(), mapperImpl)
                .skipJavaLangImports(true)
                .indent(" ".repeat(4))
                .build();
    }

    private TypeSpec.Builder generateMapperImpl(MappingClass mappingClass) {
        return TypeSpec.classBuilder(mappingClass.getType().getSimpleName() + "Impl")
                .addSuperinterface(classNameFromRawType(mappingClass.getType()))
                .addModifiers(Modifier.PUBLIC);
    }

    private MethodSpec.Builder generatePublicMethod(MappingMethod mappingMethod) {
        return MethodSpec.methodBuilder(mappingMethod.getName())
                .addModifiers(Modifier.PUBLIC)
                .addAnnotation(Override.class)
                .returns(typeName(mappingMethod.getResultType()));
    }

    private MethodSpec.Builder generatePrivateMethodsSignature(SourceMethod method) {
        MethodSpec.Builder builder = MethodSpec.methodBuilder(method.getName())
                .addModifiers(Modifier.PRIVATE)
                .returns(typeName(method.getResultType()));
        for (VarDeclaration parameter : method.getParameters()) {
            builder.addParameter(typeName(parameter.getTypeDeclaration()), parameter.getName());
        }
        return builder;
    }

    private TypeName typeName(TypeDeclaration typeDeclaration) {
        ClassName raw = classNameFromRawType(typeDeclaration.getType());
        if (!typeDeclaration.getTypeParameters().isEmpty()) {
            List<ClassName> collect = typeDeclaration.getTypeParameters().stream()
                    .map(tp -> classNameFromRawType(tp.getType()))
                    .collect(Collectors.toList());
            return ParameterizedTypeName.get(raw, collect.toArray(TypeName[]::new));
        }
        return raw;
    }

    private ClassName classNameFromRawType(Type consumerType) {
        return ClassName.get(consumerType.getPackage(), consumerType.getSimpleName());
    }

    private boolean isLoweThanJava8() {
        return processingEnv.getSourceVersion().compareTo(SourceVersion.RELEASE_8) < 0;
    }

    private boolean isHigherThanJava8() {
        return processingEnv.getSourceVersion().compareTo(SourceVersion.RELEASE_8) > 0;
    }

    private CustomMethodModifier getCustomMethodModifier() {
        return isHigherThanJava8() ? CustomMethodModifier.PRIVATE : CustomMethodModifier.DEFAULT;
    }

    private String replaceAllImportsInFile(JavaFile javaFile, List<String> imports) {
        String rawSource = javaFile.toString();
        List<String> result = new ArrayList<>();
        String[] lines = rawSource.split("\n", -1);
        for (String line : lines) {
            // skip all imports
            if (!line.startsWith("import ")) {
                result.add(line);
                if (line.startsWith("package ")) {
                    result.add("");
                    result.addAll(imports);
                }
            }
        }
        return String.join("\n", result);
    }

    private void saveRawSourceFile(MappingClass mappingClass, String rawSource) {
        String qualifiedName = mappingClass.getType().getQualifiedName() + "Impl";
        try {
            JavaFileObject implSourceFile = processingEnv.getFiler().createSourceFile(qualifiedName);
            try (Writer writer = implSourceFile.openWriter()) {
                writer.append(rawSource);
            }
        } catch (IOException e) {
            throw new ProcessingException(e);
        }
    }

    private void saveSourceFile(JavaFile javaFile) {
        try {
            javaFile.writeTo(processingEnv.getFiler());
        } catch (IOException e) {
            throw new ProcessingException(e);
        }
    }
}
