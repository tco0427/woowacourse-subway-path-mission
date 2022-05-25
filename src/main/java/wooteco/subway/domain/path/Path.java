package wooteco.subway.domain.path;

import java.util.List;
import wooteco.subway.domain.Section;

public class Path {

    private final List<Long> shortestPath;
    private final int shortestPathWeight;
    private final List<Section> shortestEdge;

    public Path(List<Long> path, int pathWeight, List<Section> pathEdge) {
        this.shortestPath = path;
        this.shortestPathWeight = pathWeight;
        this.shortestEdge = pathEdge;
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
