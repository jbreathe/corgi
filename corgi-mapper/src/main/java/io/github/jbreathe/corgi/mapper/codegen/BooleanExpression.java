package io.github.jbreathe.corgi.mapper.codegen;

import io.github.jbreathe.corgi.mapper.model.core.Type;
import io.github.jbreathe.corgi.mapper.model.core.TypeDeclaration;
import io.github.jbreathe.corgi.mapper.model.core.Types;

public class BooleanExpression implements Expression {
    private final Expression delegate;

    private BooleanExpression(MethodCall delegate) {
        this.delegate = delegate;
    }

    public static BooleanExpression fromMethodCall(MethodCall methodCall) {
        Type type = methodCall.getResultType().getType();
        if (!Types.PRIMITIVE_BOOLEAN.equals(type) && !Types.BOOLEAN.equals(type)) {
            throw new CodeGenException(methodCall.asCode() + " is not a boolean method");
        }
        return new BooleanExpression(methodCall);
    }

    public IfStatement wrapWithIf() {
        return new IfStatement(this);
    }

    @Override
    public TypeDeclaration getResultType() {
        return delegate.getResultType();
    }

    @Override
    public String asCode() {
        return delegate.asCode();
    }
}
