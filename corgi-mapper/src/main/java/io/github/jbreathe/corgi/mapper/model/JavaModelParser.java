package io.github.jbreathe.corgi.mapper.model;

import io.github.jbreathe.corgi.api.Consumer;
import io.github.jbreathe.corgi.api.FieldName;
import io.github.jbreathe.corgi.api.Init;
import io.github.jbreathe.corgi.api.Mapping;
import io.github.jbreathe.corgi.api.PreCondition;
import io.github.jbreathe.corgi.api.Producer;
import io.github.jbreathe.corgi.api.Read;
import io.github.jbreathe.corgi.api.ReadResult;
import io.github.jbreathe.corgi.api.Write;
import io.github.jbreathe.corgi.mapper.model.core.Annotation;
import io.github.jbreathe.corgi.mapper.model.core.Field;
import io.github.jbreathe.corgi.mapper.model.core.Type;
import io.github.jbreathe.corgi.mapper.model.core.TypeDeclaration;
import io.github.jbreathe.corgi.mapper.model.core.VarDeclaration;
import org.jetbrains.annotations.NotNull;

import javax.lang.model.element.AnnotationMirror;
import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.TypeElement;
import javax.lang.model.element.VariableElement;
import javax.lang.model.type.TypeKind;
import javax.lang.model.type.TypeMirror;
import javax.lang.model.util.Types;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public final class JavaModelParser {
    public static final String NOT_A_BOOLEAN_METHOD = "Method annotated with @PreCondition may return only primitive 'boolean' or 'java.lang.Boolean', but method '%s' returns '%s'";

    private final Types types;

    public JavaModelParser(Types types) {
        this.types = types;
    }

    public MappingClass parseToInternalRepresentation(TypeElement mapperElement, CustomMethodModifier customMethodModifier) {
        List<MappingMethod> mappingMethods = new ArrayList<>();
        Map<String, InitMethod> initMethods = new HashMap<>();
        Map<String, ReadMethod> readMethods = new HashMap<>();
        Map<String, WriteMethod> writeMethods = new HashMap<>();
        Map<String, PreConditionMethod> preConditionMethods = new HashMap<>();

        for (Element element : mapperElement.getEnclosedElements()) {
            if (element.getKind() != ElementKind.METHOD) {
                continue;
            }
            ExecutableElement methodElement = (ExecutableElement) element;
            if (!JavaModelUtil.methodHasModifier(methodElement, customMethodModifier)) {
                // collect mapping methods
                Mapping mapping = methodElement.getAnnotation(Mapping.class);
                if (mapping == null) {
                    throw new ModelParsingException("Mapping method must be annotated with @Mapping", methodElement);
                }
                if (isMapMethod(methodElement)) {
                    mappingMethods.add(parseMappingMethod(methodElement));
                } else {
                    throw new ModelParsingException("Only 'map' methods supported", methodElement);
                }
            } else {
                // collect custom methods
                Init init = methodElement.getAnnotation(Init.class);
                Read read = methodElement.getAnnotation(Read.class);
                Write write = methodElement.getAnnotation(Write.class);
                PreCondition preCondition = methodElement.getAnnotation(PreCondition.class);
                if (init != null) {
                    initMethods.put(methodName(init.value(), methodElement), getInitMethod(methodElement));
                } else if (read != null) {
                    readMethods.put(methodName(read.value(), methodElement), getReadMethod(methodElement));
                } else if (write != null) {
                    writeMethods.put(methodName(write.value(), methodElement), getWriteMethod(methodElement));
                } else if (preCondition != null) {
                    preConditionMethods.put(methodName(preCondition.value(), methodElement), getPreConditionMethod(methodElement));
                }
            }
        }
        return new MappingClass(typeFromTypeElement(mapperElement), mappingMethods, initMethods, readMethods, writeMethods, preConditionMethods);
    }

    @NotNull
    private MappingMethod parseMappingMethod(ExecutableElement methodElement) {
        VariableElement producerElement = getProducerForMapping(methodElement);
        Element producerTypeElement = types.asElement(producerElement.asType());
        Element consumerTypeElement = types.asElement(methodElement.getReturnType());
        Struct producerStruct = parseStruct(producerTypeElement);
        Struct consumerStruct = parseStruct(consumerTypeElement);
        Struct fieldsSource;
        if (producerElement.getAnnotation(Producer.class) != null && producerElement.getAnnotation(Producer.class).fieldsSource()) {
            fieldsSource = producerStruct;
        } else {
            fieldsSource = consumerStruct;
        }
        return MappingMethod.fromNameAndResultType(JavaModelUtil.methodName(methodElement), createTypeDeclaration(methodElement.getReturnType()))
                .parameters(methodElement.getParameters().stream().map(this::createVarDeclaration).collect(Collectors.toList()))
                .customMethodsFromMapping(methodElement.getAnnotation(Mapping.class))
                .producer(producerStruct)
                .producerVar(createVarDeclaration(producerElement))
                .consumer(consumerStruct)
                .consumerVar(consumerVar(methodElement))
                .fieldsSource(fieldsSource)
                .build();
    }

    private Struct parseStruct(Element typeElement) {
        if (typeElement.asType().getKind().isPrimitive()) {
            throw new ModelParsingException("@Consumer, @Producer and @FieldsSource must be of reference type", typeElement);
        }

        Type structType = createType(typeElement.asType());
        DefaultConstructor defaultConstructor = null;
        List<Field> fields = new ArrayList<>();
        List<Getter> getters = new ArrayList<>();
        List<Setter> setters = new ArrayList<>();

        for (Element element : typeElement.getEnclosedElements()) {
            if (element.getKind() == ElementKind.CONSTRUCTOR) {
                if (isDefaultConstructor((ExecutableElement) element)) {
                    defaultConstructor = new DefaultConstructor(structType);
                }
            } else if (element.getKind() == ElementKind.METHOD) {
                ExecutableElement methodElement = (ExecutableElement) element;
                // getters and setters
                if (isInGetterFormat(methodElement)) {
                    getters.add(createGetter(structType, methodElement));
                } else if (isInSetterForm(methodElement)) {
                    setters.add(createSetter(structType, methodElement));
                }
            } else if (element.getKind() == ElementKind.FIELD) {
                fields.add(createField((VariableElement) element));
            }
        }
        return Struct.createStruct(structType, defaultConstructor, fields, getters, setters);
    }

    private boolean isDefaultConstructor(ExecutableElement constructorElement) {
        return isPublic(constructorElement) && constructorElement.getParameters().isEmpty();
    }

    private boolean isPublic(Element element) {
        for (Modifier modifier : element.getModifiers()) {
            if (modifier == Modifier.PUBLIC) {
                return true;
            }
        }
        return false;
    }

    private boolean isInGetterFormat(ExecutableElement methodElement) {
        if (isVoidMethod(methodElement)) {
            return false;
        }
        boolean noParams = methodElement.getParameters().isEmpty();
        String methodName = methodElement.getSimpleName().toString();
        if (methodElement.getReturnType().getKind() == TypeKind.BOOLEAN) {
            return methodName.startsWith("is") && noParams;
        }
        return methodName.startsWith("get") && noParams;
    }

    private boolean isInSetterForm(ExecutableElement methodElement) {
        if (!isVoidMethod(methodElement)) {
            return false;
        }
        boolean exactlyOneParameter = methodElement.getParameters().size() == 1;
        String methodName = methodElement.getSimpleName().toString();
        return methodName.startsWith("set") && exactlyOneParameter;
    }

    @NotNull
    private Getter createGetter(Type type, ExecutableElement methodElement) {
        return Getter.createGetter(type, JavaModelUtil.methodName(methodElement), createTypeDeclaration(methodElement.getReturnType()));
    }

    @NotNull
    private Setter createSetter(Type type, ExecutableElement methodElement) {
        VariableElement setterParameter = methodElement.getParameters().get(0);
        return Setter.createSetter(type, JavaModelUtil.methodName(methodElement), createVarDeclaration(setterParameter));
    }

    private Field createField(VariableElement variableElement) {
        return new Field(createTypeDeclaration(variableElement.asType()), JavaModelUtil.variableName(variableElement));
    }

    private boolean isMapMethod(ExecutableElement methodElement) {
        List<? extends VariableElement> parameters = methodElement.getParameters();
        // not void and only one parameter or only one producer
        return !isVoidMethod(methodElement) &&
                (parameters.size() == 1 || parameters.stream().filter(v -> v.getAnnotation(Producer.class) != null).count() == 1);
    }

    private boolean isVoidMethod(ExecutableElement methodElement) {
        return methodElement.getReturnType().getKind() == TypeKind.VOID;
    }

    private boolean isBooleanMethod(ExecutableElement methodElement) {
        TypeMirror returnType = methodElement.getReturnType();
        return boolean.class.getName().equals(returnType.toString())
                || Boolean.class.getName().equals(returnType.toString());
    }

    /**
     * Find {@code @Producer} in method annotated with {@code @Mapping}.
     *
     * @param methodElement method annotated with {@code @Mapping}
     * @return producer
     */
    private VariableElement getProducerForMapping(ExecutableElement methodElement) {
        List<? extends VariableElement> parameters = methodElement.getParameters();
        if (parameters.size() == 1) {
            return parameters.get(0);
        }
        return parameters.stream()
                .filter(v -> v.getAnnotation(Producer.class) != null)
                .findFirst()
                .orElseThrow();
    }

    @NotNull
    private VarDeclaration consumerVar(ExecutableElement methodElement) {
        if (methodElement.getParameters().stream().anyMatch(p -> "consumer".equals(JavaModelUtil.variableName(p)))) {
            throw new ModelParsingException("'consumer' is a reserved name, but one of the method parameters uses this name", methodElement);
        }
        TypeDeclaration typeDeclaration = createTypeDeclaration(methodElement.getReturnType());
        return VarDeclaration.declarationWithoutAnnotations(typeDeclaration, "consumer");
    }

    @NotNull
    private VarDeclaration createVarDeclaration(VariableElement variableElement) {
        String name = JavaModelUtil.variableName(variableElement);
        TypeDeclaration typeDeclaration = createTypeDeclaration(variableElement.asType());
        return VarDeclaration.declarationWithoutAnnotations(typeDeclaration, name);
    }

    @NotNull
    private VarDeclaration createVarDeclarationWithAnnotations(VariableElement variableElement) {
        String name = JavaModelUtil.variableName(variableElement);
        TypeDeclaration typeDeclaration = createTypeDeclaration(variableElement.asType());
        List<@NotNull Annotation> annotations = variableElement.getAnnotationMirrors().stream()
                .map(this::createAnnotation)
                .collect(Collectors.toList());
        return VarDeclaration.declarationWithAnnotations(typeDeclaration, name, annotations);
    }

    @NotNull
    private TypeDeclaration createTypeDeclaration(TypeMirror typeMirror) {
        if (typeMirror.getKind() == TypeKind.VOID || typeMirror.getKind().isPrimitive()) {
            return TypeDeclaration.rawDeclaration(Type.fromFullName(typeMirror.toString()));
        }
        Type type = createType(typeMirror);
        TypeParametersVisitor visitor = new TypeParametersVisitor(types);
        List<TypeDeclaration> typeParameters = typeMirror.accept(visitor, null);
        return TypeDeclaration.parameterizedDeclaration(type, typeParameters);
    }

    @NotNull
    private Type createType(TypeMirror typeMirror) {
        Element element = types.asElement(typeMirror);
        return Type.fromFullName(element.toString());
    }

    private String methodName(@NotNull String name, ExecutableElement methodElement) {
        if (!name.isEmpty()) {
            return name;
        } else {
            return JavaModelUtil.methodName(methodElement);
        }
    }

    @NotNull
    private InitMethod getInitMethod(ExecutableElement methodElement) {
        return new InitMethod(
                typeFromTypeElement((TypeElement) methodElement.getEnclosingElement()),
                JavaModelUtil.methodName(methodElement),
                createTypeDeclaration(methodElement.getReturnType()),
                methodElement.getParameters().stream().map(this::createVarDeclaration).collect(Collectors.toList()));
    }

    @NotNull
    private ReadMethod getReadMethod(ExecutableElement methodElement) {
        validateReadParameters(methodElement);
        List<VarDeclaration> readParameters = methodElement.getParameters().stream()
                .map(this::createVarDeclarationWithAnnotations)
                .collect(Collectors.toList());
        return new ReadMethod(
                typeFromTypeElement((TypeElement) methodElement.getEnclosingElement()),
                JavaModelUtil.methodName(methodElement),
                createTypeDeclaration(methodElement.getReturnType()),
                readParameters
        );
    }

    @NotNull
    private WriteMethod getWriteMethod(ExecutableElement methodElement) {
        validateWriteParameters(methodElement);
        List<VarDeclaration> writeParameters = methodElement.getParameters().stream()
                .map(this::createVarDeclarationWithAnnotations)
                .collect(Collectors.toList());
        return new WriteMethod(
                typeFromTypeElement((TypeElement) methodElement.getEnclosingElement()),
                JavaModelUtil.methodName(methodElement),
                createTypeDeclaration(methodElement.getReturnType()),
                writeParameters
        );
    }

    @NotNull
    private PreConditionMethod getPreConditionMethod(ExecutableElement methodElement) {
        if (!isBooleanMethod(methodElement)) {
            throw new ModelParsingException(String.format(NOT_A_BOOLEAN_METHOD, JavaModelUtil.methodName(methodElement), methodElement.getReturnType().toString()), methodElement);
        }
        validatePreConditionParameters(methodElement);
        List<VarDeclaration> readParameters = methodElement.getParameters().stream()
                .map(this::createVarDeclarationWithAnnotations)
                .collect(Collectors.toList());
        return new PreConditionMethod(
                typeFromTypeElement((TypeElement) methodElement.getEnclosingElement()),
                JavaModelUtil.methodName(methodElement),
                createTypeDeclaration(methodElement.getReturnType()),
                readParameters
        );
    }

    private void validateReadParameters(ExecutableElement methodElement) {
        if (!hasExactlyOneAnnotatedParameter(methodElement, Producer.class)) {
            throw new ModelParsingException("@Read method must have one @Producer", methodElement);
        }
        if (!hasExactlyOneAnnotatedParameter(methodElement, FieldName.class)) {
            throw new ModelParsingException("@Read method must have one @FieldName", methodElement);
        }
        validateFieldName(collectFieldNames(methodElement).get(0));
    }

    private void validateWriteParameters(ExecutableElement methodElement) {
        if (!hasExactlyOneAnnotatedParameter(methodElement, Consumer.class)
                && !hasExactlyOneAnnotatedParameter(methodElement, ReadResult.class)) {
            throw new ModelParsingException("@Write method must have one @Consumer and one @ReadResult", methodElement);
        }
        List<? extends VariableElement> fieldNameParams = collectFieldNames(methodElement);
        if (!fieldNameParams.isEmpty()) {
            if (fieldNameParams.size() > 1) {
                throw new ModelParsingException("@Write method can't have more than one @FieldName", methodElement);
            }
            validateFieldName(fieldNameParams.get(0));
        }
    }

    private void validatePreConditionParameters(ExecutableElement methodElement) {
        if (hasExactlyOneProducerAndFieldName(methodElement)) {
            if (hasParameterAnnotatedWith(methodElement, ReadResult.class) && !hasExactlyOneAnnotatedParameter(methodElement, ReadResult.class)) {
                throw new ModelParsingException("@PreCondition must have exactly one @ReadResult", methodElement);
            }
        } else {
            if (!hasExactlyOneReadResultAndNothingElse(methodElement)) {
                throw new ModelParsingException("@PreCondition method may have either one @Producer and one @FieldName (with one optional @ReadResult)" +
                        " or simply one @ReadResult without any other annotations", methodElement);
            }
        }
    }

    private boolean hasExactlyOneProducerAndFieldName(ExecutableElement methodElement) {
        return hasExactlyOneAnnotatedParameter(methodElement, Producer.class)
                && hasExactlyOneAnnotatedParameter(methodElement, FieldName.class)
                && isValidFieldName(collectFieldNames(methodElement).get(0));
    }

    private boolean hasExactlyOneReadResultAndNothingElse(ExecutableElement methodElement) {
        return !hasParameterAnnotatedWith(methodElement, Producer.class)
                && !hasParameterAnnotatedWith(methodElement, FieldName.class)
                && hasExactlyOneAnnotatedParameter(methodElement, ReadResult.class);
    }

    private void validateFieldName(VariableElement fieldNameParameter) {
        if (!isValidFieldName(fieldNameParameter)) {
            throw new ModelParsingException("Parameter annotated with @FieldName must be of type 'java.lang.String', but found '" + JavaModelUtil.variableTypeName(fieldNameParameter) + "'", fieldNameParameter);
        }
    }

    private boolean isValidFieldName(VariableElement fieldNameParameter) {
        String variableTypeName = JavaModelUtil.variableTypeName(fieldNameParameter);
        return String.class.getName().equals(variableTypeName);
    }

    private boolean hasExactlyOneAnnotatedParameter(ExecutableElement methodElement, Class<? extends java.lang.annotation.Annotation> annotationClass) {
        return methodElement.getParameters().stream()
                .filter(m -> m.getAnnotation(annotationClass) != null).count() == 1;
    }

    private boolean hasParameterAnnotatedWith(ExecutableElement methodElement, Class<? extends java.lang.annotation.Annotation> annotationClass) {
        return methodElement.getParameters().stream()
                .anyMatch(m -> m.getAnnotation(annotationClass) != null);
    }

    private List<? extends VariableElement> collectFieldNames(ExecutableElement methodElement) {
        return methodElement.getParameters().stream()
                .filter(m -> m.getAnnotation(FieldName.class) != null)
                .collect(Collectors.toList());
    }

    @NotNull
    private Type typeFromTypeElement(TypeElement typeElement) {
        return Type.fromFullName(JavaModelUtil.elementTypeName(typeElement));
    }

    @NotNull
    private Annotation createAnnotation(AnnotationMirror annotationMirror) {
        return new Annotation(JavaModelUtil.annotationTypeName(annotationMirror));
    }
}
