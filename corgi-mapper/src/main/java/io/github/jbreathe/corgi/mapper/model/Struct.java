package io.github.jbreathe.corgi.mapper.model;

import io.github.jbreathe.corgi.mapper.model.core.Field;
import io.github.jbreathe.corgi.mapper.model.core.Type;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public interface Struct {
    @NotNull
    Type getType();

    @Nullable
    DefaultConstructor getDefaultConstructor();

    /**
     * Fields of this struct.
     *
     * @return list of fields or empty list, if there is no fields in this struct
     */
    @NotNull
    List<Field> getFields();

    @Nullable
    Getter findGetter(Field field);

    @Nullable
    Setter findSetter(Field field);
}
