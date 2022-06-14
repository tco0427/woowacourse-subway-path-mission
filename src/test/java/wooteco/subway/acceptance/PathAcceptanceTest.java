package wooteco.subway.acceptance;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.tuple;

import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.http.HttpStatus;
import wooteco.subway.dto.LineRequest;
import wooteco.subway.dto.LineResponse;
import wooteco.subway.dto.PathResponse;
import wooteco.subway.dto.SectionRequest;
import wooteco.subway.dto.StationRequest;
import wooteco.subway.dto.StationResponse;

public class PathAcceptanceTest extends AcceptanceTest {

    private static final int DEFAULT_FARE = 0;

    @DisplayName("출발역과 도착역을 기반으로 최단 경로를 조회할 수 있다.")
    @Test
    public void findPath() {
        // given
        final Long stationId1 = extractStationIdFromName("교대역");
        final Long stationId2 = extractStationIdFromName("강남역");
        final Long stationId3 = extractStationIdFromName("역삼역");

        final LineRequest params = new LineRequest("2호선", "bg-red-600", stationId1, stationId3, 7);
        Long lineId = extractId(AcceptanceFixture.post(params, "/lines"));

        final SectionRequest sectionRequest = new SectionRequest(stationId1, stationId2, 4);
        AcceptanceFixture.post(sectionRequest, "/lines/" + lineId + "/sections");

        // when
        final ExtractableResponse<Response> response = AcceptanceFixture.get(
                "/paths?source=" + stationId1 + "&target=" + stationId3 + "&age=21");

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());

