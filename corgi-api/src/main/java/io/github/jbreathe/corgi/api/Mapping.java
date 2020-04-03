package io.github.jbreathe.corgi.api;

import org.jetbrains.annotations.NotNull;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.SOURCE)
@Target(ElementType.METHOD)
public @interface Mapping {
    @NotNull
    String init() default "";

    @NotNull
    String read() default "";

    @NotNull
    String write() default "";
}
