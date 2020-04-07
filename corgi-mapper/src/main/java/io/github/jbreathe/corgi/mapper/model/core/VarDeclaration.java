package io.github.jbreathe.corgi.mapper.model.core;

import io.github.jbreathe.corgi.mapper.codegen.VarReference;

import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;

/**
 * Either method parameter or local variable.
 */
public final class VarDeclaration {
    private final TypeDeclaration typeDeclaration;
    private final String name;
    private final Set<Annotation> annotations;

    private VarDeclaration(TypeDeclaration typeDeclaration, String name, Set<Annotation> annotations) {
        this.typeDeclaration = typeDeclaration;
        this.name = name;
        this.annotations = annotations;
    }

    public static VarDeclaration declarationWithoutAnnotations(TypeDeclaration typeDeclaration, String name) {
        return new VarDeclaration(typeDeclaration, name, Collections.emptySet());
    }

    public static VarDeclaration declarationWithAnnotations(TypeDeclaration typeDeclaration, String name, List<Annotation> annotations) {
        // replace List with Set to remove duplicates and to improve search
        Set<Annotation> hashed = new HashSet<>(annotations);
        return new VarDeclaration(typeDeclaration, name, hashed);
    }

    public VarReference createReference() {
        return new VarReference(typeDeclaration, name);
    }

    public boolean isAnnotationPresents(Annotation annotation) {
        return annotations.contains(annotation);
    }

    public String getName() {
        return name;
    }

    public TypeDeclaration getTypeDeclaration() {
        return typeDeclaration;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        VarDeclaration that = (VarDeclaration) o;
        return typeDeclaration.equals(that.typeDeclaration) &&
                name.equals(that.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(typeDeclaration, name);
    }

    @Override
    public String toString() {
        return "VarDeclaration{" +
                "typeDeclaration=" + typeDeclaration +
                ", name='" + name + '\'' +
                '}';
    }
}
