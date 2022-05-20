package wooteco.subway.dao;

import java.util.List;
import java.util.Optional;
import wooteco.subway.domain.Line;
import wooteco.subway.domain.Section;

public interface LineDao {

    Line save(Line line);

    Optional<Line> findById(Long id);

    List<Line> findAllBySections(List<Section> sections);

    List<Line> findAll();

    Long updateByLine(Line line);

    int deleteById(Long id);
}
