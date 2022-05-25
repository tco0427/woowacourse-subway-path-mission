package wooteco.subway.domain.path;

import java.util.List;
import wooteco.subway.domain.Section;

public class Path {

    private final List<Long> shortestPath;
    private final int shortestPathWeight;
    private final List<Section> shortestEdge;

    public Path(PathStrategy pathStrategy) {
        this.shortestPath = pathStrategy.getShortestPath();
        this.shortestPathWeight = pathStrategy.getShortestPathWeight();
        this.shortestEdge = pathStrategy.getShortestEdge();
    }

    public List<Long> getShortestPath() {
        return shortestPath;
    }

    public int getShortestPathWeight() {
        return shortestPathWeight;
    }

    public List<Section> getShortestEdge() {
        return shortestEdge;
    }
}
