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
import wooteco.subway.domain.Path;
import wooteco.subway.domain.Section;
import wooteco.subway.domain.Station;
import wooteco.subway.dto.PathResponse;
import wooteco.subway.dto.StationResponse;
import wooteco.subway.exception.NotExistException;

@Service
@Transactional
public class PathService {

    private final StationDao stationDao;
    private final SectionDao sectionDao;
    private final LineDao lineDao;

    public PathService(StationDao stationDao, SectionDao sectionDao, LineDao lineDao) {
        this.stationDao = stationDao;
        this.sectionDao = sectionDao;
        this.lineDao = lineDao;
    }

    @Transactional(readOnly = true)
    public PathResponse findPath(Long sourceId, Long targetId, Integer age) {
        final List<Section> sections = sectionDao.findAll();
        final Path path = new Path(sections);

        final List<Long> shortestPath = path.getShortestPath(sourceId, targetId);
        final List<StationResponse> stations = getStationResponses(shortestPath);

        final int extraCost = getMaxExtraFareWithLine(shortestPath);
        final int distance = path.getShortestPathWeight(sourceId, targetId);
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

    private int getMaxExtraFareWithLine(List<Long> stationIds) {
        final List<Section> sections = sectionDao.findAllByStationIds(stationIds);
        final List<Line> lines = lineDao.findAllBySections(sections);

        return lines.stream()
                .mapToInt(Line::getExtraFare)
                .max()
                .orElse(0);
    }

    private Station findStation(Long id) {
        return stationDao.findById(id)
                .orElseThrow(() -> new NotExistException("찾으려는 역이 존재하지 않습니다."));
    }
}
