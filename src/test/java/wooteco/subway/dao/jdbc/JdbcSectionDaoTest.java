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
import wooteco.subway.domain.Station;

@JdbcTest
class JdbcSectionDaoTest {

    private final JdbcSectionDao jdbcSectionDao;
    private final JdbcLineDao jdbcLineDao;
    private final JdbcStationDao jdbcStationDao;

    @Autowired
    public JdbcSectionDaoTest(JdbcTemplate jdbcTemplate) {
        this.jdbcSectionDao = new JdbcSectionDao(jdbcTemplate);
        this.jdbcLineDao = new JdbcLineDao(jdbcTemplate);
        this.jdbcStationDao = new JdbcStationDao(jdbcTemplate);
    }

    @Test
    @DisplayName("구간을 등록할 수 있다.")
    void save() {
        // given
        final Station station1 = new Station("지하철역이름");
        final Station station2 = new Station("또다른지하철역이름");
        final Long upStationId = jdbcStationDao.save(station1);
        final Long downStationId = jdbcStationDao.save(station2);

        final Line line = new Line("신분당선", "bg-red-600");
        final Line savedLine = jdbcLineDao.save(line);

        // when
        final Section section = new Section(savedLine.getId(), upStationId, downStationId, 10);
        final Section savedSection = jdbcSectionDao.save(section);

        // then
        assertThat(savedSection).extracting("upStationId", "downStationId", "distance")
                .contains(upStationId, downStationId, 10);
    }

    @Test
    @DisplayName("구간을 삭제할 수 있다.")
    void deleteById() {
        // given
        final Station station1 = new Station("지하철역이름");
        final Station station2 = new Station("또다른지하철역이름");
        final Long upStationId = jdbcStationDao.save(station1);
        final Long downStationId = jdbcStationDao.save(station2);

        final Line line = new Line("신분당선", "bg-red-600");
        final Line savedLine = jdbcLineDao.save(line);

        final Section section = new Section(savedLine.getId(), upStationId, downStationId, 10);
        final Section savedSection = jdbcSectionDao.save(section);

        // when & then
        assertDoesNotThrow(() -> jdbcSectionDao.deleteById(savedSection.getId()));
    }

    @DisplayName("역 id 값들을 통해서 해당하는 모든 구간들을 모두 구할 수 있다.")
    @Test
    public void findAllByStationIds() {
        // given
        final Station station1 = new Station("지하철역이름");
        final Station station2 = new Station("다른지하철역이름");
        final Station station3 = new Station("또다른지하철역이름");
        final Long upStationId = jdbcStationDao.save(station1);
        final Long downStationId = jdbcStationDao.save(station2);
        final Long anotherStationId = jdbcStationDao.save(station3);

        final Line line = new Line("신분당선", "bg-red-600");
        final Line savedLine = jdbcLineDao.save(line);

        jdbcSectionDao.save(new Section(savedLine.getId(), upStationId, downStationId, 10));
        jdbcSectionDao.save(new Section(savedLine.getId(), downStationId, anotherStationId, 10));

        // when
        final List<Section> sections = jdbcSectionDao.findAllByStationIds(
                List.of(upStationId, downStationId, anotherStationId));

        // then
        assertThat(sections).hasSize(2)
                .extracting("upStationId", "downStationId")
                .contains(
                        tuple(upStationId, downStationId),
                        tuple(downStationId, anotherStationId)
                );
    }
}
