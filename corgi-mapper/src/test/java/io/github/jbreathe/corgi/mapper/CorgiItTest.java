package io.github.jbreathe.corgi.mapper;

import io.github.jbreathe.corgi.api.MapperFactory;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

public class CorgiItTest {

    @Test
    void shouldMapDifferentFieldWithFieldMapping() {
        TestWithRenameFieldMapper testMapper = MapperFactory.getMapper(TestWithRenameFieldMapper.class);
        assertNotNull(testMapper);
        final String bValue = "B";
        final TestWithRenameFieldMapper.AClass mapped = testMapper.map(new TestWithRenameFieldMapper.BClass() {
            @Override
            public String getB() {
                return bValue;
            }
        });
        assertEquals(bValue, mapped.getA());
    }
}
