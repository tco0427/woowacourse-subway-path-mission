package wooteco.subway.domain;

public class Fare {

    public static final int BASIC_FARE = 1250;
    public static final int ADDITIONAL_FARE = 100;

    private final int distance;

    public Fare(int distance) {
        this.distance = distance;
    }


    public int calculate() {
        if (distance <= 10) {
            return BASIC_FARE;
        }

        if (distance <= 50) {
            return BASIC_FARE + (int) Math.ceil(((double)(distance - 10) / 5)) * ADDITIONAL_FARE;
        }

        return BASIC_FARE + 800 + (int) Math.ceil(((double)(distance - 50) / 8)) * ADDITIONAL_FARE;
    }
}
