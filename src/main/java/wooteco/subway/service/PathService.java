package wooteco.subway.service;

import static java.util.stream.Collectors.toList;

import java.util.ArrayList;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import wooteco.subway.dao.LineDao;
import wooteco.subway.dao.SectionDao;
import wooteco.subway.dao.StationDao;
import wooteco.subway.domain.Fare;
import wooteco.subway.domain.Line;
import wooteco.subway.domain.Section;
import wooteco.subway.domain.Station;
import wooteco.subway.domain.path.Path;
import wooteco.subway.domain.path.PathGenerator;
import wooteco.subway.dto.PathResponse;
import wooteco.subway.dto.StationResponse;
import wooteco.subway.exception.NotExistException;

@Service
@Transactional
public class PathService {

    private final StationDao stationDao;
    private final SectionDao sectionDao;
    private final LineDao lineDao;
    private final PathGenerator pathGenerator;

    public PathService(StationDao stationDao, SectionDao sectionDao, LineDao lineDao, PathGenerator pathGenerator) {
        this.stationDao = stationDao;
        this.sectionDao = sectionDao;
        this.lineDao = lineDao;
        this.pathGenerator = pathGenerator;
    }

    @Transactional(readOnly = true)
    public PathResponse findPath(Long sourceId, Long targetId, Integer age) {
        final List<Section> sections = sectionDao.findAll();
        final Path path = pathGenerator.generatePath(sections, sourceId, targetId);

        final List<Long> shortestPath = path.getShortestPath();
        final List<StationResponse> stations = getStationResponses(shortestPath);
        final List<Section> shortestEdge = path.getShortestEdge();

        final int extraCost = getMaxExtraFareWithLine(shortestEdge);
        final int distance = path.getShortestPathWeight();
        final Fare fare = new Fare(distance, extraCost, age);

        return new PathResponse(stations, distance, fare.calculate());
    }

    private List<StationResponse> getStationResponses(List<Long> shortestPath) {
        final List<Station> stations = new ArrayList<>();
        for (Long id : shortestPath) {
            stations.add(findStation(id));
        }

        return stations.stream().sequential()
                .map(StationResponse::new)
                .collect(toList());
    }

    private int getMaxExtraFareWithLine(List<Section> sections) {
        final List<Long> lineIds = getLineIds(sections);
        final List<Line> lines = lineDao.findAllByIds(lineIds);

        return lines.stream()
                .mapToInt(Line::getExtraFare)
                .max()
                .orElse(0);
    }

    private List<Long> getLineIds(List<Section> sections) {
        return sections.stream()
                .map(Section::getLineId)
                .collect(toList());
    }

    private Station findStation(Long id) {
        return stationDao.findById(id)
                .orElseThrow(() -> new NotExistException("찾으려는 역이 존재하지 않습니다."));
    }
}
