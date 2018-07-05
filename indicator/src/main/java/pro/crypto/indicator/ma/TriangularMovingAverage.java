package pro.crypto.indicator.ma;

import pro.crypto.helper.FakeTicksCreator;
import pro.crypto.helper.IndicatorResultExtractor;
import pro.crypto.model.IndicatorType;
import pro.crypto.model.tick.PriceType;
import pro.crypto.model.tick.Tick;

import java.math.BigDecimal;
import java.util.stream.IntStream;

import static pro.crypto.model.IndicatorType.SIMPLE_MOVING_AVERAGE;
import static pro.crypto.model.IndicatorType.TRIANGULAR_MOVING_AVERAGE;

public class TriangularMovingAverage extends MovingAverage {

    private final Tick[] originalData;
    private final int period;

    TriangularMovingAverage(Tick[] originalData, int period, PriceType priceType) {
        checkIncomingData(originalData, period, priceType);
        this.originalData = originalData;
        this.period = period;
        this.priceType = priceType;
    }

    @Override
    public IndicatorType getType() {
        return TRIANGULAR_MOVING_AVERAGE;
    }

    @Override
    public void calculate() {
        result = new MAResult[originalData.length];
        BigDecimal[] simpleMovingAverageValues = calculateSimpleMovingAverageValues();
        calculateTriangularMovingAverage(simpleMovingAverageValues);
    }

    private BigDecimal[] calculateSimpleMovingAverageValues() {
        return IndicatorResultExtractor.extract(calculateMovingAverage(originalData, calculateSMAPeriod()));
    }

    private int calculateSMAPeriod() {
        return isPeriodEven() ? period / 2 : period / 2 + 1;
    }

    private void calculateTriangularMovingAverage(BigDecimal[] simpleMovingAverageValues) {
        BigDecimal[] triangularMovingAverageValues = calculateTriangularMovingAverageValues(simpleMovingAverageValues);
        IntStream.range(0, result.length)
                .forEach(idx -> result[idx] = new MAResult(originalData[idx].getTickTime(), triangularMovingAverageValues[idx]));
    }

    private BigDecimal[] calculateTriangularMovingAverageValues(BigDecimal[] simpleMovingAverageValues) {
        BigDecimal[] movingAverageValues = IndicatorResultExtractor.extract(
                calculateMovingAverage(
                        FakeTicksCreator.createWithCloseOnly(simpleMovingAverageValues), period / 2 + 1));
        BigDecimal[] result = new BigDecimal[originalData.length];
        System.arraycopy(movingAverageValues, 0, result, defineCopyStartPosition(), movingAverageValues.length);
        return result;
    }

    private int defineCopyStartPosition() {
        return isPeriodEven() ? period / 2 - 1 : period / 2;
    }

    private MAResult[] calculateMovingAverage(Tick[] data, int period) {
        return MovingAverageFactory.create(buildMARequest(data, period)).getResult();
    }

    private MARequest buildMARequest(Tick[] data, int period) {
        return MARequest.builder()
                .originalData(data)
                .period(period)
                .priceType(priceType)
                .indicatorType(SIMPLE_MOVING_AVERAGE)
                .build();
    }

    private boolean isPeriodEven() {
        return period % 2 == 0;
    }

}
