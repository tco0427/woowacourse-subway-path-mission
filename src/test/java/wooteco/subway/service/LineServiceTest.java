package wooteco.subway.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.tuple;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

import java.util.List;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.jdbc.Sql;
import wooteco.subway.dto.LineRequest;
import wooteco.subway.dto.LineResponse;
import wooteco.subway.dto.StationRequest;
import wooteco.subway.dto.StationResponse;
import wooteco.subway.exception.NotExistException;

@SpringBootTest
@Sql("/truncate.sql")
class LineServiceTest {

    private final LineService lineService;
    private final StationService stationService;

    @Autowired
    public LineServiceTest(LineService lineService, StationService stationService) {
        this.lineService = lineService;
        this.stationService = stationService;
    }

    @Test
    @DisplayName("노선을 저장할 수 있다.")
    void save() {
        // given
        final StationResponse upStationResponse = stationService.save(new StationRequest("지하철역"));
        final StationResponse downStationResponse = stationService.save(new StationRequest("새로운지하철역"));

        final Long upStationId = upStationResponse.getId();
        final Long downStationId = downStationResponse.getId();

        LineRequest request = new LineRequest("신분당선", "bg-red-600", upStationId, downStationId, 10);

        // when
        final LineResponse response = lineService.save(request);

        // then
        assertThat(response).extracting("name", "color")
                .contains("신분당선", "bg-red-600");
        assertThat(response.getStations()).hasSize(2)
                .extracting("id", "name")
                .containsExactlyInAnyOrder(
                        tuple(upStationId, "지하철역"),
                        tuple(downStationId, "새로운지하철역")
                );
    }

    @Test
    @DisplayName("전체 노선을 조회할 수 있다.")
    void findAll() {
        // given
        final StationResponse upStationResponse = stationService.save(new StationRequest("지하철역"));
        final StationResponse downStationResponse = stationService.save(new StationRequest("새로운지하철역"));
        final StationResponse anotherDownStationResponse = stationService.save(new StationRequest("또다른지하철역"));

        final Long upStationId = upStationResponse.getId();
        final Long downStationId = downStationResponse.getId();
        final Long anotherDownStationId = anotherDownStationResponse.getId();

        LineRequest request1 = new LineRequest("신분당선", "bg-red-600", upStationId, downStationId, 10);
        LineRequest request2 = new LineRequest("분당선", "bg-green-600", upStationId, anotherDownStationId, 10);

        lineService.save(request1);
        lineService.save(request2);

        // when
        List<LineResponse> responses = lineService.findAll();

        // then
        assertThat(responses).hasSize(2)
                .extracting("name", "color")
                .containsExactlyInAnyOrder(
                        tuple("신분당선", "bg-red-600"),
                        tuple("분당선", "bg-green-600")
                );
        assertThat(responses.get(0).getStations()).hasSize(2)
                .extracting("id", "name")
                .containsExactlyInAnyOrder(
                        tuple(upStationId, "지하철역"),
                        tuple(downStationId, "새로운지하철역")
                );
        assertThat(responses.get(1).getStations()).hasSize(2)
                .extracting("id", "name")
                .containsExactlyInAnyOrder(
                        tuple(upStationId, "지하철역"),
                        tuple(anotherDownStationId, "또다른지하철역")
                );
    }

    @Test
    @DisplayName("기존 노선의 이름과 색상을 변경할 수 있다.")
    void updateById() {
        // given
        final StationResponse upStationResponse = stationService.save(new StationRequest("지하철역"));
        final StationResponse downStationResponse = stationService.save(new StationRequest("새로운지하철역"));

        final Long upStationId = upStationResponse.getId();
        final Long downStationId = downStationResponse.getId();

        final LineRequest request = new LineRequest("신분당선", "bg-red-600", upStationId, downStationId, 10);
        final LineResponse savedResponse = lineService.save(request);

        // when
        final LineRequest updateRequest = new LineRequest("다른분당선", "bg-red-600", upStationId, downStationId, 10);
        Long updateId = lineService.updateByLine(savedResponse.getId(), updateRequest);

        // then
        final LineResponse response = lineService.findById(updateId);
        assertThat(response).extracting("name", "color")
                .contains("다른분당선", "bg-red-600");
    }

    @Test
    @DisplayName("노선을 삭제할 수 있다.")
    void deleteById() {
        // given
        final StationResponse upStationResponse = stationService.save(new StationRequest("지하철역"));
        final StationResponse downStationResponse = stationService.save(new StationRequest("새로운지하철역"));

        final Long upStationId = upStationResponse.getId();
        final Long downStationId = downStationResponse.getId();

        LineRequest request = new LineRequest("신분당선", "bg-red-600", upStationId, downStationId, 10);
        final LineResponse savedResponse = lineService.save(request);

        // when & then
        assertDoesNotThrow(() -> lineService.deleteById(savedResponse.getId()));
    }
    
    @DisplayName("존재하지 않는 id로 조회하면 예외가 발생한다.")
    @Test
    public void findByNotExistId() {
        // given
        final StationResponse upStationResponse = stationService.save(new StationRequest("지하철역"));
        final StationResponse downStationResponse = stationService.save(new StationRequest("새로운지하철역"));

        final Long upStationId = upStationResponse.getId();
        final Long downStationId = downStationResponse.getId();

        final LineRequest request = new LineRequest("신분당선", "bg-red-600", upStationId, downStationId, 10);
        final LineResponse savedResponse = lineService.save(request);

        // when & then
        Assertions.assertThatThrownBy(() -> lineService.findById(savedResponse.getId() + 1))
                .isInstanceOf(NotExistException.class);
    }
}
