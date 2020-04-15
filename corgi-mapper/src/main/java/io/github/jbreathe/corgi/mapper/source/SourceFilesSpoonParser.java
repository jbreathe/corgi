package io.github.jbreathe.corgi.mapper.source;

import io.github.jbreathe.corgi.mapper.model.ApiAnnotations;
import io.github.jbreathe.corgi.mapper.model.core.Type;
import io.github.jbreathe.corgi.mapper.model.core.TypeDeclaration;
import io.github.jbreathe.corgi.mapper.model.core.VarDeclaration;
import org.jetbrains.annotations.NotNull;
import spoon.Launcher;
import spoon.reflect.code.CtStatement;
import spoon.reflect.cu.CompilationUnit;
import spoon.reflect.declaration.CtElement;
import spoon.reflect.declaration.CtInterface;
import spoon.reflect.declaration.CtMethod;
import spoon.reflect.declaration.CtModifiable;
import spoon.reflect.declaration.CtType;
import spoon.reflect.reference.CtTypeReference;
import spoon.support.compiler.VirtualFile;

import javax.annotation.processing.Filer;
import javax.tools.FileObject;
import javax.tools.StandardLocation;
import java.io.IOException;
import java.io.Reader;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Collectors;

// parsing of sources can be implemented with:
// 1) Compiler Tree API (maybe info about service loader will help somehow - http://openjdk.java.net/groups/compiler/guide/compilerAPI.html)
// 2) Filer and StandardLocation (to find sources) + something to read them (Spoon, JavaParser)
public final class SourceFilesSpoonParser implements SourceFilesParser {
    private final Filer filer;

    public SourceFilesSpoonParser(Filer filer) {
        this.filer = filer;
    }

    @Override
    public SourceFile parse(Type type) {
        FileObject fileObject = getFileObject(type);
        String content = readAllLines(fileObject);
        CtInterface<?> ctInterface = parseInterface(content);

        List<CtMethod<?>> ctPrivateMethods = ctInterface.getElements(CtModifiable::isPrivate);
        List<SourceMethod> privateMethods = new ArrayList<>(ctPrivateMethods.size());
        for (CtMethod<?> ctMethod : ctPrivateMethods) {
            CtTypeReference<?> reference = ctMethod.getReference().getType();
            TypeDeclaration typeDeclaration = getTypeDeclaration(reference);
            List<VarDeclaration> parameters = ctMethod.getParameters().stream()
                    .map(p -> VarDeclaration.declarationWithoutAnnotations(getTypeDeclaration(p.getType()), p.getSimpleName()))
                    .collect(Collectors.toList());
            StringBuilder bodyBuilder = new StringBuilder();
            for (CtStatement ctStatement : ctMethod.getBody().getStatements()) {
                bodyBuilder.append(ctStatement.toString()).append(";\n");
            }
            SourceMethod sourceMethod = new SourceMethod(ctMethod.getSimpleName(), typeDeclaration, parameters, bodyBuilder.toString());
            privateMethods.add(sourceMethod);
        }

        CompilationUnit cu = ctInterface.getFactory().CompilationUnit().getOrCreate(ctInterface);
        List<String> imports = cu.getImports().stream()
                .filter(imp -> {
                    Iterator<CtTypeReference<?>> it = imp.getReferencedTypes().iterator();
                    return !it.hasNext() || !ApiAnnotations.NAMES.contains(it.next().getQualifiedName());
                })
                .map(CtElement::toString)
                .sorted() // simple sorting to prevent unpredictable order
                .collect(Collectors.toList());

        return new SourceFile(imports, privateMethods);
    }

    private FileObject getFileObject(Type type) {
        try {
            return filer.getResource(StandardLocation.CLASS_PATH, type.getPackage(),
                    type.getSimpleName() + ".java");
        } catch (IOException e) {
            throw new SourceParsingException(e);
        }
    }

    @NotNull
    private CtInterface<?> parseInterface(String file) {
        Launcher launcher = new Launcher();
        launcher.addInputResource(new VirtualFile(file));
        launcher.getEnvironment().setNoClasspath(true);
        launcher.getEnvironment().setAutoImports(true);
        launcher.getEnvironment().setComplianceLevel(9);
        Collection<CtType<?>> allTypes = launcher.buildModel().getAllTypes();
        if (allTypes.size() == 1) {
            return (CtInterface<?>) allTypes.stream().findFirst().get();
        } else {
            throw new SourceParsingException("No interfaces for you!");
        }
    }

    @NotNull
    private String readAllLines(FileObject fileObject) {
        // or fileObject.getCharContent(false) <- will return CharSeq
        try {
            return fileObject.getCharContent(false).toString();
        } catch (IOException e) {
            throw new SourceParsingException(e);
        }
    }

    @NotNull
    private TypeDeclaration getTypeDeclaration(CtTypeReference<?> reference) {
        List<TypeDeclaration> typeParameters = reference.getActualTypeArguments().stream()
                .map(ref -> TypeDeclaration.rawDeclaration(Type.fromFullName(ref.getQualifiedName())))
                .collect(Collectors.toList());
        return TypeDeclaration.parameterizedDeclaration(Type.fromFullName(reference.getQualifiedName()), typeParameters);
    }
}
