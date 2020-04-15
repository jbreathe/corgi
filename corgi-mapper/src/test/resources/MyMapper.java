import io.github.jbreathe.corgi.api.Mapper;
import io.github.jbreathe.corgi.api.Mapping;

@Mapper
public interface MyMapper {
    @Mapping
    MyEntity map(MyDto dto);

    class MyEntity {
        String s;

        String getS() {
            return s;
        }

        void setS(String s) {
            this.s = s;
        }
    }

    class MyDto {
        String s;

        String getS() {
            return s;
        }

        void setS(String s) {
            this.s = s;
        }
    }
}
