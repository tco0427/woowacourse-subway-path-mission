package wooteco.subway.domain;

import wooteco.subway.exception.IllegalSectionException;

public class Section {

    private final Long id;
    private final Line line;
    private final Station upStation;
    private final Station downStation;
    private final int distance;

    public Section(Line line, Station upStation, Station downStation, int distance) {
        this(null, line, upStation, downStation, distance);
    }

    public Section(Long id, Line line, Station upStation, Station downStation, int distance) {
        validateSection(upStation, downStation, distance);

        this.id = id;
        this.line = line;
        this.upStation = upStation;
        this.downStation = downStation;
        this.distance = distance;
    }

    private void validateSection(Station upStation, Station downStation, int distance) {
        checkNegativeDistance(distance);
        checkSameStation(upStation, downStation);
    }

    private void checkNegativeDistance(int distance) {
        if (distance <= 0) {
            throw new IllegalSectionException("구간 사이의 거리는 0보다 작거나 같을 수 없습니다.");
        }
    }

    private void checkSameStation(Station upStation, Station downStation) {
        if (upStation.equals(downStation)) {
            throw new IllegalSectionException("구간의 두 역이 같을 수 없습니다.");
        }
    }

    public boolean containsStation(Section section) {
        return upStation.equals(section.getDownStation()) || downStation.equals(section.getUpStation());
    }

    public boolean isFork(Section section) {
        return upStation.equals(section.getUpStation()) || downStation.equals(section.getDownStation());
    }

    public boolean isSameSection(Section section) {
        return (upStation.equals(section.getUpStation()) || upStation.equals(section.getDownStation()))
                && (downStation.equals(section.getUpStation()) || downStation.equals(section.getDownStation()));
    }

    public boolean isSameUpStation(Station station) {
        return getUpStation().equals(station);
    }

    public boolean isSameDownStation(Station station) {
        return getDownStation().equals(station);
    }

    public Long getId() {
        return id;
    }

    public Line getLine() {
        return line;
    }

    public Station getUpStation() {
        return upStation;
    }

    public Station getDownStation() {
        return downStation;
    }

    public int getDistance() {
        return distance;
    }
}
