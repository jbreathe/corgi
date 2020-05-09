# corgi

**Corgi** is an annotation processor for mappings in Java.

Available for Java 8 and higher.

## Usage example

With **corgi**, you can customize the way you initialize an object, read fields values or write values to fields
with a help of *@Init*, *@Read* and *@Write* annotations.
Just mark the method that will implement custom logic with one of these annotations.

**Note**:
There is one difference, depending on what Java version is used.
For Java 8 you should use **default** modifier for custom method.
For Java 9 and higher you should use **private** modifier (so none of the custom methods will be publicly visible).

### Example of custom initializing
To initialize an object, **corgi** uses constructor without parameters by default.
If you want to change this behaviour, you can use *@Init* annotation (see examples below).

#### Own initializer
**Java 9 and higher**
```java
import io.github.jbreathe.corgi.api.*;

@Mapper
public interface MyMapper {
    @Mapping(init = "initMyEntity")
    MyEntity initExample(MyDto dto);

    @Init
    private MyEntity initMyEntity() {
        return MyEntityFactory.create();
    }
}
```
For *Java 8* just replace **private** with **default** in the example above.

#### Own initializer with parameters

```java
import io.github.jbreathe.corgi.api.*;

@Mapper
public interface MyMapper {
    @Mapping(init = "initMyEntityWithParameters")
    MyEntity initWithParametersExample(@Producer MyDto dto, Class<?> clazz);

    @Init
    private MyEntity initMyEntityWithParameters(Class<?> clazz) {
        return MyEntityFactory.create(clazz);
    }
}
```
For *Java 8* just replace **private** with **default** in the example above.

### Example of custom reading
To read the value from an object field, **corgi** uses getter by default.
If you want to change this behaviour, you can use *@Read* annotation (see example below).

```java
import io.github.jbreathe.corgi.api.*;

@Mapper
public interface MyMapper {
    @Mapping(read = "getFromMap")
    MyEntity readExample(Map<String, String> map);

    @Read
    private String getFromMap(@Producer Map<String, String> map, @FieldName String key) {
        return map.get(key);
    }
}
```
For *Java 8* just replace **private** with **default** in the example above.

### Example of custom writing
To write the value to an object field, **corgi** uses setter by default.
If you want to change this behaviour, you can use *@Write* annotation (see example below).

```java
import io.github.jbreathe.corgi.api.*;

import java.util.*;

@Mapper
public interface MyMapper {
    @Mapping(init = "initMap", write = "putToMap")
    @FieldsSource(MyEntity.class)
    Map<String, Object> writeExample(MyEntity entity);

    @Init
    private Map<String, Object> initMap() {
        return new HashMap<>();
    }

    @Write
    private void putToMap(@Consumer Map<String, Object> map, @FieldName String key, @ReadResult Object value) {
        map.put(key, value);
    }
}
```
@FieldsSource explicitly defines the source of the field information.

For Java 8 just replace private with default in the example above.

### Examples of pre-conditions

```java
import io.github.jbreathe.corgi.api.*;

import java.util.*;

@Mapper
public interface MyMapper {
    @Mapping(preCondition = "containsKey", read = "getFromMap")
    MyEntity preConditionExample(Map<String, String> map);

    @PreCondition
    private boolean containsKey(@Producer Map<String, String> map, @FieldName String name) {
        return map.containsKey(name);
    }

    @Read
    private String getFromMap(@Producer Map<String, String> map, @FieldName String key) {
        return map.get(key);
    }
}
```
or
```java
import io.github.jbreathe.corgi.api.*;

@Mapper
public interface MyMapper {
    @Mapping(preCondition = "isNotNull")
    MyEntity preConditionExample(MyDto dto);

    @PreCondition
    private boolean isNotNull(@ReadResult Object result) {
        return result != null;
    }
}
```

### Records support

Simple example of mapper using Record (Java 14 preview feature):
```java
import io.github.jbreathe.corgi.api.*;

@Mapper
public interface MyMapper {
    @Mapping
    @FieldsSource(MyRecord.class)
    MyEntity recordExample(MyRecord myRecord);
}
```

### Getting instance of mapper

```java
import jbreathe.corgi.api.MapperFactory;

public class App {
    public static void main(String[] args) {
        MyMapper mapper = MapperFactory.getMapper(MyMapper.class);
    }
}
```

### How to build

You will need:
- Java 14;
- Maven (3.3.1 or later).

And that's it!

Use Maven to test or build a project. For example, run ```mvn clean test``` to run tests.

**Note:** If you're using Intellij IDEA, add ```--enable-preview``` to Maven -> Runner -> VM Options.
