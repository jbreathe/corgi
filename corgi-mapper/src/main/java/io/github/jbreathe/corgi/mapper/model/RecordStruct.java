package io.github.jbreathe.corgi.mapper.model;

import io.github.jbreathe.corgi.mapper.model.core.Field;
import io.github.jbreathe.corgi.mapper.model.core.Type;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

public final class RecordStruct implements Struct {
    private final Type type;
    private final List<Field> fields;
    private final Map<String, Getter> getters;

    private RecordStruct(Type type, List<Field> fields, Map<String, Getter> getters) {
        this.type = type;
        this.fields = fields;
        this.getters = getters;
    }

    static Struct create(Type type, List<Field> fields, List<Getter> getters) {
        Map<String, Getter> gettersMap = getters.stream().collect(Collectors.toMap(Getter::getName, g -> g));
        return new RecordStruct(type, fields, gettersMap);
    }

    @NotNull
    @Override
    public Type getType() {
        return type;
    }

    @Nullable
    @Override
    public DefaultConstructor getDefaultConstructor() {
        return null;
    }

    @NotNull
    @Override
    public List<Field> getFields() {
        return fields;
    }

    @Override
    public @NotNull Optional<Field> getField(String name) {
        return fields.stream().filter(field -> field.getName().equals(name)).findFirst();
    }

    @Nullable
    @Override
    public Getter findGetter(Field field) {
        return getters.get(field.getName());
    }

    @Nullable
    @Override
    public Setter findSetter(Field field) {
        return null;
    }
}
