package wooteco.subway.domain.fare;

public class Fare {

    private static final int ZERO_EXTRA_COST = 0;
    private static final int NO_DISCOUNT_AGE = 20;

    private final int moveDistance;
    private final int extraCost;
    private final int age;

    public Fare(int moveDistance) {
        this(moveDistance, ZERO_EXTRA_COST, NO_DISCOUNT_AGE);
    }

    public Fare(int moveDistance, int extraCost, int age) {
        this.moveDistance = moveDistance;
        this.extraCost = extraCost;
        this.age = age;
    }

    public int calculate() {
        final Distance distance = Distance.from(moveDistance);
        final int fare = distance.calculateAdditionalFare(moveDistance);
        final int calculateWithoutDiscount = fare + extraCost;

        final AgeSection ageSection = AgeSection.from(age);
        final int discount = ageSection.calculateDiscount(calculateWithoutDiscount);

        return calculateWithoutDiscount - discount;
    }
}
