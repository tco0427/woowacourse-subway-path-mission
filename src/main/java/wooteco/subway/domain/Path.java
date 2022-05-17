package wooteco.subway.domain;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.jgrapht.alg.shortestpath.DijkstraShortestPath;
import org.jgrapht.graph.DefaultWeightedEdge;
import org.jgrapht.graph.WeightedMultigraph;

public class Path {

    private final WeightedMultigraph<Long, DefaultWeightedEdge> graph = new WeightedMultigraph<>(
            DefaultWeightedEdge.class);

    public Path(List<Section> sections) {
        final List<Long> stationIds = getStationIds(sections);

        addVertexes(stationIds);
        addEdges(sections);
    }

    private List<Long> getStationIds(List<Section> sections) {
        Set<Long> stationIds = new HashSet<>();
        for (Section section : sections) {
            stationIds.add(section.getUpStationId());
            stationIds.add(section.getDownStationId());
        }

        return new ArrayList<>(stationIds);
    }

    private void addVertexes(List<Long> stationIds) {
        for (Long stationId : stationIds) {
            graph.addVertex(stationId);
        }
    }

    private void addEdges(List<Section> sections) {
        for (Section section : sections) {
            graph.setEdgeWeight(graph.addEdge(section.getUpStationId(), section.getDownStationId()),
                    section.getDistance());
        }
    }

    public List<Long> getShortestPath(Long sourceId, Long targetId) {
        DijkstraShortestPath<Long, DefaultWeightedEdge> dijkstraShortestPath = new DijkstraShortestPath<>(graph);
        return dijkstraShortestPath.getPath(sourceId, targetId).getVertexList();
    }

    public int getShortestPathWeight(Long sourceId, Long targetId) {
        DijkstraShortestPath<Long, DefaultWeightedEdge> dijkstraShortestPath = new DijkstraShortestPath<>(graph);
        return (int) dijkstraShortestPath.getPath(sourceId, targetId).getWeight();
    }
}
