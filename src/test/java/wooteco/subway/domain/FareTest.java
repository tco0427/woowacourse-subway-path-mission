package wooteco.subway.domain;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

class FareTest {

    private static final int NO_DISCOUNT_AGE = 20;
    private static final int EXTRA_COST = 500;

    @DisplayName("기본운임(10km 이내)는 1250원이고, 10km ~ 50km 는 5km 마다 100원이 추가되며, 50km 초과시에는 8km 마다 100원이 추가된다.")
    @ParameterizedTest
    @CsvSource(value = {"10, 1250", "11,1350", "15,1350", "16,1450", "20,1450", "51,2150", "58,2150", "59, 2250"})
    public void checkAdditionalFareByDistance(int distance, int expectedFare) {
        // given
        final Fare fare = new Fare(distance);

        // when
        final int result = fare.calculate();

        // then
        assertThat(result).isEqualTo(expectedFare);
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

    @DisplayName("어린이는 350원을 공제한 금액의 50%를 할인받고, 청소년은 350원을 공제한 금액의 20%를 할인 받는다.")
    @ParameterizedTest
    @CsvSource(value = {"10, 0, 6, 800", "10, 0, 13, 1070"})
    public void childrenFare(int distance, int extraCost, int age, int expectedFare) {
        // given
        final Fare fare = new Fare(distance, extraCost, age);

        // when
        final int result = fare.calculate();

        // then
        assertThat(result).isEqualTo(expectedFare);
    }
}
