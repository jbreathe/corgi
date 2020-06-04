package io.github.jbreathe.corgi.mapper;

import org.intellij.lang.annotations.Language;
import org.joor.CompileOptions;
import org.joor.Reflect;
import org.junit.jupiter.api.Test;

class CorgiProcessorTest {
    @Test
    void mapTwoStructsTest() {
        @Language("java")
        String code = """
                package org.example;
                import io.github.jbreathe.corgi.api.*;
                import java.math.BigDecimal;
                @Mapper
                interface MyMapper {
                    @Mapping
                    MyEntity map(MyDto dto);
                    class MyEntity {
                        private String name;
                        private BigDecimal amount;
                        void setName(String name) {
                            this.name = name;
                        }
                        void setAmount(BigDecimal amount) {
                            this.amount = amount;
                        }
                    }
                    class MyDto {
                        private String name;
                        private BigDecimal amount;
                        String getName() {
                            return name;
                        }
                        BigDecimal getAmount() {
                            return amount;
                        }
                    }
                }
                """;
        CompileOptions compileOptions = new CompileOptions().processors(new CorgiProcessor())
                .options("--release", "9");
        Reflect.compile(
                "org.example.MyMapper",
                code,
                compileOptions);
    }
}
