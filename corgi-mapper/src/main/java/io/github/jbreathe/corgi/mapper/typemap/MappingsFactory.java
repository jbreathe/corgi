package io.github.jbreathe.corgi.mapper.typemap;

import io.github.jbreathe.corgi.mapper.codegen.CodeGenException;
import io.github.jbreathe.corgi.mapper.model.core.Type;
import io.github.jbreathe.corgi.mapper.model.core.TypeDeclaration;
import io.github.jbreathe.corgi.mapper.model.core.Types;
import io.github.jbreathe.corgi.mapper.model.core.VarDeclaration;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

final class MappingsFactory {
    private static final Map<Pair, MappingFunction> MAPPING_FUNCTIONS;

    static {
        MAPPING_FUNCTIONS = new HashMap<>();
        MAPPING_FUNCTIONS.put(Pair.of(Types.OBJECT, Types.STRING),
                new StaticMethodWithParameter("valueOf", Types.STRING,
                        TypeDeclaration.rawDeclaration(Types.STRING),
                        VarDeclaration.declarationWithoutAnnotations(TypeDeclaration.rawDeclaration(Types.OBJECT), "obj")));
        MAPPING_FUNCTIONS.put(Pair.of(Types.STRING, Types.BIG_DECIMAL),
                new ConstructorWithParameter(Types.BIG_DECIMAL,
                        VarDeclaration.declarationWithoutAnnotations(TypeDeclaration.rawDeclaration(Types.STRING), "str")));
    }

    static MappingFunction getMappingFunction(Type from, Type to) {
        Pair typePair = new Pair(from, to);
        if (!MAPPING_FUNCTIONS.containsKey(typePair)) {
            throw new CodeGenException("Mapping for '" + from + "' and '" + to + "' not found");
        }
        return MAPPING_FUNCTIONS.get(typePair);
    }

    private static class Pair {
        private final Type first;
        private final Type second;

        private Pair(Type first, Type second) {
            this.first = first;
            this.second = second;
        }

        private static Pair of(Type first, Type second) {
            return new Pair(first, second);
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            Pair pair = (Pair) o;
            return first.equals(pair.first) &&
                    second.equals(pair.second);
        }

        @Override
        public int hashCode() {
            return Objects.hash(first, second);
        }
    }
}
