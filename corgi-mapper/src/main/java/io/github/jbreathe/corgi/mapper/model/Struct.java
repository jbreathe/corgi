package io.github.jbreathe.corgi.mapper.model;

import io.github.jbreathe.corgi.mapper.model.core.Field;
import io.github.jbreathe.corgi.mapper.model.core.Type;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Any class or interface.
 */
public final class Struct {
    private final Type type;
    private final DefaultConstructor defaultConstructor;
    private final List<Field> fields;
    private final Map<String, Getter> getters;
    private final Map<String, Setter> setters;

    private Struct(Type type, DefaultConstructor defaultConstructor, List<Field> fields, Map<String, Getter> getters, Map<String, Setter> setters) {
        this.type = type;
        this.defaultConstructor = defaultConstructor;
        this.fields = fields;
        this.getters = getters;
        this.setters = setters;
    }

    static Struct createStruct(Type type, DefaultConstructor defaultConstructor, List<Field> fields, List<Getter> getters, List<Setter> setters) {
        Map<String, Getter> gettersMap = getters.stream().collect(Collectors.toMap(Getter::getName, g -> g));
        Map<String, Setter> settersMap = setters.stream().collect(Collectors.toMap(Setter::getName, s -> s));
        return new Struct(type, defaultConstructor, fields, gettersMap, settersMap);
    }

    public Type getType() {
        return type;
    }

    @Nullable
    public DefaultConstructor getDefaultConstructor() {
        return defaultConstructor;
    }

    public List<Field> getFields() {
        return fields;
    }

    @Nullable
    public Getter findGetter(String name) {
        return getters.get(name);
    }

    @Nullable
    public Setter findSetter(String name) {
        return setters.get(name);
    }
}
