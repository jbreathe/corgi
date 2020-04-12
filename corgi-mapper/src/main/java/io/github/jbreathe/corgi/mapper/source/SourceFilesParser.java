package io.github.jbreathe.corgi.mapper.source;

import io.github.jbreathe.corgi.mapper.model.core.Type;

public interface SourceFilesParser {
    SourceFile parse(Type type);
}
