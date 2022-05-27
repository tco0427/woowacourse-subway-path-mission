package wooteco.subway.domain.vo;

import java.util.Objects;

public class Name {

    private static final int MAX_NAME_LENGTH = 255;

    private final String name;

    private Name(String name) {
        this.name = name;
    }

    public static Name of(String name) {
        validateArgument(name);

        return new Name(name);
    }

    private static void validateArgument(String name) {
        validateBlank(name);
        validateLength(name);
    }

    private static void validateBlank(String name) {
        if (name.isBlank()) {
            throw new IllegalArgumentException("이름이 공백일 수 없습니다.");
        }
    }

    private static void validateLength(String name) {
        if (name.length() > MAX_NAME_LENGTH) {
            throw new IllegalArgumentException("이름은 255자 보다 클 수 없습니다.");
        }
    }

    public String getName() {
        return name;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Name that = (Name) o;
        return Objects.equals(name, that.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name);
    }
}
