package io.github.jbreathe.corgi.mapper.source;

import io.github.jbreathe.corgi.mapper.model.core.TypeDeclaration;
import io.github.jbreathe.corgi.mapper.model.core.VarDeclaration;

import java.util.List;

public final class SourceMethod {
    private final String name;
    private final TypeDeclaration resultType;
    private final List<VarDeclaration> parameters;
    private final String body;

    SourceMethod(String name, TypeDeclaration resultType, List<VarDeclaration> parameters, String body) {
        this.name = name;
        this.resultType = resultType;
        this.parameters = parameters;
        this.body = body;
    }

    public String getName() {
        return name;
    }

    public TypeDeclaration getResultType() {
        return resultType;
    }

    public List<VarDeclaration> getParameters() {
        return parameters;
    }

    public String getBody() {
        return body;
    }
}
