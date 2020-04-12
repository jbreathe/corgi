package io.github.jbreathe.corgi.mapper.model.core;

import java.util.Objects;

public final class Annotation {
    private final String className;

    public Annotation(String className) {
        this.className = className;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Annotation that = (Annotation) o;
        return className.equals(that.className);
    }

    @Override
    public int hashCode() {
        return Objects.hash(className);
    }
}
