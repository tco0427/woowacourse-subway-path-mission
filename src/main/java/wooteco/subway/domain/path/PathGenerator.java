package wooteco.subway.domain.path;

import java.util.List;
import wooteco.subway.domain.Section;

public interface PathGenerator {

    List<Long> getShortestPath();

    int getShortestPathWeight();

    List<Section> getShortestEdge();
}
