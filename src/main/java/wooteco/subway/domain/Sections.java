package wooteco.subway.domain;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import wooteco.subway.exception.IllegalSectionException;

public class Sections {

    private static final int MINIMUM_SECTION_COUNT = 1;

    private List<Section> sections;

    public Sections(List<Section> sections) {
        this.sections = sortSections(sections);
    }

    public List<Section> add(Section section) {
        checkContainsSameSection(section);
        preventFork(section);
        validateRegistration(section);

        sections.add(section);
        sections = sortSections(sections);
        return List.copyOf(sections);
    }

    private void checkContainsSameSection(Section newSection) {
        final boolean contains = sections.stream()
                .anyMatch(section -> section.isSameSection(newSection));

        if (contains) {
            throw new IllegalSectionException("이미 동일한 구간이 등록되어 있습니다.");
        }
    }

    private void preventFork(Section newSection) {
        final Optional<Section> findSection = findForkSection(newSection);
        findSection.ifPresent(section -> processFork(section, newSection));
    }

    private Optional<Section> findForkSection(Section section) {
        return sections.stream()
                .filter(s -> s.isFork(section))
                .findAny();
    }

    private void processFork(Section existingSection, Section newSection) {
        checkDistance(existingSection, newSection);

        addSectionInMiddle(existingSection, newSection);

        sections.remove(existingSection);
    }

    private void checkDistance(Section existingSection, Section newSection) {
        if (existingSection.getDistance() <= newSection.getDistance()) {
            throw new IllegalSectionException("등록하려는 구간 길이가 기존 구간의 길이와 같거나 더 길 수 없습니다.");
        }
    }

    private void addSectionInMiddle(Section existingSection, Section newSection) {
        Section section = createNewSection(existingSection, newSection);

        sections.add(section);
    }

    private Section createNewSection(Section existingSection, Section newSection) {
        if (existingSection.getUpStation().equals(newSection.getUpStation())) {
            return new Section(existingSection.getId(),
                    existingSection.getLine(),
                    newSection.getDownStation(),
                    existingSection.getDownStation(),
                    existingSection.getDistance() - newSection.getDistance());
        }

        return new Section(existingSection.getId(), existingSection.getLine(), existingSection.getUpStation(),
                newSection.getUpStation(), existingSection.getDistance() - newSection.getDistance());
    }

    private void validateRegistration(Section section) {
        sections.stream()
                .filter(s -> s.containsStation(section))
                .findAny()
                .orElseThrow(() -> new IllegalSectionException("등록할 구간의 적어도 하나의 역은 등록되어 있어야 합니다."));
    }

    public List<Section> delete(Station station) {
        validateDeletableSize();

        final boolean existPreviousSection = hasPreviousSection(station);
        final boolean existLaterSection = hasLaterSection(station);

        removeFirstOrLastSection(existPreviousSection, existLaterSection);
        removeMiddleSection(existPreviousSection, existLaterSection, station);

        sections = sortSections(sections);
        return List.copyOf(sections);
    }

    private void validateDeletableSize() {
        if (sections.size() <= MINIMUM_SECTION_COUNT) {
            throw new IllegalSectionException("노선이 구간을 하나는 가져야하므로 구간을 제거할 수 없습니다.");
        }
    }

    private boolean hasPreviousSection(Station station) {
        return sections.stream()
                .anyMatch(section -> section.isSameDownStation(station));
    }

    private boolean hasLaterSection(Station station) {
        return sections.stream()
                .anyMatch(section -> section.isSameUpStation(station));
    }

    private void removeFirstOrLastSection(boolean existPreviousSection, boolean existLaterSection) {
        if (!existPreviousSection && existLaterSection) {
            sections.remove(0);
            return;
        }
        if (existPreviousSection && !existLaterSection) {
            sections.remove(sections.size() - 1);
        }
    }

    private void removeMiddleSection(boolean isExistPreviousSection, boolean isExistLaterSection, Station station) {
        if (isExistPreviousSection && isExistLaterSection) {
            deleteSection(station);
        }
    }

    private void deleteSection(Station station) {
        final Section previousSection = findPreviousSection(station);
        final Section laterSection = findLaterSection(station);

        final int distance = previousSection.getDistance() + laterSection.getDistance();
        final Section newSection = new Section(previousSection.getLine(), previousSection.getUpStation(),
                laterSection.getDownStation(), distance);

        sections.add(newSection);
        sections.remove(previousSection);
        sections.remove(laterSection);
    }

    private Section findPreviousSection(Station station) {
        return sections.stream()
                .filter(section -> section.isSameDownStation(station))
                .findAny()
                .orElseThrow(() -> new IllegalSectionException(
                        "삭제 이후 연결할 상행역이 존재하지 않아 구간 삭제가 불가능합니다."
                ));
    }

    private Section findLaterSection(Station station) {
        return sections.stream()
                .filter(section -> section.isSameUpStation(station))
                .findAny()
                .orElseThrow(() -> new IllegalSectionException(
                        "삭제 이후연결할 하행역이 존재하지 않아 구간 삭제가 불가능합니다."
                ));
    }

    public List<Section> getSections() {
        return List.copyOf(sections);
    }

    private List<Section> sortSections(List<Section> sections) {
        final List<Section> copySections = new ArrayList<>(List.copyOf(sections));

        final Section firstSection = findFirstSection(copySections);

        List<Section> sortedSections = new ArrayList<>();
        sortedSections.add(firstSection);

        concatenateSection(sortedSections, copySections);
        
        return sortedSections;
    }

    private Section findFirstSection(List<Section> copySections) {
        return copySections.stream()
                .filter(section -> isFirst(copySections, section.getUpStation()))
                .findAny()
                .orElseThrow();
    }

    private boolean isFirst(List<Section> copySections, Station upStation) {
        return copySections.stream()
                .noneMatch(section -> upStation.equals(section.getDownStation()));
    }

    private void concatenateSection(List<Section> sortedSections, List<Section> copySections) {
        while (sortedSections.size() != copySections.size()) {
            final Section lastSection = sortedSections.get(sortedSections.size() - 1);
            final Station lastDownStation = lastSection.getDownStation();

            checkAndConcatenate(sortedSections, copySections, lastDownStation);
        }
    }

    private void checkAndConcatenate(List<Section> sortedSections, List<Section> copySections, Station lastDownStation) {
        for (Section section : copySections) {
            moveOneByOne(sortedSections, lastDownStation, section);
        }
    }

    private void moveOneByOne(List<Section> sortedSections, Station lastDownStation, Section section) {
        if (section.getUpStation().equals(lastDownStation)) {
            sortedSections.add(section);
        }
    }
}
