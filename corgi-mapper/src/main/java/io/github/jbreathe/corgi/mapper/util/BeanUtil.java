package io.github.jbreathe.corgi.mapper.util;

import io.github.jbreathe.corgi.mapper.model.core.Field;
import io.github.jbreathe.corgi.mapper.model.core.Types;

public final class BeanUtil {
    private static final String SETTER_NAME_FORMAT = "set%s";
    private static final String GETTER_NAME_FORMAT = "get%s";
    private static final String PRIMITIVE_BOOLEAN_GETTER_NAME_FORMAT = "is%s";

    private BeanUtil() {
    }

    public static String setterName(Field field) {
        return String.format(SETTER_NAME_FORMAT, CaseUtil.toUpperCamel(field.getName()));
    }

    public static String getterName(Field field) {
        if (Types.PRIMITIVE_BOOLEAN.equals(field.getTypeDeclaration().getType())) {
            return String.format(PRIMITIVE_BOOLEAN_GETTER_NAME_FORMAT, CaseUtil.toUpperCamel(field.getName()));
        } else {
            return String.format(GETTER_NAME_FORMAT, CaseUtil.toUpperCamel(field.getName()));
        }
    }
}
