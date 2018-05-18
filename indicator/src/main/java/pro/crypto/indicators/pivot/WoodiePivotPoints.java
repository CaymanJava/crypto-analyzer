package pro.crypto.indicators.pivot;

import pro.crypto.helper.MathHelper;
import pro.crypto.model.IndicatorType;
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
        calculatePivotPoints();
    }

    // (H + L + 2 * C) / 4
    @Override
    BigDecimal calculatePivot() {
        return MathHelper.divide(
                originalData.getHigh().add(originalData.getLow()).add(new BigDecimal(2).multiply(originalData.getClose())),
                new BigDecimal(4));
    }

    // 2 * P - L
    @Override
    BigDecimal calculateFirstResistance() {
        return new BigDecimal(2).multiply(pivot).subtract(originalData.getLow());
    }

    // P + H - L
    @Override
    BigDecimal calculateSecondResistance() {
        return pivot.add(originalData.getHigh()).subtract(originalData.getLow());
    }

    // 2 * P - H
    @Override
    BigDecimal calculateFirstSupport() {
        return new BigDecimal(2).multiply(pivot).subtract(originalData.getHigh());
    }

    // P - H + L
    @Override
    BigDecimal calculateSecondSupport() {
        return pivot.subtract(originalData.getHigh()).add(originalData.getLow());
    }

}
