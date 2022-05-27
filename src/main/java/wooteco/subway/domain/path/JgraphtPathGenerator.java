package wooteco.subway.domain.path;

import static java.util.stream.Collectors.toList;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.jgrapht.GraphPath;
import org.jgrapht.alg.shortestpath.DijkstraShortestPath;
import org.jgrapht.graph.WeightedMultigraph;
import org.springframework.stereotype.Component;
import wooteco.subway.domain.Section;
import wooteco.subway.domain.SectionEdge;

@Component
public class JgraphtPathGenerator implements PathGenerator {

    public JgraphtPathGenerator() {
    }

    @Override
    public Path generatePath(List<Section> sections, Long sourceId, Long targetId) {
        final WeightedMultigraph<Long, SectionEdge> graph = new WeightedMultigraph<>(SectionEdge.class);
        addVertexes(graph, getStationIds(sections));
        addEdges(graph, sections);

        final DijkstraShortestPath<Long, SectionEdge> dijkstraShortestPath = new DijkstraShortestPath<>(graph);
        final GraphPath<Long, SectionEdge> path = dijkstraShortestPath.getPath(sourceId, targetId);

        return new Path(getShortestPath(path), getShortestPathWeight(path), getShortestEdge(path));
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

    private List<Long> getShortestPath(GraphPath<Long, SectionEdge> path) {
        return path.getVertexList();
    }

    private int getShortestPathWeight(GraphPath<Long, SectionEdge> path) {
        return (int) path.getWeight();
    }

    private List<Section> getShortestEdge(GraphPath<Long, SectionEdge> path) {
        final List<SectionEdge> edges = path.getEdgeList();

        return edges.stream()
                .map(SectionEdge::getSection)
                .collect(toList());
    }
}
