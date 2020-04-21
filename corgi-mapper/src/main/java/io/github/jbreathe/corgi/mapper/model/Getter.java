package io.github.jbreathe.corgi.mapper.model;

import io.github.jbreathe.corgi.mapper.codegen.CodeGenException;
import io.github.jbreathe.corgi.mapper.codegen.Expression;
import io.github.jbreathe.corgi.mapper.codegen.MethodCall;
import io.github.jbreathe.corgi.mapper.model.core.Type;
import io.github.jbreathe.corgi.mapper.model.core.TypeDeclaration;

/**
 * Read accessor without parameters.
 */
public final class Getter {
    private static final String WRONG_CONSUMER_TYPE = "The 'consumer' [%s] passed to the setter '%s' doesn't match consumer's declared type";

    private final Type consumerType;
    private final String name;
    private final TypeDeclaration resultType;

    private Getter(Type consumerType, String name, TypeDeclaration resultType) {
        this.name = name;
        this.consumerType = consumerType;
        this.resultType = resultType;
    }

    static Getter createGetter(Type consumerType, String name, TypeDeclaration resultType) {
        return new Getter(consumerType, name, resultType);
    }

    public MethodCall generateCall(Expression consumer) {
        if (!consumerType.equals(consumer.getResultType().getType())) {
            throw new CodeGenException(String.format(WRONG_CONSUMER_TYPE, consumer.asCode(), this));
        }
        return MethodCall.noArgsCall(name, resultType, consumer);
    }

    public String getName() {
        return name;
    }

    @Override
    public String toString() {
        return "Getter{" +
                "consumerType=" + consumerType +
                ", name='" + name + '\'' +
                ", resultType=" + resultType +
                '}';
    }
}
