package io.github.jbreathe.corgi.mapper;

public class ProcessingException extends RuntimeException {
    public ProcessingException(String message) {
        super(message);
    }

    public ProcessingException(Throwable cause) {
        super(cause);
    }
}
