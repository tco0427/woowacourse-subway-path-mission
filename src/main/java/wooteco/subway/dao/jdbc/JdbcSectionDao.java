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
import wooteco.subway.domain.Line;
import wooteco.subway.domain.Section;
import wooteco.subway.domain.Station;

@Repository
public class JdbcSectionDao implements SectionDao {

    private static final RowMapper<Section> SECTION_ROW_MAPPER = createSectionRowMapper();
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
        parameterSource.addValue("line_id", section.getLine().getId());
        parameterSource.addValue("up_station_id", section.getUpStation().getId());
        parameterSource.addValue("down_station_id", section.getDownStation().getId());
        parameterSource.addValue("distance", section.getDistance());

        final long id = simpleJdbcInsert.executeAndReturnKey(parameterSource).longValue();

        return new Section(id, section.getLine(),
                section.getUpStation(), section.getDownStation(), section.getDistance());
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
        mapSqlParameterSource.addValue("lineId", section.getLine().getId());
        mapSqlParameterSource.addValue("upStationId", section.getUpStation().getId());
        mapSqlParameterSource.addValue("downStationId", section.getDownStation().getId());
        mapSqlParameterSource.addValue("distance", section.getDistance());
        return mapSqlParameterSource;
    }

    @Override
    public Section findById(Long id) {
        String sql = "SELECT `section`.id, "
                + "line.id as line_id, line.name as line_name, "
                + "line.color as line_color, line.extraFare as line_extra_fare, "
                + "up.id as up_station_id, up.name as up_station_name, "
                + "down.id as down_station_id, down.name as down_station_name, distance FROM section "
                + "JOIN line on line.id = section.line_id "
                + "JOIN station up on up.id = up_station_id "
                + "JOIN station down on down.id = down_station_id "
                + "WHERE section.id = :id";

        return jdbcTemplate.queryForObject(sql, Map.of("id", id), SECTION_ROW_MAPPER);
    }

    @Override
    public List<Section> findByLineId(Long lineId) {
        String sql = "SELECT `section`.id, "
                + "line.id as line_id, line.name as line_name, "
                + "line.color as line_color, line.extraFare as line_extra_fare, "
                + "up.id as up_station_id, up.name as up_station_name, "
                + "down.id as down_station_id, down.name as down_station_name, distance FROM `section` "
                + "JOIN line on line.id = `section`.line_id "
                + "JOIN station up on up.id = up_station_id "
                + "JOIN station down on down.id = down_station_id "
                + "WHERE `section`.line_id = :lineId";

        return jdbcTemplate.query(sql, Map.of("lineId", lineId), SECTION_ROW_MAPPER);
    }

    @Override
    public List<Section> findAll() {
        final String sql = "SELECT `section`.id, "
                + "line.id as line_id, line.name as line_name, "
                + "line.color as line_color, line.extraFare as line_extra_fare, "
                + "up.id as up_station_id, up.name as up_station_name, "
                + "down.id as down_station_id, down.name as down_station_name, distance FROM `section` "
                + "JOIN line on line.id = `section`.line_id "
                + "JOIN station up on up.id = up_station_id "
                + "JOIN station down on down.id = down_station_id";

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

    private static RowMapper<Section> createSectionRowMapper() {
        return (resultSet, rowNum) -> {
            final long sectionId = resultSet.getLong("id");

            final long lineId = resultSet.getLong("line_id");
            final String lineName = resultSet.getString("line_name");
            final String lineColor = resultSet.getString("line_color");
            final int extraFare = resultSet.getInt("line_extra_fare");
            Line line = new Line(lineId, lineName, lineColor, extraFare);

            final Long upStationId = resultSet.getLong("up_station_id");
            final String upStationName = resultSet.getString("up_station_name");
            Station upStation = new Station(upStationId, upStationName);

            final Long downStationId = resultSet.getLong("down_station_id");
            final String downStationName = resultSet.getString("down_station_name");
            Station downStation = new Station(downStationId, downStationName);

            int distance = resultSet.getInt("distance");

            return new Section(sectionId, line, upStation, downStation, distance);
        };
    }
}
