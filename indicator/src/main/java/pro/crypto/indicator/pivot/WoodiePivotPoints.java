package pro.crypto.indicator.pivot;

import pro.crypto.helper.MathHelper;
import pro.crypto.model.IndicatorType;
import pro.crypto.model.tick.Tick;

import java.math.BigDecimal;

import static pro.crypto.model.IndicatorType.WOODIE_PIVOT_POINTS;

public class WoodiePivotPoints extends PivotPoints {

    WoodiePivotPoints(Tick[] oneDayData, Tick[] resultTickData) {
        super(oneDayData, resultTickData);
    }

    @Override
    public IndicatorType getType() {
        return WOODIE_PIVOT_POINTS;
    }

    // (H + L + 2 * C) / 4
    @Override
    BigDecimal calculatePivot(int currentIndex) {
        return MathHelper.average(
                originalData[currentIndex - 1].getHigh(),
                originalData[currentIndex - 1].getLow(),
                originalData[currentIndex - 1].getClose(),
                originalData[currentIndex - 1].getClose());
    }

    // 2 * P - L
    @Override
    BigDecimal calculateFirstResistance(int currentIndex) {
        return new BigDecimal(2).multiply(pivot)
                .subtract(originalData[currentIndex - 1].getLow());
    }

    // P + H - L
    @Override
    BigDecimal calculateSecondResistance(int currentIndex) {
        return pivot.add(originalData[currentIndex - 1].getHigh())
                .subtract(originalData[currentIndex - 1].getLow());
    }

    @Override
    BigDecimal calculateThirdResistance(int currentIndex) {
        return empty();
    }

    @Override
    BigDecimal calculateFourthResistance(int currentIndex) {
        return empty();
    }

    // 2 * P - H
    @Override
    BigDecimal calculateFirstSupport(int currentIndex) {
        return new BigDecimal(2).multiply(pivot)
                .subtract(originalData[currentIndex - 1].getHigh());
    }

    // P - H + L
    @Override
    BigDecimal calculateSecondSupport(int currentIndex) {
        return pivot.subtract(originalData[currentIndex - 1].getHigh())
                .add(originalData[currentIndex - 1].getLow());
    }

    @Override
    BigDecimal calculateThirdSupport(int currentIndex) {
        return empty();
    }

    @Override
    BigDecimal calculateFourthSupport(int currentIndex) {
        return empty();
    }

}
