package wooteco.subway.domain;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

class FareTest {

    private static final int NO_DISCOUNT_AGE = 20;
    private static final int EXTRA_COST = 500;

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
    
    @DisplayName("추가 요금이 있는 노선을 이용 할 경우 측정된 요금에 추가된다.")
    @Test
    public void testAdditionalFareWithLine() {
        // given
        final Fare fare = new Fare(10, EXTRA_COST, NO_DISCOUNT_AGE);

        // when
        final int result = fare.calculate();

        // then
        assertThat(result).isEqualTo(1250 + EXTRA_COST);
    }

    @DisplayName("어린이는 350원을 공제한 금액의 50%를 할인받는다.")
    @Test
    public void childrenFare() {
        // given
        final Fare fare = new Fare(10, 0, 6);

        // when
        final int result = fare.calculate();

        // then
        assertThat(result).isEqualTo(800);
    }

    @DisplayName("청소년은 350원을 공제한 금액의 20%를 할인받는다.")
    @Test
    public void teenagerFare() {
        // given
        final Fare fare = new Fare(10, 0, 13);

        // when
        final int result = fare.calculate();

        // then
        assertThat(result).isEqualTo(1070);
    }
}
