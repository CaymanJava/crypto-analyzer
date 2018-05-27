package pro.crypto.indicators.di;

import pro.crypto.helper.IndicatorResultExtractor;
import pro.crypto.helper.MathHelper;
import pro.crypto.indicators.ma.MovingAverageFactory;
import pro.crypto.model.Indicator;
import pro.crypto.model.IndicatorType;
import pro.crypto.model.request.DIRequest;
import pro.crypto.model.request.MARequest;
import pro.crypto.model.result.DIResult;
import pro.crypto.model.result.MAResult;
import pro.crypto.model.tick.PriceType;
import pro.crypto.model.tick.Tick;

import java.math.BigDecimal;

import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;
import static pro.crypto.model.IndicatorType.DISPARITY_INDEX;

public class DisparityIndex implements Indicator<DIResult> {

    private final Tick[] originalData;
    private final IndicatorType movingAverageType;
    private final int period;
    private final PriceType priceType;

    private DIResult[] result;

    public DisparityIndex(DIRequest request) {
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
        return IndicatorResultExtractor.extract(calculateMovingAverage());
    }

    private MAResult[] calculateMovingAverage() {
        return MovingAverageFactory.create(buildMARequest()).getResult();
    }

    private MARequest buildMARequest() {
        return MARequest.builder()
                .originalData(originalData)
                .period(period)
                .indicatorType(movingAverageType)
                .priceType(priceType)
                .build();
    }

    private void calculateDisparityIndexResult(BigDecimal[] movingAverageValues) {
        for (int currentIndex = 0; currentIndex < result.length; currentIndex++) {
            result[currentIndex] = new DIResult(
                    originalData[currentIndex].getTickTime(),
                    calculateDisparityIndex(movingAverageValues[currentIndex], currentIndex));
        }
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
