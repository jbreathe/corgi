package io.github.jbreathe.corgi.mapper.typemap;

import io.github.jbreathe.corgi.mapper.codegen.Expression;

/**
 * Code construction that accept only one parameter:
 * - constructor with only one parameter (new BigDecimal(String) for example);
 * - static method with only one parameter (String#valueOf(Object) for example);
 * - instance method without parameters (Integer#longValue for example).
 */
public interface MappingFunction {
    Expression apply(Expression expression);
}
