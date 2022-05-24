package wooteco.subway.dto;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

public class LineRequest {

    @NotBlank(message = "이름이 공백일 수 없습니다.")
    @Size(max = 255)
    private String name;

    @NotBlank(message = "노선의 색이 공백일 수 없습니다.")
    @Size(max = 255)
    private String color;

    @NotNull(message = "노선의 상행역을 입력해주세요.")
    private Long upStationId;

    @NotNull(message = "노선의 하행역을 입력해주세요.")
    private Long downStationId;

    @Min(value = 1, message = "거리는 1보다 크거나 같아야합니다.")
    private int distance;

    private int extraFare;

    public LineRequest() {
    }

    public LineRequest(String name, String color, Long upStationId, Long downStationId, int distance) {
        this(name, color, upStationId, downStationId, distance, 0);
    }

    public LineRequest(String name, String color, Long upStationId, Long downStationId, int distance, int extraFare) {
        this.name = name;
        this.color = color;
        this.upStationId = upStationId;
        this.downStationId = downStationId;
        this.distance = distance;
        this.extraFare = extraFare;
    }

    public String getName() {
        return name;
    }

    public String getColor() {
        return color;
    }

    public Long getUpStationId() {
        return upStationId;
    }

    public Long getDownStationId() {
        return downStationId;
    }

    public int getDistance() {
        return distance;
    }

    public int getExtraFare() {
        return extraFare;
    }
}
