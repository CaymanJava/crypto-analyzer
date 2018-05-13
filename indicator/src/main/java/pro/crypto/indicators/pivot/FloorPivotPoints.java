package pro.crypto.indicators.pivot;

import pro.crypto.helper.MathHelper;
import pro.crypto.model.IndicatorType;
import pro.crypto.model.result.PivotResult;
import pro.crypto.model.tick.Tick;

import java.math.BigDecimal;

import static pro.crypto.model.IndicatorType.FLOOR_PIVOT_POINTS;

public class FloorPivotPoints extends PivotPoints {

    FloorPivotPoints(Tick originalData) {
        super(originalData);
    }

    @Override
    public IndicatorType getType() {
        return FLOOR_PIVOT_POINTS;
    }

    @Override
    public void calculate() {
        result = new PivotResult[1];
        BigDecimal pivot = calculatePivot();
        BigDecimal firstResistance = calculateFirstResistance(pivot);
        BigDecimal secondResistance = calculateSecondResistance(pivot);
        BigDecimal thirdResistance = calculateThirdResistance(pivot);
        BigDecimal firstSupport = calculateFirstSupport(pivot);
        BigDecimal secondSupport = calculateSecondSupport(pivot);
        BigDecimal thirdSupport = calculateThirdSupport(pivot);
        result[0] = new PivotResult(originalData.getTickTime(), pivot,
                firstResistance, secondResistance, thirdResistance, null,
                firstSupport, secondSupport, thirdSupport, null);
    }

    // (H + L + C) / 3
    private BigDecimal calculatePivot() {
        return MathHelper.divide(originalData.getHigh().add(originalData.getLow()).add(originalData.getClose()), new BigDecimal(3));
    }

    // (2 * P) - L
    private BigDecimal calculateFirstResistance(BigDecimal pivot) {
        return MathHelper.scaleAndRound(pivot.add(pivot).subtract(originalData.getLow()));
    }

    // P + H - L
    private BigDecimal calculateSecondResistance(BigDecimal pivot) {
        return MathHelper.scaleAndRound(pivot.add(originalData.getHigh()).subtract(originalData.getLow()));
    }

    // H + 2 * (P - L)
    private BigDecimal calculateThirdResistance(BigDecimal pivot) {
        return MathHelper.scaleAndRound(pivot.subtract(originalData.getLow()).multiply(new BigDecimal(2)).add(originalData.getHigh()));
    }

    // (2 * P) - H
    private BigDecimal calculateFirstSupport(BigDecimal pivot) {
        return MathHelper.scaleAndRound(pivot.multiply(new BigDecimal(2)).subtract(originalData.getHigh()));
    }

    // P - H + L
    private BigDecimal calculateSecondSupport(BigDecimal pivot) {
        return MathHelper.scaleAndRound(pivot.subtract(originalData.getHigh()).add(originalData.getLow()));
    }

    // L - 2 * (H - P)
    private BigDecimal calculateThirdSupport(BigDecimal pivot) {
        return MathHelper.scaleAndRound(originalData.getLow().subtract(originalData.getHigh().subtract(pivot).multiply(new BigDecimal(2))));
    }

}
