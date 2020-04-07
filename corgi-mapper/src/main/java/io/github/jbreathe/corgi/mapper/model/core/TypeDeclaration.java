package io.github.jbreathe.corgi.mapper.model.core;

import java.util.Collections;
import java.util.List;
import java.util.Objects;

public final class TypeDeclaration {
    private final Type type;
    private final List<TypeDeclaration> typeParameters;

    private TypeDeclaration(Type type, List<TypeDeclaration> typeParameters) {
        this.type = type;
        this.typeParameters = typeParameters;
    }

    public static TypeDeclaration rawDeclaration(Type type) {
        return new TypeDeclaration(type, Collections.emptyList());
    }

    public static TypeDeclaration parameterizedDeclaration(Type type, List<TypeDeclaration> typeParameters) {
        return new TypeDeclaration(type, typeParameters);
    }

    public Type getType() {
        return type;
    }

    public List<TypeDeclaration> getTypeParameters() {
        return typeParameters;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        TypeDeclaration that = (TypeDeclaration) o;
        return type.equals(that.type) &&
                typeParameters.equals(that.typeParameters);
    }

    @Override
    public int hashCode() {
        return Objects.hash(type, typeParameters);
    }

    @Override
    public String toString() {
        return "TypeDeclaration{" +
                "type=" + type +
                ", typeParameters=" + typeParameters +
                '}';
    }

}
