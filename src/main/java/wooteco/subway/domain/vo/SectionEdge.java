package wooteco.subway.domain.vo;

import org.jgrapht.graph.DefaultWeightedEdge;
import wooteco.subway.domain.Section;

public class SectionEdge extends DefaultWeightedEdge {

    private final Section section;

    public SectionEdge(Section section) {
        this.section = section;
    }

    public Section getSection() {
        return section;
    }

    protected double getWeight() {
        return section.getDistance();
    }
}
