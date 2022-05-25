package wooteco.subway.domain.path;

import java.util.List;
import wooteco.subway.domain.Section;

public interface PathGenerator {

    Path generatePath(List<Section> sections, Long sourceId, Long targetId);
}
