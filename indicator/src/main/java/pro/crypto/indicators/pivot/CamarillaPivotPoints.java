package pro.crypto.indicators.pivot;

import pro.crypto.helper.MathHelper;
import pro.crypto.model.IndicatorType;
import pro.crypto.model.result.PivotResult;
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
        result = new PivotResult[1];
        BigDecimal pivot = calculatePivot();
        BigDecimal firstResistance = calculateFirstResistance();
        BigDecimal secondResistance = calculateSecondResistance();
        BigDecimal thirdResistance = calculateThirdResistance();
        BigDecimal fourthResistance = calculateFourthResistance();
        BigDecimal firstSupport = calculateFirstSupport();
        BigDecimal secondSupport = calculateSecondSupport();
        BigDecimal thirdSupport = calculateThirdSupport();
        BigDecimal fourthSupport = calculateFourthSupport();
        result[0] = new PivotResult(originalData.getTickTime(), pivot,
                firstResistance, secondResistance, thirdResistance, fourthResistance,
                firstSupport, secondSupport, thirdSupport, fourthSupport);
    }

    // (H + L + C) / 3
    private BigDecimal calculatePivot() {
        return MathHelper.divide(originalData.getHigh().add(originalData.getLow()).add(originalData.getClose()), new BigDecimal(3));
    }

    // (H - L) x 1.1 / 12 + C
    private BigDecimal calculateFirstResistance() {
        return calculateResistance(calculateFirstCoefficient());
    }

    // (H - L) x 1.1 / 6 + C
    private BigDecimal calculateSecondResistance() {
        return calculateResistance(calculateSecondCoefficient());
    }

    // (H - L) x 1.1 / 4 + C
    private BigDecimal calculateThirdResistance() {
        return calculateResistance(calculateThirdCoefficient());
    }

    private BigDecimal calculateFourthResistance() {
        return calculateResistance(calculateFourthCoefficient());
    }

    private BigDecimal calculateResistance(BigDecimal coefficient) {
        return MathHelper.scaleAndRound(originalData.getHigh().subtract(originalData.getLow()).multiply(coefficient).add(originalData.getClose()));
    }

    // C - (H - L) x 1.1 / 12
    private BigDecimal calculateFirstSupport() {
        return calculateSupport(calculateFirstCoefficient());
    }

    // C - (H - L) x 1.1 / 6
    private BigDecimal calculateSecondSupport() {
        return calculateSupport(calculateSecondCoefficient());
    }

    // C - (H - L) x 1.1 / 4
    private BigDecimal calculateThirdSupport() {
        return calculateSupport(calculateThirdCoefficient());
    }

    // C - (H - L) x 1.1 / 2
    private BigDecimal calculateFourthSupport() {
        return calculateSupport(calculateFourthCoefficient());
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
