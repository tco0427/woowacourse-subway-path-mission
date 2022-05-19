package wooteco.subway.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.tuple;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import wooteco.subway.dto.LineRequest;
import wooteco.subway.dto.PathResponse;
import wooteco.subway.dto.SectionRequest;
import wooteco.subway.dto.StationRequest;
import wooteco.subway.dto.StationResponse;
import wooteco.subway.service.fake.FakeLineDao;
import wooteco.subway.service.fake.FakeSectionDao;
import wooteco.subway.service.fake.FakeStationDao;

class PathServiceTest {

    private PathService pathService;
    private StationService stationService;
    private SectionService sectionService;
    private LineService lineService;

    @BeforeEach
    void setUp() {
        final FakeSectionDao sectionDao = new FakeSectionDao();
        final FakeStationDao stationDao = new FakeStationDao();
        final FakeLineDao lineDao = new FakeLineDao();

        pathService = new PathService(stationDao, sectionDao);
        stationService = new StationService(stationDao, sectionDao);
        sectionService = new SectionService(sectionDao);
        lineService = new LineService(lineDao, sectionDao, stationDao);
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
        final PathResponse response = pathService.findPath(stationResponse1.getId(), stationResponse3.getId());

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
}
