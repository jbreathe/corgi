package io.github.jbreathe.corgi.mapper.model.core;

public final class Field {
    private final TypeDeclaration typeDeclaration;
    private final String name;

    public Field(TypeDeclaration typeDeclaration, String name) {
        this.typeDeclaration = typeDeclaration;
        this.name = name;
    }

    public TypeDeclaration getTypeDeclaration() {
        return typeDeclaration;
    }

    public String getName() {
        return name;
    }
}
