package wooteco.subway.domain;

import java.util.Arrays;
import java.util.function.Predicate;

public enum AgeSection {
    BABY(age -> age < 6, 1.0),
    CHILDREN(age -> 6 <= age && age < 13, 0.5),
    TEENAGER(age -> 13 <= age && age < 19, 0.2),
    ADULT(age -> 19 <= age, 0.0);

    public static final int DEDUCT = 350;

    private final Predicate<Integer> predicate;
    private final double discountRate;

    AgeSection(Predicate<Integer> predicate, double discountRate) {
        this.predicate = predicate;
        this.discountRate = discountRate;
    }

    public static AgeSection from(int age) {
        return Arrays.stream(AgeSection.values())
                .filter(it -> it.predicate.test(age))
                .findAny()
                .orElseThrow(() -> new IllegalArgumentException("일치하는 연령대가 없습니다."));
    }

    public int calculateDiscount(int fare) {
        final int deductedAmount = fare - DEDUCT;
        return (int) (deductedAmount * discountRate);
    }
}
