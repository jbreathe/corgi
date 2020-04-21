package io.github.jbreathe.corgi.mapper.model;

import io.github.jbreathe.corgi.mapper.model.core.Field;
import io.github.jbreathe.corgi.mapper.model.core.Type;
import io.github.jbreathe.corgi.mapper.model.core.Types;
import io.github.jbreathe.corgi.mapper.util.CaseUtil;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Any class or interface with getters and setters.
 * In case of "class", this class also may have fields and default constructor.
 */
public final class BeanStruct implements Struct {
    private static final String SETTER_NAME_FORMAT = "set%s";
    private static final String GETTER_NAME_FORMAT = "get%s";
    private static final String PRIMITIVE_BOOLEAN_GETTER_NAME_FORMAT = "is%s";

    private final Type type;
    private final DefaultConstructor defaultConstructor;
    private final List<Field> fields;
    private final Map<String, Getter> getters;
    private final Map<String, Setter> setters;

    private BeanStruct(Type type, DefaultConstructor defaultConstructor, List<Field> fields, Map<String, Getter> getters, Map<String, Setter> setters) {
        this.type = type;
        this.defaultConstructor = defaultConstructor;
        this.fields = fields;
        this.getters = getters;
        this.setters = setters;
    }

    static Struct create(Type type, DefaultConstructor defaultConstructor, List<Field> fields, List<Getter> getters, List<Setter> setters) {
        Map<String, Getter> gettersMap = getters.stream().collect(Collectors.toMap(Getter::getName, g -> g));
        Map<String, Setter> settersMap = setters.stream().collect(Collectors.toMap(Setter::getName, s -> s));
        return new BeanStruct(type, defaultConstructor, fields, gettersMap, settersMap);
    }

    @NotNull
    @Override
    public Type getType() {
        return type;
    }

    @Nullable
    @Override
    public DefaultConstructor getDefaultConstructor() {
        return defaultConstructor;
    }

    @NotNull
    @Override
    public List<Field> getFields() {
        return fields;
    }

    @Nullable
    @Override
    public Getter findGetter(Field field) {
        return getters.get(getterName(field));
    }

    @Nullable
    @Override
    public Setter findSetter(Field field) {
        return setters.get(setterName(field));
    }

    private String setterName(Field field) {
        return String.format(SETTER_NAME_FORMAT, CaseUtil.toUpperCamel(field.getName()));
    }

    private String getterName(Field field) {
        if (Types.PRIMITIVE_BOOLEAN.equals(field.getTypeDeclaration().getType())) {
            return String.format(PRIMITIVE_BOOLEAN_GETTER_NAME_FORMAT, CaseUtil.toUpperCamel(field.getName()));
        } else {
            return String.format(GETTER_NAME_FORMAT, CaseUtil.toUpperCamel(field.getName()));
        }
    }
}
