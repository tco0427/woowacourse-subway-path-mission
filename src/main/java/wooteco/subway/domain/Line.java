package wooteco.subway.domain;

import java.util.Objects;
import wooteco.subway.domain.vo.LineColor;
import wooteco.subway.domain.vo.Name;

public class Line {

    private final Long id;
    private final Name name;
    private final LineColor color;
    private final int extraFare;

    public Line(String name, String color) {
        this(null, name, color);
    }

    public Line(String name, String color, int extraFare) {
        this(null, name, color, extraFare);
    }

    public Line(Long id, String name, String color) {
        this(id, name, color, 0);
    }

    public Line(Long id, String name, String color, int extraFare) {
        this.id = id;
        this.name = Name.of(name);
        this.color = LineColor.of(color);
        this.extraFare = extraFare;
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name.getName();
    }

    public String getColor() {
        return color.getColor();
    }

    public int getExtraFare() {
        return extraFare;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Line line = (Line) o;
        return Objects.equals(id, line.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
