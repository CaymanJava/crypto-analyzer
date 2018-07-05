package pro.crypto.indicator.pivot;

import pro.crypto.helper.MathHelper;
import pro.crypto.model.IndicatorType;
import pro.crypto.model.tick.Tick;

import java.math.BigDecimal;

import static pro.crypto.model.IndicatorType.CAMARILLA_PIVOT_POINTS;

public class CamarillaPivotPoints extends PivotPoints {

    CamarillaPivotPoints(Tick originalData) {
        super(originalData);
    }

    @Override
    public IndicatorType getType() {
        return CAMARILLA_PIVOT_POINTS;
    }

    @Override
    public void calculate() {
        calculatePivotPoints();
    }

    // (H + L + C) / 3
    @Override
    BigDecimal calculatePivot() {
        return calculateDefaultPivot();
    }

    // (H - L) x 1.1 / 12 + C
    @Override
    BigDecimal calculateFirstResistance() {
        return calculateResistance(calculateFirstCoefficient());
    }

    // (H - L) x 1.1 / 6 + C
    @Override
    BigDecimal calculateSecondResistance() {
        return calculateResistance(calculateSecondCoefficient());
    }

    // (H - L) x 1.1 / 4 + C
    @Override
    BigDecimal calculateThirdResistance() {
        return calculateResistance(calculateThirdCoefficient());
    }

    @Override
        // (H - L) x 1.1 / 2 + C
    BigDecimal calculateFourthResistance() {
        return calculateResistance(calculateFourthCoefficient());
    }

    // C - (H - L) x 1.1 / 12
    @Override
    BigDecimal calculateFirstSupport() {
        return calculateSupport(calculateFirstCoefficient());
    }

    // C - (H - L) x 1.1 / 6
    @Override
    BigDecimal calculateSecondSupport() {
        return calculateSupport(calculateSecondCoefficient());
    }

    // C - (H - L) x 1.1 / 4
    @Override
    BigDecimal calculateThirdSupport() {
        return calculateSupport(calculateThirdCoefficient());
    }

    // C - (H - L) x 1.1 / 2
    @Override
    BigDecimal calculateFourthSupport() {
        return calculateSupport(calculateFourthCoefficient());
    }

    private BigDecimal calculateResistance(BigDecimal coefficient) {
        return MathHelper.scaleAndRound(originalData.getHigh().subtract(originalData.getLow()).multiply(coefficient).add(originalData.getClose()));
    }

    private BigDecimal calculateSupport(BigDecimal coefficient) {
        return MathHelper.scaleAndRound(originalData.getClose().subtract(originalData.getHigh().subtract(originalData.getLow()).multiply(coefficient)));
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
