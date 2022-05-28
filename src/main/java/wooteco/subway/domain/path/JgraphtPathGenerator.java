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
import wooteco.subway.domain.Station;

@Component
public class JgraphtPathGenerator implements PathGenerator {

    public JgraphtPathGenerator() {
    }

    @Override
    public Path generatePath(List<Section> sections, Station sourceStation, Station targetStation) {
        final WeightedMultigraph<Station, SectionEdge> graph = new WeightedMultigraph<>(SectionEdge.class);
        addVertexes(graph, getStationIds(sections));
        addEdges(graph, sections);

        final DijkstraShortestPath<Station, SectionEdge> dijkstraShortestPath = new DijkstraShortestPath<>(graph);
        final GraphPath<Station, SectionEdge> path = dijkstraShortestPath.getPath(sourceStation, targetStation);

        return new Path(getShortestPath(path), getShortestPathWeight(path), getShortestEdge(path));
    }

    private static List<Station> getStationIds(List<Section> sections) {
        final Set<Station> stations = new HashSet<>();

        for (Section section : sections) {
            stations.add(section.getUpStation());
            stations.add(section.getDownStation());
        }

        return new ArrayList<>(stations);
    }

    private static void addVertexes(WeightedMultigraph<Station, SectionEdge> graph, List<Station> stations) {
        for (Station station : stations) {
            graph.addVertex(station);
        }
    }

    private static void addEdges(WeightedMultigraph<Station, SectionEdge> graph, List<Section> sections) {
        for (Section section : sections) {
            final SectionEdge sectionEdge = new SectionEdge(section);
            graph.addEdge(section.getUpStation(), section.getDownStation(), sectionEdge);
        }
    }

    private List<Station> getShortestPath(GraphPath<Station, SectionEdge> path) {
        return path.getVertexList();
    }

    private int getShortestPathWeight(GraphPath<Station, SectionEdge> path) {
        return (int) path.getWeight();
    }

    private List<Section> getShortestEdge(GraphPath<Station, SectionEdge> path) {
        final List<SectionEdge> edges = path.getEdgeList();

        return edges.stream()
                .map(SectionEdge::getSection)
                .collect(toList());
    }
}
