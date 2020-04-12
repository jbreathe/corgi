package io.github.jbreathe.corgi.mapper.typemap;

import io.github.jbreathe.corgi.mapper.codegen.Expression;
import io.github.jbreathe.corgi.mapper.codegen.MethodCall;
import io.github.jbreathe.corgi.mapper.model.core.Type;
import io.github.jbreathe.corgi.mapper.model.core.TypeDeclaration;

final class NoParamsInstanceMethod implements MappingFunction {
    private final String name;
    private final Type consumerType;
    private final TypeDeclaration resultType;

    NoParamsInstanceMethod(Type consumerType, String name, TypeDeclaration resultType) {
        this.name = name;
        this.consumerType = consumerType;
        this.resultType = resultType;
    }

    @Override
    public Expression apply(Expression expression) {
        assert consumerType.equals(expression.getResultType().getType());
        return MethodCall.noArgsCall(name, resultType, expression);
    }
}
