package wooteco.subway.domain;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.tuple;

import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import wooteco.subway.domain.path.JgraphtPathGenerator;
import wooteco.subway.domain.path.Path;

class PathTest {

    private final static Line line1 = new Line(1L, "2호선", "bg-green-600");
    private final static Line line2 = new Line(2L, "신분당선", "bg-red-600");

    private final static Station station1 = new Station(1L, "A");
    private final static Station station2 = new Station(2L, "B");
    private final static Station station3 = new Station(3L, "C");
    private final static Station station4 = new Station(4L, "D");
    private final static Station station5 = new Station(5L, "E");
    private final static Station station6 = new Station(6L, "F");
    private final static Station station7 = new Station(7L, "G");

    private final static List<Section> SAMPLE_SECTIONS = List.of(
            new Section(1L, line1, station1, station2, 1),
            new Section(2L, line1, station2, station5, 2),
            new Section(3L, line1, station5, station6, 2),
            new Section(4L, line1, station6, station7, 1),
            new Section(5L, line2, station2, station3, 1),
            new Section(6L, line2, station3, station4, 1),
            new Section(7L, line2, station4, station6, 1)
    );

    private final static List<Section> ANOTHER_SAMPLE_SECTIONS = List.of(
            new Section(1L, line1, station1, station2, 1),
            new Section(2L, line1, station2, station5, 2),
            new Section(3L, line1, station5, station7, 2),
            new Section(4L, line1, station7, station6, 1),
            new Section(5L, line2, station2, station4, 1),
            new Section(6L, line2, station4, station3, 1),
            new Section(7L, line2, station3, station7, 1)
    );

    @DisplayName("한 노선에서 구간과 역 정보를 통해 최단 경로를 구할 수 있다.")
    @Test
    public void getShortestPath() {
        // given
        List<Section> sections = new ArrayList<>();
        sections.add(new Section(1L, line1, station4, station5, 3));
        sections.add(new Section(2L, line1, station1, station2, 3));
        sections.add(new Section(3L, line1, station3, station4, 4));
        sections.add(new Section(4L, line1, station2, station3, 4));

        final JgraphtPathGenerator pathGenerator = new JgraphtPathGenerator();
        final Path path = pathGenerator.generatePath(sections, station1, station5);

        // when
        List<Station> shortestPath = path.getShortestPath();
        final int weight = path.getShortestPathWeight();

        // then
        assertThat(shortestPath).containsExactly(station1, station2, station3, station4, station5);
        assertThat(weight).isEqualTo(14);
    }

    @DisplayName("여러 노선이 존재할 때 구간과 역 정보를 통해 최단 경로를 구할 수 있다.")
    @Test
    public void getShortestPath2() {
        // given
        final JgraphtPathGenerator pathGenerator = new JgraphtPathGenerator();
        final Path path = pathGenerator.generatePath(SAMPLE_SECTIONS, station1, station7);

        // when
        List<Station> shortestPath = path.getShortestPath();
        final int weight = path.getShortestPathWeight();

        // then
        assertThat(shortestPath).containsExactly(station1, station2, station3, station4, station6, station7);
        assertThat(weight).isEqualTo(5);
    }

    @DisplayName("여러 노선이 존재할 때 stationId가 랜덤이어도 최단 경로를 구할 수 있다.")
    @Test
    public void getShortestPath3() {
        // given
        final JgraphtPathGenerator pathGenerator = new JgraphtPathGenerator();
        final Path path = pathGenerator.generatePath(ANOTHER_SAMPLE_SECTIONS, station1, station6);

        // when
        List<Station> shortestPath = path.getShortestPath();
        final int weight = path.getShortestPathWeight();

        // then
        assertThat(shortestPath).containsExactly(station1, station2, station4, station3, station7, station6);
        assertThat(weight).isEqualTo(5);
    }

    @DisplayName("최단 거리의 구간(section) 정보를 얻을 수 있다.")
    @Test
    public void getShortestEdge() {
        // given
        final JgraphtPathGenerator pathGenerator = new JgraphtPathGenerator();
        final Path path = pathGenerator.generatePath(ANOTHER_SAMPLE_SECTIONS, station1, station6);

        // when
        final List<Section> shortestEdge = path.getShortestEdge();

        // then
        assertThat(shortestEdge).hasSize(5)
                .extracting("id", "upStation", "downStation")
                .containsExactly(
                        tuple(1L, station1, station2),
                        tuple(5L, station2, station4),
                        tuple(6L, station4, station3),
                        tuple(7L, station3, station7),
                        tuple(4L, station7, station6)
                );
    }
}
