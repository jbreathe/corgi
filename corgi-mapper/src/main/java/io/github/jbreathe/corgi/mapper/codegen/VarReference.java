package io.github.jbreathe.corgi.mapper.codegen;

import io.github.jbreathe.corgi.mapper.model.core.TypeDeclaration;

public final class VarReference implements Expression {
    private final TypeDeclaration typeDeclaration;
    private final String name;

    public VarReference(TypeDeclaration typeDeclaration, String name) {
        this.typeDeclaration = typeDeclaration;
        this.name = name;
    }

    public Assignment initWith(Expression expression) {
        return new Assignment(this, expression);
    }

    public ResultStatement asResultStatement() {
        return new ResultStatement(this);
    }

    @Override
    public TypeDeclaration getResultType() {
        return typeDeclaration;
    }

    @Override
    public String asCode() {
        return name;
    }
}
