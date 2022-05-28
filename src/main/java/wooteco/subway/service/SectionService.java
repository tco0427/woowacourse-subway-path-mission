package wooteco.subway.service;

import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import wooteco.subway.dao.LineDao;
import wooteco.subway.dao.SectionDao;
import wooteco.subway.dao.StationDao;
import wooteco.subway.domain.Line;
import wooteco.subway.domain.Section;
import wooteco.subway.domain.Sections;
import wooteco.subway.domain.Station;
import wooteco.subway.dto.SectionRequest;
import wooteco.subway.exception.NotExistException;

@Service
@Transactional
public class SectionService {

    private final SectionDao sectionDao;
    private final LineDao lineDao;
    private final StationDao stationDao;

    public SectionService(SectionDao sectionDao, LineDao lineDao, StationDao stationDao) {
        this.sectionDao = sectionDao;
        this.lineDao = lineDao;
        this.stationDao = stationDao;
    }

    public void save(Long lineId, SectionRequest request) {
        final Sections sections = new Sections(sectionDao.findByLineId(lineId));
        final Section section = createSection(lineId, request);

        List<Section> result = sections.add(section);
        updateSection(lineId, result);
    }

    private Section createSection(Long lineId, SectionRequest request) {
        final Line line = findLine(lineId);
        final Station upStation = findStation(request.getUpStationId());
        final Station downStation = findStation(request.getDownStationId());

        return new Section(line, upStation, downStation, request.getDistance());
    }

    public void delete(Long lineId, Long stationId) {
        final Sections sections = new Sections(sectionDao.findByLineId(lineId));
        final Station station = findStation(stationId);

        List<Section> result = sections.delete(station);
        updateSection(lineId, result);
    }

    private void updateSection(Long lineId, List<Section> sections) {
        sectionDao.deleteByLineId(lineId);
        sectionDao.saveAll(sections);
    }

    private Station findStation(Long id) {
        return stationDao.findById(id)
                .orElseThrow(() -> new NotExistException("찾으려는 역이 존재하지 않습니다."));
    }

    private Line findLine(Long id) {
        return lineDao.findById(id)
                .orElseThrow(() -> new NotExistException("찾으려는 노선이 존재하지 않습니다."));
    }
}
