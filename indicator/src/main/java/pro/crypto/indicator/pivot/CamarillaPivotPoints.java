package pro.crypto.indicator.pivot;

import pro.crypto.helper.MathHelper;
import pro.crypto.model.IndicatorType;
import pro.crypto.model.tick.Tick;

import java.math.BigDecimal;

import static pro.crypto.model.IndicatorType.CAMARILLA_PIVOT_POINTS;

public class CamarillaPivotPoints extends PivotPoints {

    CamarillaPivotPoints(Tick[] oneDayData, Tick[] resultTickData) {
        super(oneDayData, resultTickData);
    }

    @Override
    public IndicatorType getType() {
        return CAMARILLA_PIVOT_POINTS;
    }

    // (H + L + C) / 3
    @Override
    BigDecimal calculatePivot(int currentIndex) {
        return calculateDefaultPivot(currentIndex);
    }

    // (H - L) x 1.1 / 12 + C
    @Override
    BigDecimal calculateFirstResistance(int currentIndex) {
        return calculateResistance(calculateFirstCoefficient(), currentIndex);
    }

    // (H - L) x 1.1 / 6 + C
    @Override
    BigDecimal calculateSecondResistance(int currentIndex) {
        return calculateResistance(calculateSecondCoefficient(), currentIndex);
    }

    // (H - L) x 1.1 / 4 + C
    @Override
    BigDecimal calculateThirdResistance(int currentIndex) {
        return calculateResistance(calculateThirdCoefficient(), currentIndex);
    }

    @Override
        // (H - L) x 1.1 / 2 + C
    BigDecimal calculateFourthResistance(int currentIndex) {
        return calculateResistance(calculateFourthCoefficient(), currentIndex);
    }

    // C - (H - L) x 1.1 / 12
    @Override
    BigDecimal calculateFirstSupport(int currentIndex) {
        return calculateSupport(calculateFirstCoefficient(), currentIndex);
    }

    // C - (H - L) x 1.1 / 6
    @Override
    BigDecimal calculateSecondSupport(int currentIndex) {
        return calculateSupport(calculateSecondCoefficient(), currentIndex);
    }

    // C - (H - L) x 1.1 / 4
    @Override
    BigDecimal calculateThirdSupport(int currentIndex) {
        return calculateSupport(calculateThirdCoefficient(), currentIndex);
    }

    // C - (H - L) x 1.1 / 2
    @Override
    BigDecimal calculateFourthSupport(int currentIndex) {
        return calculateSupport(calculateFourthCoefficient(), currentIndex);
    }

    private BigDecimal calculateResistance(BigDecimal coefficient, int currentIndex) {
        return MathHelper.scaleAndRound(originalData[currentIndex - 1].getHigh()
                .subtract(originalData[currentIndex - 1].getLow()).multiply(coefficient)
                .add(originalData[currentIndex - 1].getClose()));
    }

    private BigDecimal calculateSupport(BigDecimal coefficient, int currentIndex) {
        return MathHelper.scaleAndRound(originalData[currentIndex - 1].getClose()
                .subtract(originalData[currentIndex - 1].getHigh()
                        .subtract(originalData[currentIndex - 1].getLow()).multiply(coefficient)));
    }

    private BigDecimal calculateFirstCoefficient() {
        return calculateCoefficient(12);
    }

    private BigDecimal calculateSecondCoefficient() {
        return calculateCoefficient(6);
    }

    private BigDecimal calculateThirdCoefficient() {
        return calculateCoefficient(4);
    }

    private BigDecimal calculateFourthCoefficient() {
        return calculateCoefficient(2);
    }

    private BigDecimal calculateCoefficient(int divisor) {
        return MathHelper.divide(new BigDecimal(1.1), new BigDecimal(divisor));
    }

}
