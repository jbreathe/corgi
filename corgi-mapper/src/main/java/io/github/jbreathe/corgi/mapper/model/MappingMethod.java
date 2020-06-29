package io.github.jbreathe.corgi.mapper.model;

import io.github.jbreathe.corgi.api.FieldMapping;
import io.github.jbreathe.corgi.api.Mapping;
import io.github.jbreathe.corgi.mapper.model.core.TypeDeclaration;
import io.github.jbreathe.corgi.mapper.model.core.VarDeclaration;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

public final class MappingMethod {
    private final String name;
    private final TypeDeclaration resultType;
    private final List<VarDeclaration> parameters;
    private final Map<String, VarDeclaration> parametersMap;
    private final String initName;
    private final String readName;
    private final String writeName;
    private final String preConditionName;
    private final Struct producer;
    private final VarDeclaration producerVar;
    private final Struct consumer;
    private final VarDeclaration consumerVar;
    private final Struct fieldsSource;
    private final Map<String, String> fieldNameMap;

    private MappingMethod(String name, TypeDeclaration resultType,
                          List<VarDeclaration> parameters, Map<String, VarDeclaration> parametersMap,
                          String initName, String readName, String writeName,
                          String preConditionName, Struct producer, VarDeclaration producerVar,
                          Struct consumer, VarDeclaration consumerVar,
                          Struct fieldsSource, Map<String, String> fieldNameMap) {
        this.name = name;
        this.resultType = resultType;
        this.parameters = parameters;
        this.parametersMap = parametersMap;
        this.initName = initName;
        this.readName = readName;
        this.writeName = writeName;
        this.preConditionName = preConditionName;
        this.producer = producer;
        this.producerVar = producerVar;
        this.consumer = consumer;
        this.consumerVar = consumerVar;
        this.fieldsSource = fieldsSource;
        this.fieldNameMap = fieldNameMap;
    }

    static Builder fromNameAndResultType(String name, TypeDeclaration resultType) {
        return new Builder(name, resultType);
    }

    public String getName() {
        return name;
    }

    public TypeDeclaration getResultType() {
        return resultType;
    }

    public List<VarDeclaration> getParameters() {
        return parameters;
    }

    public boolean withCustomInit() {
        return !initName.isEmpty();
    }

    public boolean withCustomRead() {
        return !readName.isEmpty();
    }

    public boolean withCustomWrite() {
        return !writeName.isEmpty();
    }

    public boolean withCustomPreCondition() {
        return !preConditionName.isEmpty();
    }

    public String getInitName() {
        return initName;
    }

    public String getReadName() {
        return readName;
    }

    public String getWriteName() {
        return writeName;
    }

    public String getPreConditionName() {
        return preConditionName;
    }

    public Struct getProducer() {
        return producer;
    }

    public VarDeclaration getProducerVar() {
        return producerVar;
    }

    public Struct getConsumer() {
        return consumer;
    }

    public VarDeclaration getConsumerVar() {
        return consumerVar;
    }

    public Struct getFieldsSource() {
        return fieldsSource;
    }

    public Map<String, String> getFieldNameMap() {
        return fieldNameMap;
    }

    public Optional<String> findFieldByName(String name) {
        return Optional.ofNullable(fieldNameMap.get(name));
    }

    @Nullable
    public VarDeclaration findParameter(String name) {
        return parametersMap.get(name);
    }

    static class Builder {
        private final String name;
        private final TypeDeclaration resultType;
        private List<VarDeclaration> parameters;
        private String initName;
        private String readName;
        private String writeName;
        private String preConditionName;
        private Struct producer;
        private VarDeclaration producerVar;
        private Struct consumer;
        private VarDeclaration consumerVar;
        private Struct fieldsSource;
        private Map<String, String> fieldNameMap;

        private Builder(String name, TypeDeclaration resultType) {
            this.name = name;
            this.resultType = resultType;
        }

        Builder parameters(List<VarDeclaration> parameters) {
            this.parameters = parameters;
            return this;
        }

        Builder customMethodsFromMapping(@NotNull Mapping mapping) {
            this.initName = mapping.init();
            this.readName = mapping.read();
            this.writeName = mapping.write();
            this.preConditionName = mapping.preCondition();
            this.fieldNameMap = Arrays
                    .stream(mapping.fieldMappings())
                    .collect(Collectors.toUnmodifiableMap(FieldMapping::target, FieldMapping::source));
            return this;
        }

        Builder producer(Struct producer) {
            this.producer = producer;
            return this;
        }

        Builder producerVar(VarDeclaration producerVar) {
            this.producerVar = producerVar;
            return this;
        }

        Builder consumer(Struct consumer) {
            this.consumer = consumer;
            return this;
        }

        Builder consumerVar(VarDeclaration consumerVar) {
            this.consumerVar = consumerVar;
            return this;
        }

        Builder fieldsSource(Struct fieldsSource) {
            this.fieldsSource = fieldsSource;
            return this;
        }

        MappingMethod build() {
            Map<String, VarDeclaration> parametersMap = parameters.stream().collect(Collectors.toMap(VarDeclaration::getName, v -> v));
            return new MappingMethod(
                    name,
                    resultType,
                    parameters,
                    parametersMap,
                    initName,
                    readName,
                    writeName,
                    preConditionName,
                    producer,
                    producerVar,
                    consumer,
                    consumerVar,
                    fieldsSource,
                    fieldNameMap);
        }
    }
}