        final PathResponse pathResponse = response.jsonPath().getObject(".", PathResponse.class);
        assertThat(pathResponse.getStations()).hasSize(3)
                .extracting("id", "name")
                .containsExactly(
                        tuple(stationId1, "교대역"),
                        tuple(stationId2, "강남역"),
                        tuple(stationId3, "역삼역")
                );
    }

    @DisplayName("10km 이내는 기본운임인 1250원을 부과한다.")
    @Test
    public void testFareWhen10km() {
        // given
        final Long sourceStationId = extractStationIdFromName("교대역");
        final Long targetStationId = extractStationIdFromName("역삼역");
        final int distance = 10;

        requestLineWithExtraFare("2호선", sourceStationId, targetStationId, distance, DEFAULT_FARE);

        // when
        final ExtractableResponse<Response> response = AcceptanceFixture.get(
                "/paths?source=" + sourceStationId + "&target=" + targetStationId + "&age=21");

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());

        final PathResponse pathResponse = response.jsonPath().getObject(".", PathResponse.class);
        assertThat(pathResponse.getFare()).isEqualTo(1250);
    }

    @DisplayName("10km ~ 50km 사이는 5km 마다 100원이 추가된다.")
    @ParameterizedTest
    @ValueSource(ints = {11, 12, 13, 14, 15})
    public void testAdditionalFare(int distance) {
        // given
        final Long sourceStationId = extractStationIdFromName("교대역");
        final Long targetStationId = extractStationIdFromName("역삼역");

        requestLineWithExtraFare("2호선", sourceStationId, targetStationId, distance, DEFAULT_FARE);

        // when
        final ExtractableResponse<Response> response = AcceptanceFixture.get(
                "/paths?source=" + sourceStationId + "&target=" + targetStationId + "&age=21");

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());

        final PathResponse pathResponse = response.jsonPath().getObject(".", PathResponse.class);
        assertThat(pathResponse.getFare()).isEqualTo(1350);
    }

    @DisplayName("운행 거리가 50km 초과인 경우 8km 마다 100원이 추가된다.")
    @ParameterizedTest
    @ValueSource(ints = {51, 52, 53, 54, 55, 56, 57, 58})
    public void testFareWhenOver50km(int distance) {
        // given
        final Long sourceStationId = extractStationIdFromName("교대역");
        final Long targetStationId = extractStationIdFromName("역삼역");

        requestLineWithExtraFare("2호선", sourceStationId, targetStationId, distance, DEFAULT_FARE);

        // when
        final ExtractableResponse<Response> response = AcceptanceFixture.get(
                "/paths?source=" + sourceStationId + "&target=" + targetStationId + "&age=21");

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());

        final PathResponse pathResponse = response.jsonPath().getObject(".", PathResponse.class);
        assertThat(pathResponse.getFare()).isEqualTo(2150);
    }

    @DisplayName("청소년은 기본 운임에서 350원을 공제한 금액의 20%를 할인받는다.")
    @ParameterizedTest
    @ValueSource(ints = {13, 14, 15, 16, 17, 18})
    public void testTeenagerFare(int age) {
        // given
        final Long sourceStationId = extractStationIdFromName("교대역");
        final Long targetStationId = extractStationIdFromName("역삼역");
        final int distance = 58;

        requestLineWithExtraFare("2호선", sourceStationId, targetStationId, distance, DEFAULT_FARE);

        // when
        final ExtractableResponse<Response> response = AcceptanceFixture.get(
                "/paths?source=" + sourceStationId + "&target=" + targetStationId + "&age=" + age);

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());

        final PathResponse pathResponse = response.jsonPath().getObject(".", PathResponse.class);
        assertThat(pathResponse.getFare()).isEqualTo(1790);
    }

    @DisplayName("어린이는 기본 운임에서 350원을 공제한 금액의 50%를 할인받는다.")
    @ParameterizedTest
    @ValueSource(ints = {6, 7, 8, 9, 10, 11, 12})
    public void testChildrenFare(int age) {
        // given
        final Long sourceStationId = extractStationIdFromName("교대역");
        final Long targetStationId = extractStationIdFromName("역삼역");
        final int distance = 58;

        requestLineWithExtraFare("2호선", sourceStationId, targetStationId, distance, DEFAULT_FARE);

        // when
        final ExtractableResponse<Response> response = AcceptanceFixture.get(
                "/paths?source=" + sourceStationId + "&target=" + targetStationId + "&age=" + age);

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());

        final PathResponse pathResponse = response.jsonPath().getObject(".", PathResponse.class);
        assertThat(pathResponse.getFare()).isEqualTo(1250);
    }

    @DisplayName("이용한 노선 중 추가 요금이 있는 경우 가장 추가요금이 높은 금액만 적용한다.")
    @Test
    public void testExtraFromLine() {
        // given
        final Long sourceStationId = extractStationIdFromName("교대역");
        final Long targetStationId = extractStationIdFromName("강남역");
        final Long anotherTargetStationId = extractStationIdFromName("양재역");
        final int distance = 10;

        requestLineWithExtraFare("2호선", sourceStationId, targetStationId, distance, DEFAULT_FARE);
        requestLineWithExtraFare("신분당선", targetStationId, anotherTargetStationId, distance, 900);

        // when
        final ExtractableResponse<Response> response = AcceptanceFixture.get(
                "/paths?source=" + sourceStationId + "&target=" + anotherTargetStationId + "&age=15");

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());

        final PathResponse pathResponse = response.jsonPath().getObject(".", PathResponse.class);
        assertThat(pathResponse.getFare()).isEqualTo(1950);
    }

    private void requestLineWithExtraFare(String lineName, Long sourceStationId, Long targetStationId, int distance, int extraFare) {
        final LineRequest params = new LineRequest(lineName, "bg-red-600", sourceStationId, targetStationId, distance, extraFare);
        AcceptanceFixture.post(params, "/lines");
    }

    private Long extractId(ExtractableResponse<Response> response) {
        return response.jsonPath()
                .getObject(".", LineResponse.class)
                .getId();
    }

    private Long extractStationIdFromName(String name) {
        final StationRequest stationRequest = new StationRequest(name);
        final ExtractableResponse<Response> stationResponse = AcceptanceFixture.post(stationRequest, "/stations");

        return stationResponse.jsonPath()
                .getObject(".", StationResponse.class)
                .getId();
    }
}
