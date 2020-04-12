package io.github.jbreathe.corgi.mapper.model;

import javax.lang.model.element.AnnotationMirror;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.TypeElement;
import javax.lang.model.element.VariableElement;
import javax.lang.model.type.TypeMirror;
import javax.lang.model.type.WildcardType;

/**
 * Utility for classes under {@link javax.lang.model.element} package.
 */
final class JavaModelUtil {
    private JavaModelUtil() {
    }

    static boolean methodHasModifier(ExecutableElement element, CustomMethodModifier methodModifier) {
        Modifier modifier = Modifier.valueOf(methodModifier.name());
        return element.getModifiers().contains(modifier);
    }

    static String methodName(ExecutableElement methodElement) {
        return methodElement.getSimpleName().toString();
    }

    static String variableName(VariableElement variableElement) {
        return variableElement.getSimpleName().toString();
    }

    static String variableTypeName(VariableElement variableElement) {
        return typeName(variableElement.asType());
    }

    static String elementTypeName(TypeElement element) {
        return typeName(element.asType());
    }

    static String typeName(TypeMirror typeMirror) {
        return typeMirror.toString();
    }

    static String wildcardTypeName(WildcardType wildcardType) {
        return wildcardType.toString();
    }

    static String annotationTypeName(AnnotationMirror annotationMirror) {
        return annotationMirror.getAnnotationType().toString();
    }
}
