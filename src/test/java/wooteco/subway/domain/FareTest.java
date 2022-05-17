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

    @DisplayName("11km는 초과운임이 부과되어 1350원 이다.")
    @Test
    public void checkBoundaryTen() {
        // given
        final Fare fare = new Fare(11);

        // when
        final int result = fare.calculate();

        // then
        assertThat(result).isEqualTo(1350);
    }

    @DisplayName("51km는 초과운임이 부과되어 2150원 이다.")
    @Test
    public void checkBoundaryFifty() {
        // given
        final Fare fare = new Fare(51);

        // when
        final int result = fare.calculate();

        // then
        assertThat(result).isEqualTo(2150);
    }
}
