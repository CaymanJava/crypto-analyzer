package pro.crypto.indicator.pivot;

import pro.crypto.helper.MathHelper;
import pro.crypto.model.indicator.IndicatorType;
import pro.crypto.model.tick.Tick;

import java.math.BigDecimal;

import static pro.crypto.model.indicator.IndicatorType.FIBONACCI_PIVOT_POINTS;

public class FibonacciPivotPoints extends PivotPoints {

    private final static BigDecimal FIRST_COEFFICIENT = new BigDecimal(0.382);
    private final static BigDecimal SECOND_COEFFICIENT = new BigDecimal(0.618);
    private final static BigDecimal THIRD_COEFFICIENT = new BigDecimal(1.0);

    FibonacciPivotPoints(Tick[] oneDayData, Tick[] resultTickData) {
        super(oneDayData, resultTickData);
    }

    @Override
    public IndicatorType getType() {
        return FIBONACCI_PIVOT_POINTS;
    }

    @Override
    BigDecimal calculatePivot(int currentIndex) {
        return calculateDefaultPivot(currentIndex);
    }

    // PP + ((High - Low) x 0.382)
    @Override
    BigDecimal calculateFirstResistance(int currentIndex) {
        return calculateResistance(FIRST_COEFFICIENT, currentIndex);
    }

    // PP + ((High - Low) x 0.618)
    @Override
    BigDecimal calculateSecondResistance(int currentIndex) {
        return calculateResistance(SECOND_COEFFICIENT, currentIndex);
    }

    // PP + ((High - Low) x 1.000)
    @Override
    BigDecimal calculateThirdResistance(int currentIndex) {
        return calculateResistance(THIRD_COEFFICIENT, currentIndex);
    }

    @Override
    BigDecimal calculateFourthResistance(int currentIndex) {
        return empty();
    }

    // PP - ((High - Low) x 0.382)
    @Override
    BigDecimal calculateFirstSupport(int currentIndex) {
        return calculateSupport(FIRST_COEFFICIENT, currentIndex);
    }

    // PP - ((High - Low) x 0.618)
    @Override
    BigDecimal calculateSecondSupport(int currentIndex) {
        return calculateSupport(SECOND_COEFFICIENT, currentIndex);
    }

    // S3 = PP - ((High - Low) x 1.000)
    @Override
    BigDecimal calculateThirdSupport(int currentIndex) {
        return calculateSupport(THIRD_COEFFICIENT, currentIndex);
    }

    @Override
    BigDecimal calculateFourthSupport(int currentIndex) {
        return empty();
    }

    private BigDecimal calculateResistance(BigDecimal coefficient, int currentIndex) {
        return MathHelper.scaleAndRound(pivot.add(originalData[currentIndex - 1].getHigh()
                .subtract(originalData[currentIndex - 1].getLow())
                .multiply(coefficient)));
    }

    private BigDecimal calculateSupport(BigDecimal coefficient, int currentIndex) {
        return MathHelper.scaleAndRound(pivot.subtract(originalData[currentIndex - 1].getHigh()
                .subtract(originalData[currentIndex - 1].getLow())
                .multiply(coefficient)));
    }

}
