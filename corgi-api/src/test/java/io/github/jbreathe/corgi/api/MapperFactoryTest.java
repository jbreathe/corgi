package io.github.jbreathe.corgi.api;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertNotNull;

class MapperFactoryTest {
    @Test
    void getMapperTest() {
        SomeMapper mapper = MapperFactory.getMapper(SomeMapper.class);
        assertNotNull(mapper);
    }
}
