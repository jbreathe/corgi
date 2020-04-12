package io.github.jbreathe.corgi.mapper.model;

import io.github.jbreathe.corgi.mapper.model.core.Type;
import io.github.jbreathe.corgi.mapper.model.core.TypeDeclaration;
import org.jetbrains.annotations.NotNull;

import javax.lang.model.element.Element;
import javax.lang.model.type.DeclaredType;
import javax.lang.model.type.TypeKind;
import javax.lang.model.type.WildcardType;
import javax.lang.model.util.SimpleTypeVisitor9;
import javax.lang.model.util.Types;
import java.util.List;
import java.util.stream.Collectors;

public final class TypeParametersVisitor extends SimpleTypeVisitor9<List<TypeDeclaration>, Void> {
    private final Types types;

    public TypeParametersVisitor(Types types) {
        this.types = types;
    }

    @Override
    public List<TypeDeclaration> visitDeclared(DeclaredType t, Void aVoid) {
        return t.getTypeArguments().stream().map(arg -> {
            Type type;
            if (arg.getKind() == TypeKind.WILDCARD) {
                type = wildcardType((WildcardType) arg);
            } else {
                type = typeFromElement(types.asElement(arg));
            }
            return TypeDeclaration.rawDeclaration(type);
        }).collect(Collectors.toList());
    }

    @NotNull
    private Type wildcardType(WildcardType wildcardType) {
        return Type.fromFullName(JavaModelUtil.wildcardTypeName(wildcardType));
    }

    @NotNull
    private Type typeFromElement(Element element) {
        return Type.fromFullName(JavaModelUtil.typeName(element.asType()));
    }
}
