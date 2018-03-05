package pro.crypto.moving.average;

import pro.crypto.model.IndicatorType;
import pro.crypto.model.PriceType;
import pro.crypto.model.result.MovingAverageResult;
import pro.crypto.model.tick.Tick;

import java.math.BigDecimal;

import static pro.crypto.model.IndicatorType.SMOOTHED_MOVING_AVERAGE;

public class SmoothedMovingAverage extends MovingAverage {

    private final Tick[] originalData;
    private final int period;

    SmoothedMovingAverage(Tick[] originalData, int period, PriceType priceType) {
        checkIncomingData(originalData, period, priceType);
        this.originalData = originalData;
        this.period = period;
        this.priceType = priceType;
    }

    @Override
    public IndicatorType getType() {
        return SMOOTHED_MOVING_AVERAGE;
    }

    @Override
    public void calculate() {
        initResultArray(originalData.length);
        fillStartPositions(originalData, period);
        fillStartIndicatorValue();
        fillAllPositions();
    }

    private void fillAllPositions() {
        for (int currentIndex = period; currentIndex < originalData.length; currentIndex++) {
            result[currentIndex] = buildMovingAverageResult(currentIndex);
        }
    }

    private MovingAverageResult buildMovingAverageResult(int currentIndex) {
        return new MovingAverageResult(
                originalData[currentIndex].getTickTime(),
                scaleAndRoundValue(extractPriceByType(originalData[currentIndex])),
                scaleAndRoundValue(countSmoothedAverage(currentIndex)));
    }

    private BigDecimal countSmoothedAverage(int currentIndex) {
        return ((result[currentIndex - 1].getIndicatorValue().multiply(new BigDecimal(period - 1)))
                .add(extractPriceByType(originalData[currentIndex])))
                .divide(new BigDecimal(period), BigDecimal.ROUND_HALF_UP);
    }

    private void fillStartIndicatorValue() {
        countSimpleAverage(0, period - 1, period, originalData);
    }

}
