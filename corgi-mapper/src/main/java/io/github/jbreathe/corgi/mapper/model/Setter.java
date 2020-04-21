package io.github.jbreathe.corgi.mapper.model;

import io.github.jbreathe.corgi.mapper.codegen.CodeGenException;
import io.github.jbreathe.corgi.mapper.codegen.Expression;
import io.github.jbreathe.corgi.mapper.codegen.MethodCall;
import io.github.jbreathe.corgi.mapper.model.core.Type;
import io.github.jbreathe.corgi.mapper.model.core.TypeDeclaration;
import io.github.jbreathe.corgi.mapper.model.core.Types;
import io.github.jbreathe.corgi.mapper.model.core.VarDeclaration;

import java.util.List;

/**
 * Write accessor with one parameter.
 */
public final class Setter {
    private static final String WRONG_CONSUMER_TYPE = "The 'consumer' [%s] passed to the setter '%s' doesn't match consumer's declared type";
    private static final String WRONG_ARGUMENT_TYPE = "Argument [%s] passed to the setter '%s' doesn't match argument's declared type";

    private final Type consumerType;
    private final String name;
    private final VarDeclaration parameter;

    private Setter(Type consumerType, String name, VarDeclaration parameter) {
        this.consumerType = consumerType;
        this.name = name;
        this.parameter = parameter;
    }

    static Setter createSetter(Type consumerType, String name, VarDeclaration parameter) {
        return new Setter(consumerType, name, parameter);
    }

    public MethodCall generateCall(Expression consumer, Expression argument) {
        if (!consumerType.equals(consumer.getResultType().getType())) {
            throw new CodeGenException(String.format(WRONG_CONSUMER_TYPE, consumer.asCode(), this));
        }
        if (!parameter.getTypeDeclaration().equals(argument.getResultType())) {
            throw new CodeGenException(String.format(WRONG_ARGUMENT_TYPE, argument.asCode(), this));
        }
        return MethodCall.callWithArgs(name, TypeDeclaration.rawDeclaration(Types.VOID), consumer, List.of(argument));
    }

    public String getName() {
        return name;
    }

    @Override
    public String toString() {
        return "Setter{" +
                "consumerType=" + consumerType +
                ", name='" + name + '\'' +
                ", parameter=" + parameter +
                '}';
    }
}
