package wooteco.subway.dto;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

public class PathRequest {

    @NotNull(message = "찾으려는 경로의 출발역을 입력해주세요.")
    private Long source;

    @NotNull(message = "찾으려는 경로의 도착역을 입력해주세요.")
    private Long target;

    @Min(value = 1, message = "나이는 양수여야 합니다.")
    private Integer age;

    public PathRequest(Long source, Long target, Integer age) {
        this.source = source;
        this.target = target;
        this.age = age;
    }

    public Long getSource() {
        return source;
    }

    public Long getTarget() {
        return target;
    }

    public Integer getAge() {
        return age;
    }
}
