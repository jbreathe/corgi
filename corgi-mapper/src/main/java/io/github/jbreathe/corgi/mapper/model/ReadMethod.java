package io.github.jbreathe.corgi.mapper.model;

import io.github.jbreathe.corgi.mapper.codegen.CodeGenException;
import io.github.jbreathe.corgi.mapper.codegen.Expression;
import io.github.jbreathe.corgi.mapper.codegen.MethodCall;
import io.github.jbreathe.corgi.mapper.codegen.StringValue;
import io.github.jbreathe.corgi.mapper.codegen.VarReference;
import io.github.jbreathe.corgi.mapper.model.core.Field;
import io.github.jbreathe.corgi.mapper.model.core.Type;
import io.github.jbreathe.corgi.mapper.model.core.TypeDeclaration;
import io.github.jbreathe.corgi.mapper.model.core.VarDeclaration;
import io.github.jbreathe.corgi.mapper.typemap.TypeMapper;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Representation of method annotated with {@link io.github.jbreathe.corgi.api.Read}.
 */
public final class ReadMethod {
    private static final String PARAMETER_NOT_FOUND = "Parameter '%s' of @Read method '%s' must be declared in the associated @Mapping method '%s'";

    private final VarReference thisReference;
    private final String name;
    private final TypeDeclaration resultType;
    private final List<VarDeclaration> parameters;

    ReadMethod(Type consumerType, String name, TypeDeclaration resultType, List<VarDeclaration> parameters) {
        this.thisReference = new VarReference(TypeDeclaration.rawDeclaration(consumerType), "this");
        this.name = name;
        this.resultType = resultType;
        this.parameters = parameters;
    }

    public MethodCall generateCall(MappingMethod mappingMethod, Field field) {
        List<Expression> arguments = collectArguments(mappingMethod, field);
        return MethodCall.callWithArgs(name, resultType, thisReference, arguments);
    }

    @NotNull
    private List<Expression> collectArguments(MappingMethod mappingMethod, Field field) {
        return parameters.stream().map(p -> {
            if (p.isAnnotationPresents(ApiAnnotations.FIELD_NAME)) {
                return new StringValue(field.getName());
            } else if (p.isAnnotationPresents(ApiAnnotations.PRODUCER)) {
                VarDeclaration consumerVar = mappingMethod.getProducerVar();
                return TypeMapper.tryMap(consumerVar.createReference(), p.getTypeDeclaration());
            } else {
                VarDeclaration parameter = mappingMethod.findParameter(p.getName());
                if (parameter == null) {
                    throw new CodeGenException(String.format(PARAMETER_NOT_FOUND, p, name, mappingMethod.getName()));
                }
                return TypeMapper.tryMap(parameter.createReference(), p.getTypeDeclaration());
            }
        }).collect(Collectors.toList());
    }
}
