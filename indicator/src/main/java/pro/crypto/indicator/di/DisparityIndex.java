package pro.crypto.indicator.di;

import pro.crypto.helper.IndicatorResultExtractor;
import pro.crypto.helper.MathHelper;
import pro.crypto.indicator.ma.MARequest;
import pro.crypto.indicator.ma.MovingAverageFactory;
import pro.crypto.model.Indicator;
import pro.crypto.model.IndicatorRequest;
import pro.crypto.model.IndicatorType;
import pro.crypto.model.SimpleIndicatorResult;
import pro.crypto.model.tick.PriceType;
import pro.crypto.model.tick.Tick;

import java.math.BigDecimal;
import java.util.stream.IntStream;

import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;
import static pro.crypto.model.IndicatorType.DISPARITY_INDEX;

public class DisparityIndex implements Indicator<DIResult> {

    private final Tick[] originalData;
    private final IndicatorType movingAverageType;
    private final int period;
    private final PriceType priceType;

    private DIResult[] result;

    public DisparityIndex(IndicatorRequest creationRequest) {
        DIRequest request = (DIRequest) creationRequest;
        this.originalData = request.getOriginalData();
        this.movingAverageType = request.getMovingAverageType();
        this.period = request.getPeriod();
        this.priceType = request.getPriceType();
        checkIncomingData();
    }

    @Override
    public IndicatorType getType() {
        return DISPARITY_INDEX;
    }

    @Override
    public void calculate() {
        result = new DIResult[originalData.length];
        BigDecimal[] movingAverageValues = calculateMovingAverageValues();
        calculateDisparityIndexResult(movingAverageValues);
    }

    @Override
    public DIResult[] getResult() {
        if (isNull(result)) {
            calculate();
        }
        return result;
    }

    private void checkIncomingData() {
        checkOriginalData(originalData);
        checkOriginalDataSize(originalData, period);
        checkPriceType(priceType);
        checkPeriod(period);
        checkMovingAverageType(movingAverageType);
    }

    private BigDecimal[] calculateMovingAverageValues() {
        return IndicatorResultExtractor.extractIndicatorValue(calculateMovingAverage());
    }

    private SimpleIndicatorResult[] calculateMovingAverage() {
        return MovingAverageFactory.create(buildMARequest()).getResult();
    }

    private IndicatorRequest buildMARequest() {
        return MARequest.builder()
                .originalData(originalData)
                .period(period)
                .indicatorType(movingAverageType)
                .priceType(priceType)
                .build();
    }

    private void calculateDisparityIndexResult(BigDecimal[] movingAverageValues) {
        IntStream.range(0, result.length)
                .forEach(idx -> result[idx] = new DIResult(
                        originalData[idx].getTickTime(),
                        calculateDisparityIndex(movingAverageValues[idx], idx)));
    }

    private BigDecimal calculateDisparityIndex(BigDecimal movingAverageValue, int currentIndex) {
        return nonNull(movingAverageValue)
                ? calculateDisparityIndexValue(movingAverageValue, currentIndex)
                : null;
    }

    // (Price(i) – MA(i)) / MA(i) * 100
    private BigDecimal calculateDisparityIndexValue(BigDecimal movingAverageValue, int currentIndex) {
        return MathHelper.divide(
                originalData[currentIndex].getPriceByType(priceType).subtract(movingAverageValue).multiply(new BigDecimal(100)),
                movingAverageValue
        );
    }

}
