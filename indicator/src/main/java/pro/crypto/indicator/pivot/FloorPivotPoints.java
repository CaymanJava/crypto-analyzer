package pro.crypto.indicator.pivot;

import pro.crypto.helper.MathHelper;
import pro.crypto.model.IndicatorType;
import pro.crypto.model.tick.Tick;

import java.math.BigDecimal;

import static pro.crypto.model.IndicatorType.FLOOR_PIVOT_POINTS;

public class FloorPivotPoints extends PivotPoints {

    FloorPivotPoints(Tick[] oneDayData, Tick[] resultTickData) {
        super(oneDayData, resultTickData);
    }

    @Override
    public IndicatorType getType() {
        return FLOOR_PIVOT_POINTS;
    }

    @Override
    BigDecimal calculatePivot(int currentIndex) {
        return calculateDefaultPivot(currentIndex);
    }

    // (2 * P) - L
    @Override
    BigDecimal calculateFirstResistance(int currentIndex) {
        return MathHelper.scaleAndRound(pivot.add(pivot)
                .subtract(originalData[currentIndex - 1].getLow()));
    }

    // P + H - L
    @Override
    BigDecimal calculateSecondResistance(int currentIndex) {
        return MathHelper.scaleAndRound(pivot.add(originalData[currentIndex - 1].getHigh())
                .subtract(originalData[currentIndex - 1].getLow()));
    }

    // H + 2 * (P - L)
    @Override
    BigDecimal calculateThirdResistance(int currentIndex) {
        return MathHelper.scaleAndRound(pivot.subtract(originalData[currentIndex - 1].getLow())
                .multiply(new BigDecimal(2))
                .add(originalData[currentIndex - 1].getHigh()));
    }

    @Override
    BigDecimal calculateFourthResistance(int currentIndex) {
        return empty();
    }

    // (2 * P) - H
    @Override
    BigDecimal calculateFirstSupport(int currentIndex) {
        return MathHelper.scaleAndRound(pivot.multiply(new BigDecimal(2))
                .subtract(originalData[currentIndex - 1].getHigh()));
    }

    // P - H + L
    @Override
    BigDecimal calculateSecondSupport(int currentIndex) {
        return MathHelper.scaleAndRound(pivot.subtract(originalData[currentIndex - 1].getHigh())
                .add(originalData[currentIndex - 1].getLow()));
    }

    // L - 2 * (H - P)
    @Override
    BigDecimal calculateThirdSupport(int currentIndex) {
        return MathHelper.scaleAndRound(originalData[currentIndex - 1].getLow()
                .subtract(originalData[currentIndex - 1].getHigh().subtract(pivot)
                        .multiply(new BigDecimal(2))));
    }

    @Override
    BigDecimal calculateFourthSupport(int currentIndex) {
        return empty();
    }

}
