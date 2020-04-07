package io.github.jbreathe.corgi.mapper.codegen;

import io.github.jbreathe.corgi.mapper.model.core.TypeDeclaration;

public interface Expression {
    TypeDeclaration getResultType();

    String asCode();
}
