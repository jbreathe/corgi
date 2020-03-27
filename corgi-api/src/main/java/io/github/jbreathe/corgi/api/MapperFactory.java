package io.github.jbreathe.corgi.api;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

public class MapperFactory {
    public static <T> T getMapper(Class<T> mapperClass) {
        String implClassName = mapperClass.getName() + "Impl";
        try {
            Class<?> implClass = Class.forName(implClassName);
            Constructor<?> defaultConstructor = implClass.getDeclaredConstructor();
            return mapperClass.cast(defaultConstructor.newInstance());
        } catch (ClassNotFoundException e) {
            throw new RuntimeException("No such class '" + implClassName + "'", e);
        } catch (NoSuchMethodException e) {
            throw new RuntimeException("Default constructor for '" + implClassName + "' not found", e);
        } catch (IllegalAccessException | InstantiationException | InvocationTargetException e) {
            throw new RuntimeException(e);
        }
    }
}
