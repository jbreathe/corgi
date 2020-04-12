package io.github.jbreathe.corgi.mapper.codegen;

import io.github.jbreathe.corgi.mapper.model.core.Type;
import io.github.jbreathe.corgi.mapper.model.core.TypeDeclaration;

import java.util.List;
import java.util.stream.Collectors;

public final class StaticCall implements Expression {
    private static final String METHOD_FORMAT = "%s.%s(%s)";

    private final String name;
    private final Type consumerType;
    private final TypeDeclaration resultType;
    private final List<? extends Expression> arguments;

    private StaticCall(String name, TypeDeclaration resultType, Type consumerType, List<? extends Expression> arguments) {
        this.name = name;
        this.resultType = resultType;
        this.consumerType = consumerType;
        this.arguments = arguments;
    }

    public static StaticCall callWithArgs(String name, TypeDeclaration resultType, Type consumerType, List<? extends Expression> arguments) {
        return new StaticCall(name, resultType, consumerType, arguments);
    }

    @Override
    public TypeDeclaration getResultType() {
        return resultType;
    }

    @Override
    public String asCode() {
        return String.format(METHOD_FORMAT, consumerType.getSimpleName(), name,
                arguments.stream().map(Expression::asCode).collect(Collectors.joining(", ")));
    }
}
