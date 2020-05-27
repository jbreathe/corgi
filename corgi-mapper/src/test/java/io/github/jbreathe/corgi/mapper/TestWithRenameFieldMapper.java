package io.github.jbreathe.corgi.mapper;

import io.github.jbreathe.corgi.api.FieldMapping;
import io.github.jbreathe.corgi.api.Mapper;
import io.github.jbreathe.corgi.api.Mapping;

@Mapper
public interface TestWithRenameFieldMapper {

    @Mapping(fieldMappings = @FieldMapping(source = "b", target = "a"))
    AClass map(BClass bClass);

    class AClass {
        private String a;

        public void setA(String a) {
            this.a = a;
        }

        public String getA() {
            return a;
        }
    }

    class BClass {
        private String b;

        public String getB() {
            return b;
        }

        public void setB(String b) {
            this.b = b;
        }
    }
}
