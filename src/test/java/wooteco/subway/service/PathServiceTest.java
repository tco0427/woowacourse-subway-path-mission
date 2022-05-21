package wooteco.subway.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.tuple;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.jdbc.Sql;
import wooteco.subway.dto.LineRequest;
import wooteco.subway.dto.PathResponse;
import wooteco.subway.dto.SectionRequest;
import wooteco.subway.dto.StationRequest;
import wooteco.subway.dto.StationResponse;

@SpringBootTest
@Sql("/truncate.sql")
class PathServiceTest {

    private final PathService pathService;
    private final StationService stationService;
    private final SectionService sectionService;
    private final LineService lineService;

    @Autowired
    public PathServiceTest(PathService pathService, StationService stationService, SectionService sectionService, LineService lineService) {
        this.pathService = pathService;
        this.stationService = stationService;
        this.sectionService = sectionService;
        this.lineService = lineService;
    }


    @DisplayName("출발역과 도착역의 id로 최단 경로를 조회할 수 있다.")
    @Test
    public void findPath() {
        // given
        final StationRequest stationRequest1 = new StationRequest("a");
        final StationRequest stationRequest2 = new StationRequest("b");
        final StationRequest stationRequest3 = new StationRequest("c");

        final StationResponse stationResponse1 = stationService.save(stationRequest1);
        final StationResponse stationResponse3 = stationService.save(stationRequest3);
        final StationResponse stationResponse2 = stationService.save(stationRequest2);

        final LineRequest lineRequest = new LineRequest("신분당선", "bg-red-600", stationResponse1.getId(), stationResponse3.getId(), 10);
        final Long lineId = lineService.save(lineRequest).getId();

        final SectionRequest request = new SectionRequest(stationResponse1.getId(), stationResponse2.getId(), 4);
        sectionService.save(lineId, request);

        // when
        final PathResponse response = pathService.findPath(stationResponse1.getId(), stationResponse3.getId(), 20);

        // then
        assertThat(response).extracting("distance", "fare")
                .containsExactly(10, 1250);
        assertThat(response.getStations()).hasSize(3)
                .extracting("id", "name")
                .containsExactly(
                        tuple(stationResponse1.getId(), "a"),
                        tuple(stationResponse2.getId(), "b"),
                        tuple(stationResponse3.getId(), "c")
                );
    }

    @DisplayName("추가 요금이 있는 노선을 이용할 경우 가장 높은 금액의 추가 요금이 적용된다.")
    @Test
    public void testExtraFromLine() {
        // given
        final StationRequest stationRequest1 = new StationRequest("a");
        final StationRequest stationRequest2 = new StationRequest("b");
        final StationRequest stationRequest3 = new StationRequest("c");

        final StationResponse stationResponse1 = stationService.save(stationRequest1);
        final StationResponse stationResponse2 = stationService.save(stationRequest2);
        final StationResponse stationResponse3 = stationService.save(stationRequest3);

        final LineRequest lineRequest1 = new LineRequest("분당선", "bg-yellow-600", stationResponse1.getId(), stationResponse2.getId(), 10);
        lineService.save(lineRequest1);

        final LineRequest lineRequest2 = new LineRequest("신분당선", "bg-red-600", stationResponse2.getId(), stationResponse3.getId(), 10, 900);
        lineService.save(lineRequest2);

        // when
        final PathResponse response = pathService.findPath(stationResponse1.getId(), stationResponse3.getId(), 20);

        // then
        assertThat(response).extracting("distance", "fare")
                .containsExactly(20, 2350);
        assertThat(response.getStations()).hasSize(3)
                .extracting("id", "name")
                .containsExactly(
                        tuple(stationResponse1.getId(), "a"),
                        tuple(stationResponse2.getId(), "b"),
                        tuple(stationResponse3.getId(), "c")
                );
    }

    @DisplayName("어린이는 운임에서 350원을 공제한 금액의 50%를 할인받는다.")
    @Test
    public void testChildrenFare() {
        // given
        final StationResponse stationResponse1 = stationService.save(new StationRequest("a"));
        final StationResponse stationResponse2 = stationService.save(new StationRequest("b"));

        final LineRequest lineRequest2 = new LineRequest("신분당선", "bg-red-600", stationResponse1.getId(), stationResponse2.getId(), 10);
        lineService.save(lineRequest2);

        // when
        final PathResponse response = pathService.findPath(stationResponse1.getId(), stationResponse2.getId(), 6);

        // then
        assertThat(response).extracting("distance", "fare")
                .containsExactly(10, 800);
        assertThat(response.getStations()).hasSize(2)
                .extracting("id", "name")
                .containsExactly(
                        tuple(stationResponse1.getId(), "a"),
                        tuple(stationResponse2.getId(), "b")
                );
    }

    @DisplayName("청소년은 운임에서 350원을 공제한 금액의 20%를 할인받는다.")
    @Test
    public void testTeenagerFare() {
        // given
        final StationResponse stationResponse1 = stationService.save(new StationRequest("a"));
        final StationResponse stationResponse2 = stationService.save(new StationRequest("b"));

        final LineRequest lineRequest2 = new LineRequest("신분당선", "bg-red-600", stationResponse1.getId(), stationResponse2.getId(), 10);
        lineService.save(lineRequest2);

        // when
        final PathResponse response = pathService.findPath(stationResponse1.getId(), stationResponse2.getId(), 13);

        // then
        assertThat(response).extracting("distance", "fare")
                .containsExactly(10, 1070);
        assertThat(response.getStations()).hasSize(2)
                .extracting("id", "name")
                .containsExactly(
                        tuple(stationResponse1.getId(), "a"),
                        tuple(stationResponse2.getId(), "b")
                );
    }
}
