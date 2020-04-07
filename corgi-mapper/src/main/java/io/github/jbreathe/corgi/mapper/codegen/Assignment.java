package io.github.jbreathe.corgi.mapper.codegen;

import io.github.jbreathe.corgi.mapper.model.core.TypeDeclaration;

import java.util.stream.Collectors;

public final class Assignment implements Expression {
    private static final String ASSIGNMENT_TEMPLATE = "%s %s = %s";

    private final VarReference varReference;
    private final Expression expression;

    public Assignment(VarReference varReference, Expression expression) {
        this.varReference = varReference;
        this.expression = expression;
    }

    @Override
    public TypeDeclaration getResultType() {
        return varReference.getResultType();
    }

    @Override
    public String asCode() {
        TypeDeclaration typeDeclaration = varReference.getResultType();
        StringBuilder code = new StringBuilder(typeDeclaration.getType().getSimpleName());
        if (!typeDeclaration.getTypeParameters().isEmpty()) {
            code.append(typeDeclaration.getTypeParameters().stream()
                    .map(tp -> tp.getType().getSimpleName())
                    .collect(Collectors.joining(", ", "<", ">")));
        }
        return String.format(ASSIGNMENT_TEMPLATE, code, varReference.asCode(), expression.asCode());
    }
}
