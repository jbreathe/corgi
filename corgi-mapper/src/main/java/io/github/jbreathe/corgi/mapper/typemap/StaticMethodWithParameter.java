package io.github.jbreathe.corgi.mapper.typemap;

import io.github.jbreathe.corgi.mapper.codegen.Expression;
import io.github.jbreathe.corgi.mapper.codegen.StaticCall;
import io.github.jbreathe.corgi.mapper.model.core.Type;
import io.github.jbreathe.corgi.mapper.model.core.TypeDeclaration;
import io.github.jbreathe.corgi.mapper.model.core.VarDeclaration;

import java.util.List;

final class StaticMethodWithParameter implements MappingFunction {
    private final String name;
    private final Type consumerType;
    private final TypeDeclaration resultType;
    private final VarDeclaration parameter;

    StaticMethodWithParameter(String name, Type consumerType, TypeDeclaration resultType, VarDeclaration parameter) {
        this.name = name;
        this.consumerType = consumerType;
        this.resultType = resultType;
        this.parameter = parameter;
    }

    @Override
    public Expression apply(Expression expression) {
        assert parameter.getTypeDeclaration().equals(expression.getResultType());
        return StaticCall.callWithArgs(name, resultType, consumerType, List.of(expression));
    }
}
