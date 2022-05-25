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

    private final static List<Section> SAMPLE_SECTIONS = List.of(
            new Section(1L, 1L, 1L, 2L, 1),
            new Section(2L, 1L, 2L, 5L, 2),
            new Section(3L, 1L, 5L, 6L, 2),
            new Section(4L, 1L, 6L, 7L, 1),
            new Section(5L, 2L, 2L, 3L, 1),
            new Section(6L, 2L, 3L, 4L, 1),
            new Section(7L, 2L, 4L, 6L, 1)
    );

    private final static List<Section> ANOTHER_SAMPLE_SECTIONS = List.of(
            new Section(1L, 1L, 1L, 2L, 1),
            new Section(2L, 1L, 2L, 5L, 2),
            new Section(3L, 1L, 5L, 7L, 2),
            new Section(4L, 1L, 7L, 6L, 1),
            new Section(5L, 2L, 2L, 4L, 1),
            new Section(6L, 2L, 4L, 3L, 1),
            new Section(7L, 2L, 3L, 7L, 1)
    );

    @DisplayName("한 노선에서 구간과 역 정보를 통해 최단 경로를 구할 수 있다.")
    @Test
    public void getShortestPath() {
        // given
        List<Section> sections = new ArrayList<>();
        sections.add(new Section(1L, 1L, 4L, 5L, 3));
        sections.add(new Section(2L, 1L, 1L, 2L, 3));
        sections.add(new Section(3L, 1L, 3L, 4L, 4));
        sections.add(new Section(4L, 1L, 2L, 3L, 4));

        final JgraphtPathGenerator pathGenerator = new JgraphtPathGenerator();
        final Path path = pathGenerator.generatePath(sections, 1L, 5L);

        // when
        List<Long> shortestPath = path.getShortestPath();
        final int weight = path.getShortestPathWeight();

        // then
        assertThat(shortestPath).containsExactly(1L, 2L, 3L, 4L, 5L);
        assertThat(weight).isEqualTo(14);
    }

    @DisplayName("여러 노선이 존재할 때 구간과 역 정보를 통해 최단 경로를 구할 수 있다.")
    @Test
    public void getShortestPath2() {
        // given
        final JgraphtPathGenerator pathGenerator = new JgraphtPathGenerator();
        final Path path = pathGenerator.generatePath(SAMPLE_SECTIONS, 1L, 7L);

        // when
        List<Long> shortestPath = path.getShortestPath();
        final int weight = path.getShortestPathWeight();

        // then
        assertThat(shortestPath).containsExactly(1L, 2L, 3L, 4L, 6L, 7L);
        assertThat(weight).isEqualTo(5);
    }

    @DisplayName("여러 노선이 존재할 때 stationId가 랜덤이어도 최단 경로를 구할 수 있다.")
    @Test
    public void getShortestPath3() {
        // given
        final JgraphtPathGenerator pathGenerator = new JgraphtPathGenerator();
        final Path path = pathGenerator.generatePath(ANOTHER_SAMPLE_SECTIONS, 1L, 6L);

        // when
        List<Long> shortestPath = path.getShortestPath();
        final int weight = path.getShortestPathWeight();

        // then
        assertThat(shortestPath).containsExactly(1L, 2L, 4L, 3L, 7L, 6L);
        assertThat(weight).isEqualTo(5);
    }

    @DisplayName("최단 거리의 구간(section) 정보를 얻을 수 있다.")
    @Test
    public void getShortestEdge() {
        // given
        final JgraphtPathGenerator pathGenerator = new JgraphtPathGenerator();
        final Path path = pathGenerator.generatePath(ANOTHER_SAMPLE_SECTIONS, 1L, 6L);

        // when
        final List<Section> shortestEdge = path.getShortestEdge();

        // then
        assertThat(shortestEdge).hasSize(5)
                .extracting("id", "upStationId", "downStationId")
                .containsExactly(
                        tuple(1L, 1L, 2L),
                        tuple(5L, 2L, 4L),
                        tuple(6L, 4L, 3L),
                        tuple(7L, 3L, 7L),
                        tuple(4L, 7L, 6L)
                );
    }
}
