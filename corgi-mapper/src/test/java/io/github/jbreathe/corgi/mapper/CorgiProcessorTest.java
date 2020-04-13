package io.github.jbreathe.corgi.mapper;

import com.google.testing.compile.Compilation;
import com.google.testing.compile.Compiler;
import com.google.testing.compile.JavaFileObjects;
import org.intellij.lang.annotations.Language;
import org.junit.jupiter.api.Test;

import javax.tools.JavaFileObject;

import static org.junit.jupiter.api.Assertions.assertEquals;

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
        JavaFileObject input = JavaFileObjects.forSourceString("org.example.MyMapper", code);
        CorgiProcessor processor = new CorgiProcessor();
        Compiler javac = Compiler.javac()
//                .withOptions("--source", "8", "--target", "8")
                .withOptions("--release", "9")
                .withProcessors(processor);
        Compilation compilation = javac.compile(input);
        System.out.println(compilation.errors());
        assertEquals(0, compilation.errors().size());
    }
}
