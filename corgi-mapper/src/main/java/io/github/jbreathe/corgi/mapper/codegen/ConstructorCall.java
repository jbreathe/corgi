package io.github.jbreathe.corgi.mapper.codegen;

import io.github.jbreathe.corgi.mapper.model.core.TypeDeclaration;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public final class ConstructorCall implements Expression {
    private static final String CONSTRUCTOR_FORMAT = "new %s(%s)";
    private static final String PARAMETERIZED_CONSTRUCTOR_FORMAT = "new %s<>(%s)";

    private final TypeDeclaration resultType;
    private final List<? extends Expression> arguments;

    private ConstructorCall(TypeDeclaration resultType, List<? extends Expression> arguments) {
        this.resultType = resultType;
        this.arguments = arguments;
    }

    public static ConstructorCall defaultCall(TypeDeclaration resultType) {
        return new ConstructorCall(resultType, Collections.emptyList());
    }

    public static ConstructorCall callWithArgs(TypeDeclaration resultType, List<? extends Expression> arguments) {
        return new ConstructorCall(resultType, arguments);
    }

    @Override
    public TypeDeclaration getResultType() {
        return resultType;
    }

    @Override
    public String asCode() {
        String className = resultType.getType().getSimpleName();
        if (!resultType.getTypeParameters().isEmpty()) {
            return String.format(PARAMETERIZED_CONSTRUCTOR_FORMAT, className,
                    arguments.stream().map(Expression::asCode).collect(Collectors.joining(", ")));
        } else {
            return String.format(CONSTRUCTOR_FORMAT, className,
                    arguments.stream().map(Expression::asCode).collect(Collectors.joining(", ")));
        }
    }
}
