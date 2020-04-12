package io.github.jbreathe.corgi.mapper.codegen;

import io.github.jbreathe.corgi.mapper.model.core.TypeDeclaration;
import io.github.jbreathe.corgi.mapper.model.core.Types;

public final class StringValue implements Expression {
    private static final String STRING_VALUE_FORMAT = "\"%s\"";

    private final TypeDeclaration resultType = TypeDeclaration.rawDeclaration(Types.STRING);
    private final String value;

    public StringValue(String value) {
        this.value = value;
    }

    @Override
    public TypeDeclaration getResultType() {
        return resultType;
    }

    @Override
    public String asCode() {
        return String.format(STRING_VALUE_FORMAT, value);
    }
}
