package wooteco.subway.dao.jdbc;

import static java.util.stream.Collectors.toList;

import java.sql.PreparedStatement;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;
import wooteco.subway.dao.LineDao;
import wooteco.subway.domain.Line;
import wooteco.subway.domain.Section;

@Repository
public class JdbcLineDao implements LineDao {

    private static final RowMapper<Line> LINE_ROW_MAPPER = (resultSet, rowNum) -> new Line(
            resultSet.getLong("id"),
            resultSet.getString("name"),
            resultSet.getString("color"),
            resultSet.getInt("extraFare")
    );

    private final JdbcTemplate jdbcTemplate;

    public JdbcLineDao(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public Line save(Line line) {
        String sql = "INSERT INTO line (name, color, extraFare) VALUES(?, ?, ?)";

        final KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(connection -> {
            final PreparedStatement statement = connection.prepareStatement(sql, new String[]{"id"});
            statement.setString(1, line.getName());
            statement.setString(2, line.getColor());
            statement.setInt(3, line.getExtraFare());
            return statement;
        }, keyHolder);

        final Long id = Objects.requireNonNull(keyHolder.getKey()).longValue();

        return new Line(id, line.getName(), line.getColor());
    }

    @Override
    public Optional<Line> findById(Long id) {
        String sql = "SELECT * FROM line WHERE id = ?";

        try {
            final Line line = jdbcTemplate.queryForObject(sql, LINE_ROW_MAPPER, id);
            return Optional.ofNullable(line);
        } catch (EmptyResultDataAccessException e) {
            return Optional.empty();
        }
    }

    @Override
    public List<Line> findAllBySections(List<Section> sections) {
        final List<Long> lineIds = getLineIds(sections);

        final String inSql = String.join(",", Collections.nCopies(lineIds.size(), "?"));
        final String sql = String.format("SELECT * FROM line WHERE id IN (%s)", inSql);

        return jdbcTemplate.query(sql, lineIds.toArray(), LINE_ROW_MAPPER);
    }

    @Override
    public List<Line> findAll() {
        String sql = "SELECT * FROM line";

        return jdbcTemplate.query(sql, LINE_ROW_MAPPER);
    }

    @Override
    public Long updateByLine(Line line) {
        String sql = "UPDATE line SET name = ?, color = ? WHERE id = ?";

        jdbcTemplate.update(sql, line.getName(), line.getColor(), line.getId());
        return line.getId();
    }

    @Override
    public int deleteById(Long id) {
        String sql = "DELETE FROM line WHERE id = ?";
        return jdbcTemplate.update(sql, id);
    }

    private List<Long> getLineIds(List<Section> sections) {
        return sections.stream()
                .map(Section::getLineId)
                .collect(toList());
    }
}
