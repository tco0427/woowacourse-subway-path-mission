package wooteco.subway.domain;

public class Fare {

    public static final int BASIC_FARE = 1250;
    public static final int ADDITIONAL_FARE = 100;
    private static final int SECOND_ADDITIONAL_FARE = 800;
    private static final int BASIC_DISTANCE = 10;
    private static final int SURCHARGE_BOUNDARY = 50;
    private static final int FIRST_CHARGING_UNIT = 5;
    private static final int SECOND_CHARGING_UNIT = 8;

    private final int distance;

    public Fare(int distance) {
        this.distance = distance;
    }

    public int calculate() {
        if (distance <= BASIC_DISTANCE) {
            return BASIC_FARE;
        }

        if (distance <= SURCHARGE_BOUNDARY) {
            return BASIC_FARE + (int) Math.ceil(((double)(distance - BASIC_DISTANCE) / FIRST_CHARGING_UNIT)) * ADDITIONAL_FARE;
        }

        return BASIC_FARE + SECOND_ADDITIONAL_FARE
                + (int) Math.ceil(((double)(distance - SURCHARGE_BOUNDARY) / SECOND_CHARGING_UNIT)) * ADDITIONAL_FARE;
    }
}
