package io.github.jbreathe.corgi.mapper.typemap;

import io.github.jbreathe.corgi.mapper.codegen.ConstructorCall;
import io.github.jbreathe.corgi.mapper.codegen.Expression;
import io.github.jbreathe.corgi.mapper.model.core.Type;
import io.github.jbreathe.corgi.mapper.model.core.TypeDeclaration;
import io.github.jbreathe.corgi.mapper.model.core.VarDeclaration;

import java.util.List;

final class ConstructorWithParameter implements MappingFunction {
    private final Type consumerType;
    private final VarDeclaration parameter;

    ConstructorWithParameter(Type consumerType, VarDeclaration parameter) {
        this.consumerType = consumerType;
        this.parameter = parameter;
    }

    @Override
    public Expression apply(Expression expression) {
        assert parameter.getTypeDeclaration().equals(expression.getResultType());
        return ConstructorCall.callWithArgs(TypeDeclaration.rawDeclaration(consumerType), List.of(expression));
    }
}
