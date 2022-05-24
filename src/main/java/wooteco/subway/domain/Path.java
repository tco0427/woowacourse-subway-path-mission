package wooteco.subway.domain;

import static java.util.stream.Collectors.toList;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.jgrapht.GraphPath;
import org.jgrapht.alg.shortestpath.DijkstraShortestPath;
import org.jgrapht.graph.WeightedMultigraph;

public class Path {

    private final GraphPath<Long, SectionEdge> path;

    private Path(GraphPath<Long, SectionEdge> path) {
        this.path = path;
    }

    public static Path of(List<Section> sections, Long sourceId, Long targetId) {
        final WeightedMultigraph<Long, SectionEdge> graph = new WeightedMultigraph<>(SectionEdge.class);
        addVertexes(graph, getStationIds(sections));
        addEdges(graph, sections);

        final DijkstraShortestPath<Long, SectionEdge> dijkstraShortestPath = new DijkstraShortestPath<>(graph);
        return new Path(dijkstraShortestPath.getPath(sourceId, targetId));
    }

    private static List<Long> getStationIds(List<Section> sections) {
        final Set<Long> stationIds = new HashSet<>();

        for (Section section : sections) {
            stationIds.add(section.getUpStationId());
            stationIds.add(section.getDownStationId());
        }

        return new ArrayList<>(stationIds);
    }

    private static void addVertexes(WeightedMultigraph<Long, SectionEdge> graph, List<Long> stationIds) {
        for (Long stationId : stationIds) {
            graph.addVertex(stationId);
        }
    }

    private static void addEdges(WeightedMultigraph<Long, SectionEdge> graph, List<Section> sections) {
        for (Section section : sections) {
            final SectionEdge sectionEdge = new SectionEdge(section);
            graph.addEdge(section.getUpStationId(), section.getDownStationId(), sectionEdge);
        }
    }

    public List<Long> getShortestPath() {
        return path.getVertexList();
    }

    public int getShortestPathWeight() {
        return (int) path.getWeight();
    }

    public List<Section> getShortestEdge() {
        final List<SectionEdge> edges = path.getEdgeList();

        return edges.stream()
                .map(SectionEdge::getSection)
                .collect(toList());
    }
}
