package pro.crypto.indicator.ma;

import pro.crypto.model.indicator.IndicatorType;
import pro.crypto.model.tick.PriceType;
import pro.crypto.model.tick.Tick;

import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.IntStream;

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
        AtomicInteger from = new AtomicInteger(0);
        IntStream.range(period - 1, originalData.length)
                .forEach(idx -> calculateSimpleAverage(from.getAndIncrement(), idx, originalData));
    }

}
