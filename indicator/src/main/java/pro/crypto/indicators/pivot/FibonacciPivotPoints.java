package pro.crypto.indicators.pivot;

import pro.crypto.helper.MathHelper;
import pro.crypto.model.IndicatorType;
import pro.crypto.model.tick.Tick;

import java.math.BigDecimal;

import static pro.crypto.model.IndicatorType.FIBONACCI_PIVOT_POINTS;

public class FibonacciPivotPoints extends PivotPoints {

    private final static Double FIRST_COEFFICIENT = 0.382;
    private final static Double SECOND_COEFFICIENT = 0.618;
    private final static Double THIRD_COEFFICIENT = 1.0;

    FibonacciPivotPoints(Tick originalData) {
        super(originalData);
    }

    @Override
    public IndicatorType getType() {
        return FIBONACCI_PIVOT_POINTS;
    }

    @Override
    public void calculate() {
        calculatePivotPoints();
    }

    @Override
    BigDecimal calculatePivot() {
        return calculateDefaultPivot();
    }

    // PP + ((High - Low) x 0.382)
    @Override
    BigDecimal calculateFirstResistance() {
        return calculateResistance(FIRST_COEFFICIENT);
    }

    // PP + ((High - Low) x 0.618)
    @Override
    BigDecimal calculateSecondResistance() {
        return calculateResistance(SECOND_COEFFICIENT);
    }

    // PP + ((High - Low) x 1.000)
    @Override
    BigDecimal calculateThirdResistance() {
        return calculateResistance(THIRD_COEFFICIENT);
    }

    // PP - ((High - Low) x 0.382)
    @Override
    BigDecimal calculateFirstSupport() {
        return calculateSupport(FIRST_COEFFICIENT);
    }

    // PP - ((High - Low) x 0.618)
    @Override
    BigDecimal calculateSecondSupport() {
        return calculateSupport(SECOND_COEFFICIENT);
    }

    // S3 = PP - ((High - Low) x 1.000)
    @Override
    BigDecimal calculateThirdSupport() {
        return calculateSupport(THIRD_COEFFICIENT);
    }

    private BigDecimal calculateResistance(Double coefficient) {
        return MathHelper.scaleAndRound(pivot.add(originalData.getHigh()
                .subtract(originalData.getLow())
                .multiply(new BigDecimal(coefficient))));
    }

    private BigDecimal calculateSupport(Double coefficient) {
        return MathHelper.scaleAndRound(pivot.subtract(originalData.getHigh()
                .subtract(originalData.getLow())
                .multiply(new BigDecimal(coefficient))));
    }

}
