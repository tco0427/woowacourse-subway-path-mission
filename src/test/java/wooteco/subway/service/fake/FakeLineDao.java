package wooteco.subway.service.fake;

import static java.util.stream.Collectors.toList;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.util.ReflectionUtils;
import wooteco.subway.dao.LineDao;
import wooteco.subway.domain.Line;
import wooteco.subway.domain.Section;

public class FakeLineDao implements LineDao {

    private static final int DELETE_SUCCESS = 1;

    private static Long seq = 0L;
    private final List<Line> lines = new ArrayList<>();

    @Override
    public Line save(Line line) {
        final List<String> names = getNames();

        if (names.contains(line.getName())) {
            throw new DuplicateKeyException("동일한 line이 존재합니다.");
        }

        final Line newLine = createNewObject(line);
        lines.add(newLine);
        return newLine;
    }

    @Override
    public Optional<Line> findById(Long id) {
        return lines.stream()
                .filter(line -> line.getId().equals(id))
                .findAny();
    }

    @Override
    public List<Line> findAllBySections(List<Section> sections) {
        Set<Long> stationIds = new HashSet<>();
        setStationIds(sections, stationIds);

        return lines.stream()
                .filter(line -> stationIds.contains(line.getId()))
                .collect(toList());
    }

    @Override
    public List<Line> findAll() {
        return List.copyOf(lines);
    }

    @Override
    public Long updateByLine(Line updateLine) {
        final Line findLine = lines.stream()
                .filter(line -> line.getId().equals(updateLine.getId()))
                .findAny()
                .orElseThrow(IllegalArgumentException::new);
        lines.remove(findLine);
        lines.add(updateLine);

        return updateLine.getId();
    }

    @Override
    public int deleteById(Long id) {
        final Line findLine = lines.stream()
                .filter(line -> line.getId().equals(id))
                .findAny()
                .orElseThrow(IllegalArgumentException::new);

        lines.remove(findLine);
        return DELETE_SUCCESS;
    }

    private static Line createNewObject(Line line) {
        Field field = ReflectionUtils.findField(Line.class, "id");
        field.setAccessible(true);
        ReflectionUtils.setField(field, line, ++seq);
        return line;
    }

    private List<String> getNames() {
        return lines.stream()
                .map(Line::getName)
                .collect(toList());
    }

    private void setStationIds(List<Section> sections, Set<Long> stationIds) {
        final List<Long> upStationIds = getUpStationIds(sections);
        final List<Long> downStationIds = getDownStationIds(sections);

        stationIds.addAll(upStationIds);
        stationIds.addAll(downStationIds);
    }

    private List<Long> getUpStationIds(List<Section> sections) {
        return sections.stream()
                .map(Section::getUpStationId)
                .collect(toList());
    }

    private List<Long> getDownStationIds(List<Section> sections) {
        return sections.stream()
                .map(Section::getDownStationId)
                .collect(toList());
    }
}
