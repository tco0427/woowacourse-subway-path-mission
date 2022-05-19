package wooteco.subway.domain;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

class FareTest {

    @DisplayName("기본 운임(10km 이내) 요금을 확인한다.")
    @Test
    public void chargeDefaultFare() {
        // given
        final Fare fare = new Fare(10);

        // when
        final int result = fare.calculate();

        // then
        assertThat(result).isEqualTo(1250);
    }

    @DisplayName("10km ~ 50km 는 5km 마다 100원이 추가되므로 11~15km는 초과운임이 부과되어 1350원 이다.")
    @ParameterizedTest
    @ValueSource(ints = {11, 12, 13, 14, 15})
    public void checkBoundaryTen(int distance) {
        // given
        final Fare fare = new Fare(distance);

        // when
        final int result = fare.calculate();

        // then
        assertThat(result).isEqualTo(1350);
    }

    @DisplayName("16 ~ 20km는 10km ~ 50km 사이이므로 200원 추가운임을 부과한다.")
    @ParameterizedTest
    @ValueSource(ints = {16, 17, 18, 19, 20})
    public void chargeAdditionalFare200(int distance) {
        // given
        final Fare fare = new Fare(distance);

        // when
        final int result = fare.calculate();

        // then
        assertThat(result).isEqualTo(1450);
    }

    @DisplayName("51 ~ 58km는 50km 초과이므로 900원 초과운임이 부과되어 2150원 이다.")
    @ParameterizedTest
    @ValueSource(ints = {51, 52, 53, 54, 55, 56, 57, 58})
    public void checkBoundaryFifty(int distance) {
        // given
        final Fare fare = new Fare(distance);

        // when
        final int result = fare.calculate();

        // then
        assertThat(result).isEqualTo(2150);
    }
}
