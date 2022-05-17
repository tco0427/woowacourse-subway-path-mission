package wooteco.subway.domain;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

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

    @DisplayName("12km는 10km ~ 50km 사이이므로 100원 추가운임을 부과한다.")
    @Test
    public void chargeAdditionalFare100() {
        // given
        final Fare fare = new Fare(12);

        // when
        final int result = fare.calculate();

        // then
        assertThat(result).isEqualTo(1350);
    }

    @DisplayName("16km는 10km ~ 50km 사이이므로 200원 추가운임을 부과한다.")
    @Test
    public void chargeAdditionalFare200() {
        // given
        final Fare fare = new Fare(16);

        // when
        final int result = fare.calculate();

        // then
        assertThat(result).isEqualTo(1450);
    }

    @DisplayName("58km는 50km 초과이므로 900원 추가운임을 부과한다.")
    @Test
    public void chargeAdditionalFareOver50() {
        // given
        final Fare fare = new Fare(58);

        // when
        final int result = fare.calculate();

        // then
        assertThat(result).isEqualTo(2150);
    }
}