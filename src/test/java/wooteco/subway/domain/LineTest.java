package wooteco.subway.domain;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class LineTest {

    private static final String EXCESS_MAX_LENGTH_NAME = "-".repeat(256);
    private static final String EXCESS_MAX_LENGTH_COLOR = "-".repeat(21);
    private static final int EXTRA_FARE = 900;

    @DisplayName("노선의 이름이 공백인지를 검사한다.")
    @Test
    public void blankNameTest() {
        assertThatThrownBy(() -> new Line("", "bg-red-600"))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @DisplayName("노선의 색이 공백인지를 검사한다.")
    @Test
    public void blankColorTest() {
        assertThatThrownBy(() -> new Line("신분당선", ""))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @DisplayName("노선의 이름과 색이 공백인지를 검사한다.")
    @Test
    public void blankNameAndColorTest() {
        assertThatThrownBy(() -> new Line("", ""))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @DisplayName("노선의 이름은 255보다 클 수 없다.")
    @Test
    public void LineNameLengthTest() {
        assertThatThrownBy(() -> new Line(EXCESS_MAX_LENGTH_NAME, "bg-red-600"))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @DisplayName("노선의 색은 20자 보다 클 수 없다.")
    @Test
    public void LineColorLengthTest() {
        assertThatThrownBy(() -> new Line("신분당선", EXCESS_MAX_LENGTH_COLOR))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @DisplayName("추가요금을 포함하여 Line을 생성할 수 있다.")
    @Test
    public void createLineWithExtraFare() {
        // given
        final Line line = new Line("신분당선", "bg-red-600", EXTRA_FARE);

        // when
        final Integer extraFare = line.getExtraFare();

        // then
        assertThat(extraFare).isNotNull();
        assertThat(extraFare).isEqualTo(EXTRA_FARE);
    }
}
