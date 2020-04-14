package io.github.jbreathe.corgi.mapper.codegen;

import io.github.jbreathe.corgi.mapper.model.core.TypeDeclaration;

public final class IfStatement implements Expression {
    private static final String IF_STATEMENT_TEMPLATE = "if (%s)";

    private final BooleanExpression booleanExpression;

    public IfStatement(BooleanExpression booleanExpression) {
        this.booleanExpression = booleanExpression;
    }

    @Override
    public TypeDeclaration getResultType() {
        return booleanExpression.getResultType();
    }

    @Override
    public String asCode() {
        return String.format(IF_STATEMENT_TEMPLATE, booleanExpression.asCode());
    }
}
