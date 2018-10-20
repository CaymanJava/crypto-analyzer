package pro.crypto.indicator.ma;

import pro.crypto.helper.IndicatorResultExtractor;
import pro.crypto.indicator.lr.LRRequest;
import pro.crypto.indicator.lr.LinearRegression;
import pro.crypto.model.IndicatorRequest;
import pro.crypto.model.IndicatorType;
import pro.crypto.model.SimpleIndicatorResult;
import pro.crypto.model.tick.PriceType;
import pro.crypto.model.tick.Tick;

import java.math.BigDecimal;
import java.util.stream.IntStream;

import static pro.crypto.model.IndicatorType.TIME_SERIES_MOVING_AVERAGE;

public class TimeSeriesMovingAverage extends MovingAverage {

    private final Tick[] originalData;
    private final int period;

    public TimeSeriesMovingAverage(Tick[] originalData, int period, PriceType priceType) {
        this.originalData = originalData;
        this.period = period;
        this.priceType = priceType;
        checkIncomingData(originalData, period, priceType);
    }

    @Override
    public IndicatorType getType() {
        return TIME_SERIES_MOVING_AVERAGE;
    }

    @Override
    public void calculate() {
        BigDecimal[] linearRegressionValues = calculateLinearRegressionValues();
        buildTimeSeriesMovingAverageResult(linearRegressionValues);
    }

    private BigDecimal[] calculateLinearRegressionValues() {
        return IndicatorResultExtractor.extractIndicatorValues(calculateLinearRegression());
    }

    private SimpleIndicatorResult[] calculateLinearRegression() {
        return new LinearRegression(buildLRRequest()).getResult();
    }

    private IndicatorRequest buildLRRequest() {
        return LRRequest.builder()
                .originalData(originalData)
                .priceType(priceType)
                .period(period)
                .averageCalculation(false)
                .build();
    }

    private void buildTimeSeriesMovingAverageResult(BigDecimal[] linearRegressionValues) {
        result = IntStream.range(0, originalData.length)
                .mapToObj(idx -> new MAResult(originalData[idx].getTickTime(), linearRegressionValues[idx]))
                .toArray(MAResult[]::new);
    }

}
