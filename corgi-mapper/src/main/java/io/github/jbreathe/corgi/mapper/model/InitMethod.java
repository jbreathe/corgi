package io.github.jbreathe.corgi.mapper.model;

import io.github.jbreathe.corgi.mapper.codegen.CodeGenException;
import io.github.jbreathe.corgi.mapper.codegen.Expression;
import io.github.jbreathe.corgi.mapper.codegen.MethodCall;
import io.github.jbreathe.corgi.mapper.codegen.VarReference;
import io.github.jbreathe.corgi.mapper.model.core.Type;
import io.github.jbreathe.corgi.mapper.model.core.TypeDeclaration;
import io.github.jbreathe.corgi.mapper.model.core.VarDeclaration;
import io.github.jbreathe.corgi.mapper.typemap.TypeMapper;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Representation of method annotated with {@link io.github.jbreathe.corgi.api.Init}.
 */
public final class InitMethod {
    private static final String PARAMETER_NOT_FOUND = "Parameter '%s' of @Init method '%s' must be declared in the associated @Mapping method '%s'";

    private final VarReference thisReference;
    private final String name;
    private final TypeDeclaration resultType;
    private final List<VarDeclaration> parameters;

    InitMethod(Type consumerType, String name, TypeDeclaration resultType, List<VarDeclaration> parameters) {
        this.thisReference = new VarReference(TypeDeclaration.rawDeclaration(consumerType), "this");
        this.name = name;
        this.resultType = resultType;
        this.parameters = parameters;
    }

    public MethodCall generateCall(MappingMethod mappingMethod) {
        List<Expression> arguments = collectArguments(mappingMethod);
        return MethodCall.callWithArgs(name, resultType, thisReference, arguments);
    }

    private List<Expression> collectArguments(MappingMethod mappingMethod) {
        return parameters.stream().map(p -> {
            VarDeclaration parameter = mappingMethod.findParameter(p.getName());
            if (parameter == null) {
                throw new CodeGenException(String.format(PARAMETER_NOT_FOUND, p, name, mappingMethod.getName()));
            }
            return TypeMapper.tryMap(parameter.createReference(), p.getTypeDeclaration());
        }).collect(Collectors.toList());
    }
}
