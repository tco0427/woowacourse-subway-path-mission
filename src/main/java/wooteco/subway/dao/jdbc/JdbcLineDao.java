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
import wooteco.subway.dao.LineDao;
import wooteco.subway.domain.Line;

@Repository
public class JdbcLineDao implements LineDao {

    private static final RowMapper<Line> LINE_ROW_MAPPER = (resultSet, rowNum) -> new Line(
            resultSet.getLong("id"),
            resultSet.getString("name"),
            resultSet.getString("color"),
            resultSet.getInt("extraFare")
    );
    private static final String LINE_TABLE_NAME = "line";
    private static final String GENERATE_KEY_COLUMN = "id";

    private final NamedParameterJdbcTemplate jdbcTemplate;
    private final SimpleJdbcInsert simpleJdbcInsert;

    public JdbcLineDao(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = new NamedParameterJdbcTemplate(jdbcTemplate);
        this.simpleJdbcInsert = new SimpleJdbcInsert(jdbcTemplate)
                .withTableName(LINE_TABLE_NAME)
                .usingGeneratedKeyColumns(GENERATE_KEY_COLUMN);
    }

    @Override
    public Line save(Line line) {
        MapSqlParameterSource parameterSource = new MapSqlParameterSource();
        parameterSource.addValue("name", line.getName());
        parameterSource.addValue("color", line.getColor());
        parameterSource.addValue("extraFare", line.getExtraFare());

        final Long id = simpleJdbcInsert.executeAndReturnKey(parameterSource).longValue();

        return new Line(id, line.getName(), line.getColor());
    }

    @Override
    public Optional<Line> findById(Long id) {
        String sql = "SELECT id, name, color, extraFare "
                + "FROM line "
                + "WHERE id = :id";

        try {
            final Line line = jdbcTemplate.queryForObject(sql, Map.of("id", id), LINE_ROW_MAPPER);
            return Optional.ofNullable(line);
        } catch (EmptyResultDataAccessException e) {
            return Optional.empty();
        }
    }

    @Override
    public List<Line> findAllByIds(List<Long> ids) {
        final String sql = "SELECT id, name, color, extraFare "
                + "FROM line "
                + "WHERE id IN (:ids)";

        return jdbcTemplate.query(sql, Map.of("ids", ids), LINE_ROW_MAPPER);
    }

    @Override
    public List<Line> findAll() {
        String sql = "SELECT id, name, color, extraFare FROM line";

        return jdbcTemplate.query(sql, LINE_ROW_MAPPER);
    }

    @Override
    public Long updateByLine(Line line) {
        String sql = "UPDATE line SET name = :name, color = :color WHERE id = :id";

        MapSqlParameterSource parameterSource = new MapSqlParameterSource();
        parameterSource.addValue("name", line.getName());
        parameterSource.addValue("color", line.getColor());
        parameterSource.addValue("id", line.getId());

        jdbcTemplate.update(sql, parameterSource);
        return line.getId();
    }

    @Override
    public int deleteById(Long id) {
        String sql = "DELETE FROM line WHERE id = :id";
        return jdbcTemplate.update(sql, Map.of("id", id));
    }
}
