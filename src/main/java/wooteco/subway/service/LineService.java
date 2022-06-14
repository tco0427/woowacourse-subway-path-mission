package wooteco.subway.service;

import static java.util.stream.Collectors.toUnmodifiableList;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import wooteco.subway.dao.LineDao;
import wooteco.subway.dao.SectionDao;
import wooteco.subway.dao.StationDao;
import wooteco.subway.domain.Line;
import wooteco.subway.domain.Section;
import wooteco.subway.domain.Sections;
import wooteco.subway.domain.Station;
import wooteco.subway.dto.LineRequest;
import wooteco.subway.dto.LineResponse;
import wooteco.subway.dto.StationResponse;
import wooteco.subway.exception.NotExistException;

@Service
@Transactional
public class LineService {

    private static final int DELETE_FAIL = 0;

    private final LineDao lineDao;
    private final SectionDao sectionDao;
    private final StationDao stationDao;

    public LineService(LineDao lineDao, SectionDao sectionDao, StationDao stationDao) {
        this.lineDao = lineDao;
        this.sectionDao = sectionDao;
        this.stationDao = stationDao;
    }

    public LineResponse save(LineRequest request) {
        Line line = new Line(request.getName(), request.getColor(), request.getExtraFare());
        final Line savedLine = lineDao.save(line);

        final Station upStation = findStationById(request.getUpStationId());
        final Station downStation = findStationById(request.getDownStationId());
        final Section section = new Section(savedLine, upStation, downStation, request.getDistance());
        sectionDao.save(section);

        return new LineResponse(savedLine, makeStationResponseList(request));
    }

    @Transactional(readOnly = true)
    public LineResponse findById(Long id) {
        final Line line = findLineById(id);
        final Sections sections = new Sections(sectionDao.findByLineId(line.getId()));

        return new LineResponse(line, sortedStations(sections));
    }

    @Transactional(readOnly = true)
    public List<LineResponse> findAll() {
        return lineDao.findAll()
                .stream()
                .map(line -> new LineResponse(line, sortedStations(getSections(line))))
                .collect(toUnmodifiableList());
    }

    public Long updateByLine(Long id, LineRequest request) {
        final Line updateLine = new Line(id, request.getName(), request.getColor());
        return lineDao.updateByLine(updateLine);
    }

    public void deleteById(Long id) {
        final int isDeleted = lineDao.deleteById(id);

        if (isDeleted == DELETE_FAIL) {
            throw new NotExistException("존재하지 않는 노선입니다.");
        }
        sectionDao.deleteByLineId(id);
    }

    private List<StationResponse> makeStationResponseList(LineRequest request) {
        final Station upStation = findStationById(request.getUpStationId());
        final Station downStation = findStationById(request.getDownStationId());

        final StationResponse upStationResponse = new StationResponse(upStation);
        final StationResponse downStationResponse = new StationResponse(downStation);

        return List.of(upStationResponse, downStationResponse);
    }

    private Station findStationById(Long id) {
        return stationDao.findById(id)
                .orElseThrow(() -> new NotExistException("찾으려는 역이 존재하지 않습니다."));
    }

    private Line findLineById(Long id) {
        return lineDao.findById(id)
                .orElseThrow(() -> new NotExistException("찾으려는 노선이 존재하지 않습니다."));
    }

    private Sections getSections(Line line) {
        final List<Section> sections = sectionDao.findByLineId(line.getId());

        return new Sections(sections);
    }

    private List<StationResponse> sortedStations(Sections sections) {
        final List<Station> stations = getStations(sections.getSections());
        stations.sort(Comparator.comparing(Station::getId));
        return makeStationResponse(stations);
    }

    private List<Station> getStations(List<Section> sections) {
        Set<Station> stations = new HashSet<>();
        for (Section section : sections) {
            stations.add(section.getUpStation());
            stations.add(section.getDownStation());
        }
        return new ArrayList<>(stations);
    }

    private List<StationResponse> makeStationResponse(List<Station> stations) {
        List<StationResponse> stationResponses = new ArrayList<>();
        for (Station station : stations) {
            stationResponses.add(new StationResponse(station));
        }
        return stationResponses;
    }
}
