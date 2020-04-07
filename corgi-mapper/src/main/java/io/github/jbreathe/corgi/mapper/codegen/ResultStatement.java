package io.github.jbreathe.corgi.mapper.codegen;

import io.github.jbreathe.corgi.mapper.model.core.TypeDeclaration;

public final class ResultStatement implements Expression {
    private static final String RESULT_STATEMENT_TEMPLATE = "return %s";

    private final VarReference varReference;

    public ResultStatement(VarReference varReference) {
        this.varReference = varReference;
    }

    @Override
    public TypeDeclaration getResultType() {
        return varReference.getResultType();
    }

    @Override
    public String asCode() {
        return String.format(RESULT_STATEMENT_TEMPLATE, varReference.asCode());
    }
}
