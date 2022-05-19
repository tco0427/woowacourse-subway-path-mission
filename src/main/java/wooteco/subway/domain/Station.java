package wooteco.subway.domain;

import java.util.Objects;
import wooteco.subway.domain.vo.Name;

public class Station {
    private final Long id;
    private final Name name;

    public Station(String name) {
        this(null, name);
    }

    public Station(Long id, String name) {
        this.id = id;
        this.name = Name.of(name);
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name.getName();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Station)) {
            return false;
        }
        Station station = (Station) o;
        return Objects.equals(name, station.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name);
    }
}
