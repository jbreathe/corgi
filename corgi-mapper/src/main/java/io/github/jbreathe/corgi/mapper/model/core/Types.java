package io.github.jbreathe.corgi.mapper.model.core;

public final class Types {
    // primitives
    public static final Type PRIMITIVE_VOID = Type.primitive("void");
    public static final Type PRIMITIVE_BOOLEAN = Type.primitive("boolean");
    public static final Type PRIMITIVE_CHAR = Type.primitive("char");
    public static final Type PRIMITIVE_BYTE = Type.primitive("byte");
    public static final Type PRIMITIVE_INT = Type.primitive("int");
    public static final Type PRIMITIVE_SHORT = Type.primitive("short");
    public static final Type PRIMITIVE_LONG = Type.primitive("long");
    public static final Type PRIMITIVE_FLOAT = Type.primitive("float");
    public static final Type PRIMITIVE_DOUBLE = Type.primitive("double");
    // java.lang
    public static final Type OBJECT = Type.fromFullName("java.lang.Object");
    public static final Type STRING = Type.fromFullName("java.lang.String");
    // primitive wrappers
    public static final Type VOID = Type.fromFullName("java.lang.Void");
    public static final Type BOOLEAN = Type.fromFullName("java.lang.Boolean");
    public static final Type CHAR = Type.fromFullName("java.lang.Character");
    public static final Type BYTE = Type.fromFullName("java.lang.Byte");
    public static final Type INT = Type.fromFullName("java.lang.Integer");
    public static final Type SHORT = Type.fromFullName("java.lang.Short");
    public static final Type LONG = Type.fromFullName("java.lang.Long");
    public static final Type FLOAT = Type.fromFullName("java.lang.Float");
    public static final Type DOUBLE = Type.fromFullName("java.lang.Double");
    // java.math
    public static final Type BIG_DECIMAL = Type.fromFullName("java.math.BigDecimal");

    private Types() {
    }
}
