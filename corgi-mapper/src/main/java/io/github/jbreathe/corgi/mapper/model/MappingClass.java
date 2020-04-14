package io.github.jbreathe.corgi.mapper.model;

import io.github.jbreathe.corgi.mapper.model.core.Type;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Map;

public final class MappingClass {
    private final Type type;
    private final List<MappingMethod> mappingMethods;
    /**
     * {@link InitMethod}'s by aliases (value of {@link io.github.jbreathe.corgi.api.Init#value()}).
     */
    private final Map<String, InitMethod> initMethods;
    /**
     * {@link ReadMethod}'s by aliases (value of {@link io.github.jbreathe.corgi.api.Read#value()}).
     */
    private final Map<String, ReadMethod> readMethods;
    /**
     * {@link WriteMethod}'s by aliases (value of {@link io.github.jbreathe.corgi.api.Write#value()}).
     */
    private final Map<String, WriteMethod> writeMethods;
    /**
     * {@link PreConditionMethod}'s by aliases (value of {@link io.github.jbreathe.corgi.api.PreCondition#value()}).
     */
    private final Map<String, PreConditionMethod> preConditionMethods;

    MappingClass(Type type, List<MappingMethod> mappingMethods, Map<String, InitMethod> initMethods, Map<String, ReadMethod> readMethods, Map<String, WriteMethod> writeMethods, Map<String, PreConditionMethod> preConditionMethods) {
        this.type = type;
        this.mappingMethods = mappingMethods;
        this.initMethods = initMethods;
        this.readMethods = readMethods;
        this.writeMethods = writeMethods;
        this.preConditionMethods = preConditionMethods;
    }

    public Type getType() {
        return type;
    }

    public List<MappingMethod> getMappingMethods() {
        return mappingMethods;
    }

    @Nullable
    public InitMethod findInitMethod(String name) {
        return initMethods.get(name);
    }

    @Nullable
    public ReadMethod findReadMethod(String name) {
        return readMethods.get(name);
    }

    @Nullable
    public WriteMethod findWriteMethod(String name) {
        return writeMethods.get(name);
    }

    @Nullable
    public PreConditionMethod findPreConditionMethod(String name) {
        return preConditionMethods.get(name);
    }
}
