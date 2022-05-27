package wooteco.subway.dao.jdbc;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Repository;
import wooteco.subway.dao.SectionDao;
import wooteco.subway.domain.Section;

@Repository
public class JdbcSectionDao implements SectionDao {

    private static final RowMapper<Section> SECTION_ROW_MAPPER = (resultSet, rowNum) -> new Section(
            resultSet.getLong("id"),
            resultSet.getLong("line_id"),
            resultSet.getLong("up_station_id"),
            resultSet.getLong("down_station_id"),
            resultSet.getInt("distance")
    );
    private static final String SECTION_TABLE_NAME = "section";
    private static final String GENERATE_KEY_COLUMN = "id";

    private final NamedParameterJdbcTemplate jdbcTemplate;
    private final SimpleJdbcInsert simpleJdbcInsert;

    public JdbcSectionDao(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = new NamedParameterJdbcTemplate(jdbcTemplate);
        this.simpleJdbcInsert = new SimpleJdbcInsert(jdbcTemplate)
                .withTableName(SECTION_TABLE_NAME)
                .usingGeneratedKeyColumns(GENERATE_KEY_COLUMN);
    }

    @Override
    public Section save(Section section) {
        MapSqlParameterSource parameterSource = new MapSqlParameterSource();
        parameterSource.addValue("line_id", section.getLineId());
        parameterSource.addValue("up_station_id", section.getUpStationId());
        parameterSource.addValue("down_station_id", section.getDownStationId());
        parameterSource.addValue("distance", section.getDistance());

        final long id = simpleJdbcInsert.executeAndReturnKey(parameterSource).longValue();

        return new Section(id, section.getLineId(),
                section.getUpStationId(), section.getDownStationId(), section.getDistance());
    }

    @Override
    public void saveAll(List<Section> sections) {
        final String sql = "INSERT INTO `section` (line_id, up_station_id, down_station_id, distance)"
                + " VALUES (:lineId, :upStationId, :downStationId, :distance)";

        SqlParameterSource[] parameters = getParameters(sections);
        jdbcTemplate.batchUpdate(sql, parameters);
    }

    private SqlParameterSource[] getParameters(List<Section> sections) {
        return sections.stream()
                .map(this::makeParameterSource)
                .collect(Collectors.toList()).toArray(SqlParameterSource[]::new);
    }

    private SqlParameterSource makeParameterSource(Section section) {
        final MapSqlParameterSource mapSqlParameterSource = new MapSqlParameterSource();
        mapSqlParameterSource.addValue("lineId", section.getLineId());
        mapSqlParameterSource.addValue("upStationId", section.getUpStationId());
        mapSqlParameterSource.addValue("downStationId", section.getDownStationId());
        mapSqlParameterSource.addValue("distance", section.getDistance());
        return mapSqlParameterSource;
    }

    @Override
    public List<Section> findByLineId(Long lineId) {
        String sql = "SELECT id, line_id, up_station_id, down_station_id, distance"
                + " FROM `section`"
                + " WHERE line_id = :lineId";

        return jdbcTemplate.query(sql, Map.of("lineId", lineId), SECTION_ROW_MAPPER);
    }

    @Override
    public List<Section> findAll() {
        final String sql = "SELECT id, line_id, up_station_id, down_station_id, distance FROM `section`";

        return jdbcTemplate.query(sql, SECTION_ROW_MAPPER);
    }


    @Override
    public int deleteById(Long id) {
        String sql = "DELETE FROM `section` WHERE id = :id";

        return jdbcTemplate.update(sql, Map.of("id",id));
    }

    @Override
    public int deleteByLineId(Long lineId) {
        String sql = "DELETE FROM `section` WHERE line_id = :lineId";

        return jdbcTemplate.update(sql, Map.of("lineId", lineId));
    }
}
