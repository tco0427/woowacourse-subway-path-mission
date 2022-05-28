package wooteco.subway.domain.path;

import java.util.List;
import wooteco.subway.domain.Section;
import wooteco.subway.domain.Station;

public interface PathGenerator {

    Path generatePath(List<Section> sections, Station sourceStation, Station targetStation);
}
