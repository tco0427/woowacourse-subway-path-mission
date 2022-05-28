package wooteco.subway.domain;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.api.Assertions.tuple;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import wooteco.subway.exception.IllegalSectionException;

class SectionsTest {

    private static final Line line1 = new Line(1L, "2호선", "bg-green-600");
    private static final Station station1 = new Station(1L, "서울대입구역");
    private static final Station station2 = new Station(2L, "낙성대역");
    private static final Station station3 = new Station(3L, "사당역");
    private static final Station station4 = new Station(4L, "교대역");
    private static final Station station5 = new Station(5L, "서초역");

    @DisplayName("새로 등록할 구간의 상행역과 하행역 중 노선에 이미 등록되어있는 역을 기준으로 새로운 구간을 추가한다.")
    @Test
    public void addNewSection() {
        // given
        final Sections sections = makeSectionsBySingleSection(line1, station2, station3, 1);

        // when & then
        final Section section = new Section(line1, station1, station2, 1);
        assertDoesNotThrow(() -> sections.add(section));
    }
    
    @DisplayName("하나의 노선에는 갈래길이 허용되지 않기 때문에 새로운 구간이 추가되기 전에 갈래길이 생기지 않도록 상행역을 기준으로 기존 구간을 변경한다.")
    @Test
    public void forkRodeSameUpStation() {
        // given
        final Sections sections = makeSectionsBySingleSection(line1, station1, station3, 7);

        // when
        final Section section = new Section(line1, station1, station2, 4);
        sections.add(section);

        // then
        assertThat(sections.getSections())
                .hasSize(2)
                .extracting("distance")
                .containsExactly(4, 3);
    }

    @DisplayName("하나의 노선에는 갈래길이 허용되지 않기 때문에 새로운 구간이 추가되기 전에 갈래길이 생기지 않도록 하행역을 기준으로 기존 구간을 변경한다.")
    @Test
    public void forkRodeSameDownStation() {
        // given
        final Sections sections = makeSectionsBySingleSection(line1, station1, station3, 7);

        // when
        final Section section = new Section(line1, station2, station3, 4);
        sections.add(section);

        // then
        assertThat(sections.getSections())
                .hasSize(2)
                .extracting("distance")
                .containsExactly(3, 4);
    }

    @DisplayName("역 사이에 새로운 역을 등록할 경우 기존 역 사이 길이보다 크거나 같으면 등록을 할 수 없다.")
    @Test
    public void checkDistance() {
        // given
        final Sections sections = makeSectionsBySingleSection(1L, line1, station1, station3, 7);

        // when & then
        final Section section = new Section(2L, line1, station1, station2, 7);
        assertThatThrownBy(() -> sections.add(section))
                        .isInstanceOf(IllegalSectionException.class);
    }

    @DisplayName("상행역과 하행역이 이미 노선에 모두 등록되어 있다면 추가할 수 없다.")
    @Test
    public void sameSection() {
        // given
        final Sections sections = makeSectionsBySingleSection(1L, line1, station1, station3, 7);

        // when & then
        final Section section = new Section(2L,line1, station1, station3, 7);
        assertThatThrownBy(() -> sections.add(section))
                .isInstanceOf(IllegalSectionException.class);
    }

    @DisplayName("상행역과 하행역 둘 중 하나도 포함되어 있지 않으면 추가할 수 없다.")
    @Test
    public void IllegalAddSection() {
        // given
        final Sections sections = new Sections(List.of(
                new Section(line1, station2, station3, 4),
                new Section(line1, station1, station2, 3)
        ));

        // when & then
        final Section section = new Section(line1, station4, station5, 7);
        assertThatThrownBy(() -> sections.add(section))
                .isInstanceOf(IllegalSectionException.class);
        assertThat(sections.getSections().size()).isEqualTo(2);
    }

    @DisplayName("Station을 받아 구간을 제거할 수 있다.")
    @Test
    public void deleteSection() {
        // given
        final Sections sections = new Sections(List.of(
                new Section(line1, station1, station2, 3),
                new Section(line1, station2, station3, 4)
        ));

        // when
        final Station station = new Station(2L, "중간역");
        sections.delete(station);

        // then
        assertThat(sections.getSections().size()).isEqualTo(1);
        final Section section = sections.getSections().get(0);
        assertThat(section.getDistance()).isEqualTo(7);
        assertThat(section.getUpStation()).isEqualTo(station1);
        assertThat(section.getDownStation()).isEqualTo(station3);
    }

    @DisplayName("구간이 하나인 노선에서 마지막 구간을 제거할 수 없다.")
    @Test
    public void IllegalDeleteSection() {
        // given
        final Sections sections = makeSectionsBySingleSection(line1, station1, station3, 7);

        // when & then
        final Station station = new Station(1L, "상행역");
        assertThatThrownBy(() -> sections.delete(station))
                .isInstanceOf(IllegalSectionException.class);
    }

    @DisplayName("첫번째 역을 삭제할 수 있다.")
    @Test
    public void deleteFirstSection() {
        // given
        final Sections sections = new Sections(List.of(
                new Section(line1, station1, station2, 7),
                new Section(line1, station2, station3, 7)
        ));

        final Station deleteStation = new Station(1L, "첫번째역");

        // when
        sections.delete(deleteStation);

        //then
        assertThat(sections.getSections().size()).isEqualTo(1);
        final Section section = sections.getSections().get(0);
        assertThat(section.getDistance()).isEqualTo(7);
        assertThat(section.getUpStation()).isEqualTo(station2);
        assertThat(section.getDownStation()).isEqualTo(station3);
    }

    @DisplayName("마지막 순서의 역을 삭제할 수 있다.")
    @Test
    public void deleteLastSection() {
        // given
        final Sections sections = new Sections(List.of(
                new Section(line1, station1, station2, 7),
                new Section(line1, station2, station3, 7)
        ));

        final Station deleteStation = new Station(3L, "마지막역");

        // when
        sections.delete(deleteStation);

        //then
        assertThat(sections.getSections().size()).isEqualTo(1);
        final Section section = sections.getSections().get(0);
        assertThat(section.getDistance()).isEqualTo(7);
        assertThat(section.getUpStation()).isEqualTo(station1);
        assertThat(section.getDownStation()).isEqualTo(station2);
    }

    @DisplayName("상행부터 하행의 순서로 정렬된 구간들을 구할 수 있다.")
    @Test
    public void sortedSection() {
        //given
        final Sections sections = new Sections(List.of(
                new Section(line1, station4, station5, 3),
                new Section(line1, station1, station2, 3),
                new Section(line1, station3, station4, 4),
                new Section(line1, station2, station3, 4)
        ));

        //when
        final List<Section> sortedSections = sections.getSections();

        //then
        assertThat(sortedSections).hasSize(4)
                .extracting("upStation", "downStation")
                .containsExactly(
                        tuple(station1, station2),
                        tuple(station2, station3),
                        tuple(station3, station4),
                        tuple(station4, station5)
                );
    }

    private Sections makeSectionsBySingleSection(Long id, Line line, Station upStation, Station downStation, int distance) {
        return new Sections(List.of(
                new Section(id, line, upStation, downStation, distance)
        ));
    }

    private Sections makeSectionsBySingleSection(Line line, Station upStation, Station downStation, int distance) {
        return new Sections(List.of(
                new Section(line, upStation, downStation, distance)
        ));
    }
}
