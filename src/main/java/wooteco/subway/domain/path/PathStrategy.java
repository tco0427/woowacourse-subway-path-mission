package wooteco.subway.domain.path;

import java.util.List;
import wooteco.subway.domain.Section;

public interface PathStrategy {

    List<Long> getShortestPath();

    int getShortestPathWeight();

    List<Section> getShortestEdge();
}
