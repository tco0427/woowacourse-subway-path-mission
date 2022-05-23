package wooteco.subway.domain;

import static java.util.stream.Collectors.toList;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.jgrapht.alg.shortestpath.DijkstraShortestPath;
import org.jgrapht.graph.WeightedMultigraph;
import wooteco.subway.domain.vo.SectionEdge;

public class Path {

    private final WeightedMultigraph<Long, SectionEdge> graph = new WeightedMultigraph<>(SectionEdge.class);

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
            final SectionEdge sectionEdge = new SectionEdge(section);
            graph.addEdge(section.getUpStationId(), section.getDownStationId(), sectionEdge);
        }
    }

    public List<Long> getShortestPath(Long sourceId, Long targetId) {
        DijkstraShortestPath<Long, SectionEdge> dijkstraShortestPath = new DijkstraShortestPath<>(graph);
        return dijkstraShortestPath.getPath(sourceId, targetId).getVertexList();
    }

    public int getShortestPathWeight(Long sourceId, Long targetId) {
        DijkstraShortestPath<Long, SectionEdge> dijkstraShortestPath = new DijkstraShortestPath<>(graph);
        return (int) dijkstraShortestPath.getPath(sourceId, targetId).getWeight();
    }

    public List<Section> getShortestEdge(Long sourceId, Long targetId) {
        DijkstraShortestPath<Long, SectionEdge> dijkstraShortestPath = new DijkstraShortestPath<>(graph);
        final List<SectionEdge> edges = dijkstraShortestPath.getPath(sourceId, targetId).getEdgeList();

        return edges.stream()
                .map(SectionEdge::getSection)
                .collect(toList());
    }
}
