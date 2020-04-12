package io.github.jbreathe.corgi.mapper.source;

import io.github.jbreathe.corgi.mapper.ProcessingException;

public final class SourceParsingException extends ProcessingException {
    SourceParsingException(String message) {
        super(message);
    }

    SourceParsingException(Throwable cause) {
        super(cause);
    }
}
