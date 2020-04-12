package io.github.jbreathe.corgi.mapper.model;

import io.github.jbreathe.corgi.api.Consumer;
import io.github.jbreathe.corgi.api.FieldName;
import io.github.jbreathe.corgi.api.FieldsSource;
import io.github.jbreathe.corgi.api.Init;
import io.github.jbreathe.corgi.api.Mapper;
import io.github.jbreathe.corgi.api.Mapping;
import io.github.jbreathe.corgi.api.Producer;
import io.github.jbreathe.corgi.api.Read;
import io.github.jbreathe.corgi.api.ReadResult;
import io.github.jbreathe.corgi.api.Write;
import io.github.jbreathe.corgi.mapper.model.core.Annotation;

import java.util.Set;

public final class ApiAnnotations {
    public static final Annotation FIELD_NAME = new Annotation(FieldName.class.getName());
    public static final Annotation CONSUMER = new Annotation(Consumer.class.getName());
    public static final Annotation READ_RESULT = new Annotation(ReadResult.class.getName());

    public static final Set<String> NAMES = Set.of(
            Consumer.class.getName(),
            FieldName.class.getName(),
            FieldsSource.class.getName(),
            Init.class.getName(),
            Mapper.class.getName(),
            Mapping.class.getName(),
            Producer.class.getName(),
            Read.class.getName(),
            ReadResult.class.getName(),
            Write.class.getName()
    );

    private ApiAnnotations() {
    }
}
