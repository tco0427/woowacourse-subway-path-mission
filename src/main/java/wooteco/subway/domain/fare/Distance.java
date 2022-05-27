package wooteco.subway.domain.fare;

import java.util.Arrays;
import java.util.function.Predicate;

public enum Distance {

    BASIC(distance -> distance <= 10, 0, 0),
    NORMAL(distance -> 10 < distance && distance <= 50, 5, 0),
    FAR(distance -> 50 <= distance, 8, 800);

    private static final int BASIC_DISTANCE = 10;
    private static final int EXCESS_DISTANCE = 50;
    private static final int BASIC_FARE = 1250;
    private static final int ADDITIONAL_FARE = 100;

    private final Predicate<Integer> predicate;
    private final int unitOfRateMeasure;
    private final int additionalFareByDistance;

    Distance(Predicate<Integer> predicate, int unitOfRateMeasure, int additionalFareByDistance) {
        this.predicate = predicate;
        this.unitOfRateMeasure = unitOfRateMeasure;
        this.additionalFareByDistance = additionalFareByDistance;
    }

    public static Distance from(int distance) {
        return Arrays.stream(Distance.values())
                .filter(it -> it.predicate.test(distance))
                .findAny()
                .orElseThrow(() -> new IllegalArgumentException("일치하는 거리 없습니다."));
    }

    public int calculateAdditionalFare(int distance) {
        if (this == BASIC) {
            return BASIC_FARE;
        }
        if (this == NORMAL) {
            return (BASIC_FARE + calculateChargeCount(distance, BASIC_DISTANCE) * ADDITIONAL_FARE);
        }
        return (BASIC_FARE + calculateChargeCount(distance, EXCESS_DISTANCE) * ADDITIONAL_FARE
                + additionalFareByDistance);
    }

    private int calculateChargeCount(int distance, int basicDistance) {
        return (int) Math.ceil(((double) (distance - basicDistance) / unitOfRateMeasure));
    }
}
