package wooteco.subway.dao.jdbc;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Repository;
import wooteco.subway.dao.StationDao;
import wooteco.subway.domain.Station;

@Repository
public class JdbcStationDao implements StationDao {

    private static final RowMapper<Station> STATION_ROW_MAPPER = (resultSet, rowNum) -> new Station(
            resultSet.getLong("id"),
            resultSet.getString("name")
    );
    private static final String STATION_TABLE_NAME = "station";
    private static final String GENERATE_KEY_COLUMN = "id";

    private final NamedParameterJdbcTemplate jdbcTemplate;
    private final SimpleJdbcInsert simpleJdbcInsert;

    public JdbcStationDao(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = new NamedParameterJdbcTemplate(jdbcTemplate);
        this.simpleJdbcInsert = new SimpleJdbcInsert(jdbcTemplate)
                .withTableName(STATION_TABLE_NAME)
                .usingGeneratedKeyColumns(GENERATE_KEY_COLUMN);
    }

    @Override
    public Station save(Station station) {
        MapSqlParameterSource parameterSource = new MapSqlParameterSource();
        parameterSource.addValue("name", station.getName());

        final long id = simpleJdbcInsert.executeAndReturnKey(parameterSource).longValue();

        return new Station(id, station.getName());
    }

    @Override
    public Optional<Station> findById(Long id) {
        String sql = "SELECT id, name FROM station WHERE id = :id";

        try {
            final Station station = jdbcTemplate.queryForObject(sql, Map.of("id", id), STATION_ROW_MAPPER);
            return Optional.ofNullable(station);
        } catch (EmptyResultDataAccessException e) {
            return Optional.empty();
        }
    }

    @Override
    public List<Station> findAll() {
        String sql = "SELECT id, name FROM station";

        return jdbcTemplate.query(sql, STATION_ROW_MAPPER);
    }

    @Override
    public List<Station> findAllByIds(List<Long> stationIds) {
        String sql = "SELECT id, name FROM station WHERE id IN (:stationIds)";

        return jdbcTemplate.query(sql, Map.of("stationIds", stationIds), STATION_ROW_MAPPER);
    }

    @Override
    public int deleteById(Long id) {
        String sql = "DELETE FROM station WHERE id = :id";

        return jdbcTemplate.update(sql, Map.of("id", id));
    }
}
