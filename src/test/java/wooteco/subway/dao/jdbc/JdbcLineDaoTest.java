package wooteco.subway.dao.jdbc;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.tuple;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.jdbc.core.JdbcTemplate;
import wooteco.subway.domain.Line;
import wooteco.subway.domain.Section;

@JdbcTest
class JdbcLineDaoTest {

    private static final String FAIL_FIND_LINE = "fail";
    private static final Line INAPPROPRIATE_LINE = new Line(FAIL_FIND_LINE, FAIL_FIND_LINE);

    private final JdbcLineDao jdbcLineDao;

    @Autowired
    public JdbcLineDaoTest(JdbcTemplate jdbcTemplate) {
        this.jdbcLineDao = new JdbcLineDao(jdbcTemplate);
    }

    @Test
    @DisplayName("노선을 등록할 수 있다.")
    void save() {
        // given
        final Line line = new Line("신분당선", "bg-red-600");

        // when
        final Line savedLine = jdbcLineDao.save(line);

        // then
        assertThat(savedLine).extracting("name", "color")
                .contains("신분당선", "bg-red-600");
    }

    @Test
    @DisplayName("전체 노선을 조회할 수 있다.")
    void findAll() {
        // given
        final Line line1 = new Line("신분당선", "bg-red-600");
        final Line line2 = new Line("분당선", "bg-green-600");
        jdbcLineDao.save(line1);
        jdbcLineDao.save(line2);

        // when
        List<Line> lines = jdbcLineDao.findAll();

        // then
        assertThat(lines).hasSize(2)
                .extracting("name", "color")
                .containsExactlyInAnyOrder(
                        tuple("신분당선", "bg-red-600"),
                        tuple("분당선", "bg-green-600"));
    }

    @Test
    @DisplayName("단건 노선을 조회한다.")
    void findById() {
        // given
        final Line line = new Line("신분당선", "bg-red-600");
        final Line savedLine = jdbcLineDao.save(line);

        // when
        final Line findLine = jdbcLineDao.findById(savedLine.getId())
                .orElse(INAPPROPRIATE_LINE);

        // then
        assertThat(findLine).extracting("name", "color")
                .contains("신분당선", "bg-red-600");
    }

    @Test
    @DisplayName("기존 노선의 이름과 색상을 변경할 수 있다.")
    void updateById() {
        // given
        final Line line = new Line("신분당선", "bg-red-600");
        final Line savedLine = jdbcLineDao.save(line);

        // when
        final Line newLine = new Line(savedLine.getId(), "다른분당선", "bg-red-600");
        jdbcLineDao.updateByLine(newLine);

        // then
        final Line findLine = jdbcLineDao.findById(savedLine.getId())
                        .orElse(INAPPROPRIATE_LINE);

        assertThat(findLine).extracting("name", "color")
                .contains("다른분당선", "bg-red-600");
    }

    @Test
    @DisplayName("노선을 삭제할 수 있다.")
    void deleteById() {
        // given
        final Line line = new Line("신분당선", "bg-red-600");
        final Line savedLine = jdbcLineDao.save(line);

        // when & then
        assertDoesNotThrow(() -> jdbcLineDao.deleteById(savedLine.getId()));
    }

    @DisplayName("구간들을 통해서 노선을 구할 수 있다.")
    @Test
    public void findAllBySections() {
        // given
        final Long lineId1 = jdbcLineDao.save(new Line("분당선", "bg-green-600")).getId();
        final Long lineId2 = jdbcLineDao.save(new Line("신분당선", "bg-red-600")).getId();
        final Long lineId3 = jdbcLineDao.save(new Line("다른분당선", "bg-red-600")).getId();

        final List<Section> sampleSections = List.of(
                new Section(1L, lineId1, 1L, 2L, 10),
                new Section(2L, lineId1, 2L, 3L, 10),
                new Section(3L, lineId2, 2L, 4L, 10),
                new Section(4L, lineId3, 5L, 6L, 10)
        );

        // when
        final List<Line> lines = jdbcLineDao.findAllBySections(sampleSections);

        // then
        assertThat(lines).hasSize(3)
                .extracting("id")
                .contains(lineId1, lineId2, lineId3);
    }
}
