package wooteco.subway.ui;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import wooteco.subway.dto.PathRequest;
import wooteco.subway.dto.PathResponse;
import wooteco.subway.service.PathService;

@RestController
public class PathController {

    private final PathService pathService;

    public PathController(PathService pathService) {
        this.pathService = pathService;
    }

    @GetMapping("/paths")
    public ResponseEntity<PathResponse> findPath(PathRequest pathRequest) {
        final PathResponse response = pathService.findPath(pathRequest.getSource(), pathRequest.getTarget(),
                pathRequest.getAge());
        return ResponseEntity.ok().body(response);
    }
}
