package pro.crypto.indicators.pivot;

import pro.crypto.helper.MathHelper;
import pro.crypto.model.IndicatorType;
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
        calculatePivotPoints();
    }

    @Override
    BigDecimal calculatePivot() {
        return calculateDefaultPivot();
    }

    // (2 * P) - L
    @Override
    BigDecimal calculateFirstResistance() {
        return MathHelper.scaleAndRound(pivot.add(pivot).subtract(originalData.getLow()));
    }

    // P + H - L
    @Override
    BigDecimal calculateSecondResistance() {
        return MathHelper.scaleAndRound(pivot.add(originalData.getHigh()).subtract(originalData.getLow()));
    }

    // H + 2 * (P - L)
    @Override
    BigDecimal calculateThirdResistance() {
        return MathHelper.scaleAndRound(pivot.subtract(originalData.getLow())
                .multiply(new BigDecimal(2))
                .add(originalData.getHigh()));
    }

    // (2 * P) - H
    @Override
    BigDecimal calculateFirstSupport() {
        return MathHelper.scaleAndRound(pivot.multiply(new BigDecimal(2)).subtract(originalData.getHigh()));
    }

    // P - H + L
    @Override
    BigDecimal calculateSecondSupport() {
        return MathHelper.scaleAndRound(pivot.subtract(originalData.getHigh()).add(originalData.getLow()));
    }

    // L - 2 * (H - P)
    @Override
    BigDecimal calculateThirdSupport() {
        return MathHelper.scaleAndRound(originalData.getLow().subtract(originalData.getHigh().subtract(pivot)
                .multiply(new BigDecimal(2))));
    }

}
