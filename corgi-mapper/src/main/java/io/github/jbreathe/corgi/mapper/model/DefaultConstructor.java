package io.github.jbreathe.corgi.mapper.model;

import io.github.jbreathe.corgi.mapper.codegen.ConstructorCall;
import io.github.jbreathe.corgi.mapper.model.core.Type;
import io.github.jbreathe.corgi.mapper.model.core.TypeDeclaration;

public final class DefaultConstructor {
    private final Type consumerType;

    DefaultConstructor(Type consumerType) {
        this.consumerType = consumerType;
    }

    public ConstructorCall generateCall() {
        return ConstructorCall.defaultCall(TypeDeclaration.rawDeclaration(consumerType));
    }
}
