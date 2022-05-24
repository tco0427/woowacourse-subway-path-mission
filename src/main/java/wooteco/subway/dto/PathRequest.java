package wooteco.subway.dto;

public class PathRequest {

    private Long source;
    private Long target;
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
