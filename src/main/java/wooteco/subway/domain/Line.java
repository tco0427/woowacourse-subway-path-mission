package wooteco.subway.domain;

import java.util.Objects;
import wooteco.subway.domain.vo.LineColor;
import wooteco.subway.domain.vo.Name;

public class Line {

    private final Long id;
    private final Name name;
    private final LineColor color;

    public Line(String name, String color) {
        this(null, name, color);
    }

    public Line(Long id, String name, String color) {
        this.id = id;
        this.name = Name.of(name);
        this.color = LineColor.of(color);
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
