package wooteco.subway.domain;

public class Fare {

    private static final int BASIC_FARE = 1250;
    private static final int ADDITIONAL_FARE = 100;
    private static final int SECOND_ADDITIONAL_FARE = 800;
    private static final int BASIC_DISTANCE = 10;
    private static final int SURCHARGE_BOUNDARY = 50;
    private static final int FIRST_CHARGING_UNIT = 5;
    private static final int SECOND_CHARGING_UNIT = 8;
    private static final int ZERO_EXTRA_COST = 0;
    private static final int NO_DISCOUNT_AGE = 20;
    private static final int DEDUCT = 350;

    private final Integer distance;
    private final int extraCost;
    private final int age;

    public Fare(int distance) {
        this(distance, ZERO_EXTRA_COST, NO_DISCOUNT_AGE);
    }

    public Fare(Integer distance, int extraCost, int age) {
        this.distance = distance;
        this.extraCost = extraCost;
        this.age = age;
    }

    public int calculate() {
        final int calculateWithoutDiscount = calculateWithoutDiscount();

        int deductedAmount = calculateWithoutDiscount - DEDUCT;

        if (age >= 6 && age < 13) {
            final double discount = deductedAmount * 0.5;
            return calculateWithoutDiscount - (int) discount;
        }

        if (age >= 13 && age < 19) {
            final double discount = deductedAmount * 0.2;
            return calculateWithoutDiscount - (int) discount;
        }

        return calculateWithoutDiscount;
    }

    private int calculateWithoutDiscount() {
        if (distance <= BASIC_DISTANCE) {
            return BASIC_FARE + extraCost;
        }

        if (distance <= SURCHARGE_BOUNDARY) {
            return (BASIC_FARE + (int) Math.ceil(((double)(distance - BASIC_DISTANCE) / FIRST_CHARGING_UNIT)) * ADDITIONAL_FARE) + extraCost;
        }

        return (BASIC_FARE + SECOND_ADDITIONAL_FARE
                + (int) Math.ceil(((double)(distance - SURCHARGE_BOUNDARY) / SECOND_CHARGING_UNIT)) * ADDITIONAL_FARE) + extraCost;
    }
}
