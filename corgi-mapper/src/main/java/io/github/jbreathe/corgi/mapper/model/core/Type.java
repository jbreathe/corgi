package io.github.jbreathe.corgi.mapper.model.core;

import org.jetbrains.annotations.NotNull;

import java.util.Objects;

public final class Type {
    private final String qualifiedName;
    private final String simpleName;
    private final String thePackage;
    private final boolean primitive;

    private Type(String qualifiedName, String simpleName, String thePackage, boolean primitive) {
        this.qualifiedName = qualifiedName;
        this.simpleName = simpleName;
        this.thePackage = thePackage;
        this.primitive = primitive;
    }

    /**
     * Factory method to create {@code Type} from fully qualified class name
     *
     * @param qualifiedName fully qualified class name
     * @return type
     */
    public static Type fromFullName(@NotNull String qualifiedName) {
        if (qualifiedName.contains(".")) {
            int lastDotPosition = qualifiedName.lastIndexOf('.');
            return new Type(qualifiedName, qualifiedName.substring(lastDotPosition + 1),
                    qualifiedName.substring(0, lastDotPosition), false);
        } else {
            return new Type(qualifiedName, qualifiedName, "", false);
        }
    }

    public static Type primitive(@NotNull String name) {
        return new Type(name, name, "", true);
    }

    public String getQualifiedName() {
        return qualifiedName;
    }

    public String getSimpleName() {
        return simpleName;
    }

    public String getPackage() {
        return thePackage;
    }

    public boolean isPrimitive() {
        return primitive;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Type type = (Type) o;
        return qualifiedName.equals(type.qualifiedName);
    }

    @Override
    public int hashCode() {
        return Objects.hash(qualifiedName);
    }

    @Override
    public String toString() {
        return "Type{" +
                "qualifiedName='" + qualifiedName + '\'' +
                '}';
    }
}
