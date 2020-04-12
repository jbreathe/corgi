package io.github.jbreathe.corgi.mapper;

import javax.annotation.processing.Messager;
import javax.lang.model.element.Element;
import javax.tools.Diagnostic;

public final class Logger {
    private final Messager messager;

    public Logger(Messager messager) {
        this.messager = messager;
    }

    public void error(String message) {
        messager.printMessage(Diagnostic.Kind.ERROR, message);
    }

    public void error(String message, Element element) {
        messager.printMessage(Diagnostic.Kind.ERROR, message, element);
    }
}
