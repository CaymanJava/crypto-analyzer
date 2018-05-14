package pro.crypto.indicators.pivot;

import pro.crypto.helper.MathHelper;
import pro.crypto.model.IndicatorType;
import pro.crypto.model.result.PivotResult;
import pro.crypto.model.tick.Tick;

import java.math.BigDecimal;

import static pro.crypto.model.IndicatorType.WOODIE_PIVOT_POINTS;

public class WoodiePivotPoints extends PivotPoints {

    WoodiePivotPoints(Tick originalData) {
        super(originalData);
    }

    @Override
    public IndicatorType getType() {
        return WOODIE_PIVOT_POINTS;
    }

    @Override
    public void calculate() {
        result = new PivotResult[1];
        BigDecimal pivot = calculatePivot();
        BigDecimal firstResistance = calculateFirstResistance(pivot);
        BigDecimal secondResistance = calculateSecondResistance(pivot);
        BigDecimal firstSupport = calculateFirstSupport(pivot);
        BigDecimal secondSupport = calculateSecondSupport(pivot);
        result[0] = new PivotResult(originalData.getTickTime(), pivot,
                firstResistance, secondResistance, null, null,
                firstSupport, secondSupport, null, null);
    }

    // (H + L + 2 * C) / 4
    private BigDecimal calculatePivot() {
        return MathHelper.divide(
                originalData.getHigh().add(originalData.getLow()).add(new BigDecimal(2).multiply(originalData.getClose())),
                new BigDecimal(4));
    }

    // 2 * P - L
    private BigDecimal calculateFirstResistance(BigDecimal pivot) {
        return new BigDecimal(2).multiply(pivot).subtract(originalData.getLow());
    }

    // P + H - L
    private BigDecimal calculateSecondResistance(BigDecimal pivot) {
        return pivot.add(originalData.getHigh()).subtract(originalData.getLow());
    }

    // 2 * P - H
    private BigDecimal calculateFirstSupport(BigDecimal pivot) {
        return new BigDecimal(2).multiply(pivot).subtract(originalData.getHigh());
    }

    // P - H + L
    private BigDecimal calculateSecondSupport(BigDecimal pivot) {
        return pivot.subtract(originalData.getHigh()).add(originalData.getLow());
    }

}
