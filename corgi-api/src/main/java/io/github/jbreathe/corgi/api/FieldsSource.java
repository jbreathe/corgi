package io.github.jbreathe.corgi.api;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

// todo
@Retention(RetentionPolicy.SOURCE)
@Target(ElementType.METHOD)
public @interface FieldsSource {
    Class<?> value();
}
