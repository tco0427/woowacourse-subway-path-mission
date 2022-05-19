package wooteco.subway.domain.vo;

import java.util.Objects;

public class LineColor {

    private final String color;

    private LineColor(String color) {
        this.color = color;
    }

    public static LineColor of(String color) {
        validateArgument(color);

        return new LineColor(color);
    }

    private static void validateArgument(String color) {
        validateBlank(color);
        validateLength(color);
    }

    private static void validateBlank(String color) {
        if (color.isBlank()) {
            throw new IllegalArgumentException("노선의 색이 공백일 수 없습니다.");
        }
    }

    private static void validateLength(String color) {
        if (color.length() > 20) {
            throw new IllegalArgumentException("노선의 색이 20자 보다 클 수 없습니다.");
        }
    }

    public String getColor() {
        return color;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        LineColor lineColor = (LineColor) o;
        return Objects.equals(color, lineColor.color);
    }

    @Override
    public int hashCode() {
        return Objects.hash(color);
    }
}
