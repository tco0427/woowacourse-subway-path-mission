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
        FakeSectionDao sectionDao = new FakeSectionDao();
        FakeStationDao stationDao = new FakeStationDao();
        final FakeLineDao lineDao = new FakeLineDao();

        pathService = new PathService(stationDao, sectionDao);
        stationService = new StationService(stationDao, sectionDao);
        sectionService = new SectionService(sectionDao);
        lineService = new LineService(lineDao, sectionDao, stationDao);
    }


    @DisplayName("경로를 조회할 수 있다.")
    @Test
    public void findPath() {
        // given
        final StationRequest a = new StationRequest("a");
        final StationRequest b = new StationRequest("b");
        final StationRequest c = new StationRequest("c");

        final StationResponse response1 = stationService.save(a);
        final StationResponse response2 = stationService.save(b);
        final StationResponse response3 = stationService.save(c);

        final LineRequest lineRequest = new LineRequest("신분당선", "bg-red-600", response1.getId(), response3.getId(), 10);
        final Long lineId = lineService.save(lineRequest).getId();

        final SectionRequest request = new SectionRequest(response1.getId(), response2.getId(), 4);

        sectionService.save(lineId, request);

        // when
        final PathResponse response = pathService.findPath(1L, 3L);

        // then
        assertThat(response).extracting("distance", "fare")
                .containsExactly(10, 1250);
        assertThat(response.getStations()).hasSize(3)
                .extracting("id", "name")
                .containsExactly(
                        tuple(1L, "a"),
                        tuple(2L, "b"),
                        tuple(3L, "c")
                );
    }
}