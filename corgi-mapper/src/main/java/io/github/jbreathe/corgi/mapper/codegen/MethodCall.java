package io.github.jbreathe.corgi.mapper.codegen;

import io.github.jbreathe.corgi.mapper.model.core.TypeDeclaration;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public final class MethodCall implements Expression {
    private static final String METHOD_FORMAT = "%s.%s(%s)";

    private final String name;
    private final TypeDeclaration resultType;
    private final Expression consumer;
    private final List<? extends Expression> arguments;

    private MethodCall(String name, TypeDeclaration resultType, Expression consumer, List<? extends Expression> arguments) {
        this.name = name;
        this.resultType = resultType;
        this.consumer = consumer;
        this.arguments = arguments;
    }

    public static MethodCall noArgsCall(String name, TypeDeclaration resultType, Expression consumer) {
        return new MethodCall(name, resultType, consumer, Collections.emptyList());
    }

    public static MethodCall callWithArgs(String name, TypeDeclaration resultType, Expression consumer, List<? extends Expression> arguments) {
        return new MethodCall(name, resultType, consumer, arguments);
    }

    @Override
    public TypeDeclaration getResultType() {
        return resultType;
    }

    @Override
    public String asCode() {
        return String.format(METHOD_FORMAT, consumer.asCode(), name,
                arguments.stream().map(Expression::asCode).collect(Collectors.joining(", ")));
    }
}
