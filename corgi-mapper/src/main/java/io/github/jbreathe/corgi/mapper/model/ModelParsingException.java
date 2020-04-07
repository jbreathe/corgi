package io.github.jbreathe.corgi.mapper.model;

import io.github.jbreathe.corgi.mapper.ProcessingException;

import javax.lang.model.element.Element;

public final class ModelParsingException extends ProcessingException {
    private final Element element;

    ModelParsingException(String message, Element element) {
        super(message);
        this.element = element;
    }

    public Element getElement() {
        return element;
    }
}
