package pro.crypto.indicators.ma;

import pro.crypto.model.IndicatorType;
import pro.crypto.model.tick.PriceType;
import pro.crypto.model.tick.Tick;

public class SimpleMovingAverage extends MovingAverage {

    private final Tick[] originalData;
    private final int period;

    SimpleMovingAverage(Tick[] originalData, int period, PriceType priceType) {
        checkIncomingData(originalData, period, priceType);
        this.originalData = originalData;
        this.period = period;
        this.priceType = priceType;
    }

    @Override
    public IndicatorType getType() {
        return IndicatorType.SIMPLE_MOVING_AVERAGE;
    }

    @Override
    public void calculate() {
        initResultArray(this.originalData.length);
        fillInInitialPositions(originalData, period);
        fillInRemainPositions();
    }

    private void fillInRemainPositions() {
        int from = 0;
        for (int i = period - 1; i < originalData.length; i++) {
            countSimpleAverage(from, i, originalData);
            from++;
        }
    }

}
