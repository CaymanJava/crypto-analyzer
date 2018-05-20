package pro.crypto.indicators.ao;

import pro.crypto.helper.*;
import pro.crypto.indicators.ma.MovingAverageFactory;
import pro.crypto.model.Indicator;
import pro.crypto.model.IndicatorType;
import pro.crypto.model.request.AORequest;
import pro.crypto.model.request.MARequest;
import pro.crypto.model.result.AOResult;
import pro.crypto.model.result.MAResult;
import pro.crypto.model.tick.Tick;

import java.math.BigDecimal;

import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;
import static pro.crypto.model.IndicatorType.AWESOME_OSCILLATOR;
import static pro.crypto.model.IndicatorType.SIMPLE_MOVING_AVERAGE;
import static pro.crypto.model.tick.PriceType.CLOSE;

public class AwesomeOscillator implements Indicator<AOResult> {

    private final Tick[] originalData;
    private final int slowPeriod;
    private final int fastPeriod;

    private AOResult[] result;

    public AwesomeOscillator(AORequest request) {
        this.originalData = request.getOriginalData();
        this.slowPeriod = request.getSlowPeriod();
        this.fastPeriod = request.getFastPeriod();
        checkIncomingData();
    }

    @Override
    public IndicatorType getType() {
        return AWESOME_OSCILLATOR;
    }

    @Override
    public void calculate() {
        result = new AOResult[originalData.length];
        BigDecimal[] medianPrices = MedianPriceCalculator.calculate(originalData);
        BigDecimal[] slowMovingAverage = calculateSlowMovingAverage(medianPrices);
        BigDecimal[] fastMovingAverage = calculateFastMovingAverage(medianPrices);
        BigDecimal[] awesomeOscillatorValues = calculateAwesomeOscillator(slowMovingAverage, fastMovingAverage);
        Boolean[] increasedFlags = IncreasedQualifier.define(awesomeOscillatorValues);
        buildAwesomeOscillatorResult(awesomeOscillatorValues, increasedFlags);
    }

    @Override
    public AOResult[] getResult() {
        if (isNull(result)) {
            calculate();
        }
        return result;
    }

    private void checkIncomingData() {
        checkOriginalData(originalData);
        checkOriginalDataSize(originalData, slowPeriod);
        checkOriginalDataSize(originalData, fastPeriod);
        checkPeriod(slowPeriod);
        checkPeriod(fastPeriod);
    }

    private BigDecimal[] calculateSlowMovingAverage(BigDecimal[] medianPrices) {
        return MAResultExtractor.extract(calculateSimpleMovingAverage(medianPrices, slowPeriod));
    }

    private BigDecimal[] calculateFastMovingAverage(BigDecimal[] medianPrices) {
        return MAResultExtractor.extract(calculateSimpleMovingAverage(medianPrices, fastPeriod));
    }

    private MAResult[] calculateSimpleMovingAverage(BigDecimal[] medianPrices, int period) {
        return MovingAverageFactory.create(buildMARequest(medianPrices, period)).getResult();
    }

    private MARequest buildMARequest(BigDecimal[] medianPrices, int period) {
        return MARequest.builder()
                .originalData(FakeTicksCreator.createWithCloseOnly(medianPrices))
                .period(period)
                .indicatorType(SIMPLE_MOVING_AVERAGE)
                .priceType(CLOSE)
                .build();
    }

    private BigDecimal[] calculateAwesomeOscillator(BigDecimal[] slowMovingAverage, BigDecimal[] fastMovingAverage) {
        BigDecimal[] awesomeOscillatorValues = new BigDecimal[originalData.length];
        for (int currentIndex = 0; currentIndex < awesomeOscillatorValues.length; currentIndex++) {
            awesomeOscillatorValues[currentIndex] = calculateAwesomeOscillator(slowMovingAverage[currentIndex], fastMovingAverage[currentIndex]);
        }
        return awesomeOscillatorValues;
    }

    private BigDecimal calculateAwesomeOscillator(BigDecimal slowMAValue, BigDecimal fastMAValue) {
        return nonNull(slowMAValue) && nonNull(fastMAValue)
                ? calculateAwesomeOscillatorValue(slowMAValue, fastMAValue)
                : null;
    }

    private BigDecimal calculateAwesomeOscillatorValue(BigDecimal slowMAValue, BigDecimal fastMAValue) {
        return MathHelper.scaleAndRound(slowMAValue.subtract(fastMAValue));
    }

    private void buildAwesomeOscillatorResult(BigDecimal[] awesomeOscillatorValues, Boolean[] increasedFlags) {
        for (int currentIndex = 0; currentIndex < result.length; currentIndex++) {
            result[currentIndex] = new AOResult(
                    originalData[currentIndex].getTickTime(),
                    awesomeOscillatorValues[currentIndex],
                    increasedFlags[currentIndex]);
        }
    }

}
