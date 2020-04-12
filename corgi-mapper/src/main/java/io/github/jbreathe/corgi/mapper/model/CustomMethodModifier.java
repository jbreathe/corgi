package io.github.jbreathe.corgi.mapper.model;

public enum CustomMethodModifier {
    PRIVATE("private"),
    DEFAULT("default");

    private final String name;

    CustomMethodModifier(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }
}
