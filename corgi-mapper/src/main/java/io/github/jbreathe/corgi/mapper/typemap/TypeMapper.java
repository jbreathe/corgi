package io.github.jbreathe.corgi.mapper.typemap;

import io.github.jbreathe.corgi.mapper.codegen.Expression;
import io.github.jbreathe.corgi.mapper.model.core.Type;
import io.github.jbreathe.corgi.mapper.model.core.TypeDeclaration;
import io.github.jbreathe.corgi.mapper.model.core.Types;

import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

public final class TypeMapper {
    private static final Map<Type, Type> PRIMITIVE_TO_OBJECT;
    private static final Map<Type, Type> OBJECT_TO_PRIMITIVE;

    static {
        PRIMITIVE_TO_OBJECT = new HashMap<>();
        PRIMITIVE_TO_OBJECT.put(Types.PRIMITIVE_BOOLEAN, Types.BOOLEAN);
        PRIMITIVE_TO_OBJECT.put(Types.PRIMITIVE_CHAR, Types.CHAR);
        PRIMITIVE_TO_OBJECT.put(Types.PRIMITIVE_BYTE, Types.BYTE);
        PRIMITIVE_TO_OBJECT.put(Types.PRIMITIVE_INT, Types.INT);
        PRIMITIVE_TO_OBJECT.put(Types.PRIMITIVE_SHORT, Types.SHORT);
        PRIMITIVE_TO_OBJECT.put(Types.PRIMITIVE_LONG, Types.LONG);
        PRIMITIVE_TO_OBJECT.put(Types.PRIMITIVE_FLOAT, Types.FLOAT);
        PRIMITIVE_TO_OBJECT.put(Types.PRIMITIVE_DOUBLE, Types.DOUBLE);
        OBJECT_TO_PRIMITIVE = PRIMITIVE_TO_OBJECT.entrySet().stream().collect(Collectors.toMap(Map.Entry::getValue, Map.Entry::getKey));
    }

    private TypeMapper() {
    }

    public static Expression tryMap(Expression input, TypeDeclaration toType) {
        if (isImplicitlyCastable(input.getResultType(), toType)) {
            return input;
        }
        MappingFunction mappingFunction = MappingsFactory.getMappingFunction(input.getResultType().getType(), toType.getType());
        return mappingFunction.apply(input);
    }

    private static boolean isImplicitlyCastable(TypeDeclaration fromDeclaration, TypeDeclaration toDeclaration) {
        if (fromDeclaration.equals(toDeclaration)) {
            return true;
        }
        Type fromType = fromDeclaration.getType();
        Type toType = toDeclaration.getType();
        // any reference object to java.lang.Object
        if (!fromType.isPrimitive() && Types.OBJECT.equals(toType)) {
            return true;
        }
        // boxing/unboxing
        return (PRIMITIVE_TO_OBJECT.containsKey(fromType) && PRIMITIVE_TO_OBJECT.get(fromType).equals(toType))
                || (OBJECT_TO_PRIMITIVE.containsKey(fromType) && OBJECT_TO_PRIMITIVE.get(fromType).equals(toType));
    }
}
