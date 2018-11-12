package pro.crypto.indicator.ao;

import pro.crypto.helper.*;
import pro.crypto.indicator.ma.MARequest;
import pro.crypto.indicator.ma.MovingAverageFactory;
import pro.crypto.model.Indicator;
import pro.crypto.model.IndicatorRequest;
import pro.crypto.model.IndicatorType;
import pro.crypto.model.SimpleIndicatorResult;
import pro.crypto.model.tick.Tick;

import java.math.BigDecimal;
import java.util.stream.IntStream;

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

    public AwesomeOscillator(IndicatorRequest creationRequest) {
        AORequest request = (AORequest) creationRequest;
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
        BigDecimal[] fastMovingAverage = calculateFastMovingAverage(medianPrices);
        BigDecimal[] slowMovingAverage = calculateSlowMovingAverage(medianPrices);
        BigDecimal[] awesomeOscillatorValues = calculateAwesomeOscillator(fastMovingAverage, slowMovingAverage);
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
        checkOriginalDataSize(originalData, fastPeriod);
        checkOriginalDataSize(originalData, slowPeriod);
        checkPeriod(fastPeriod);
        checkPeriod(slowPeriod);
    }

    private BigDecimal[] calculateFastMovingAverage(BigDecimal[] medianPrices) {
        return IndicatorResultExtractor.extractIndicatorValues(calculateSimpleMovingAverage(medianPrices, fastPeriod));
    }

    private BigDecimal[] calculateSlowMovingAverage(BigDecimal[] medianPrices) {
        return IndicatorResultExtractor.extractIndicatorValues(calculateSimpleMovingAverage(medianPrices, slowPeriod));
    }

    private SimpleIndicatorResult[] calculateSimpleMovingAverage(BigDecimal[] medianPrices, int period) {
        return MovingAverageFactory.create(buildMARequest(medianPrices, period)).getResult();
    }

    private IndicatorRequest buildMARequest(BigDecimal[] medianPrices, int period) {
        return MARequest.builder()
                .originalData(FakeTicksCreator.createWithCloseOnly(medianPrices))
                .period(period)
                .indicatorType(SIMPLE_MOVING_AVERAGE)
                .priceType(CLOSE)
                .build();
    }

    private BigDecimal[] calculateAwesomeOscillator(BigDecimal[] fastMovingAverage, BigDecimal[] slowMovingAverage) {
        return IntStream.range(0, originalData.length)
                .mapToObj(idx -> calculateAwesomeOscillator(fastMovingAverage[idx], slowMovingAverage[idx]))
                .toArray(BigDecimal[]::new);
    }

    private BigDecimal calculateAwesomeOscillator(BigDecimal fastMAValue, BigDecimal slowMAValue) {
        return nonNull(fastMAValue) && nonNull(slowMAValue)
                ? calculateAwesomeOscillatorValue(fastMAValue, slowMAValue)
                : null;
    }

    private BigDecimal calculateAwesomeOscillatorValue(BigDecimal fastMAValue, BigDecimal slowMAValue) {
        return MathHelper.scaleAndRound(fastMAValue.subtract(slowMAValue));
    }

    private void buildAwesomeOscillatorResult(BigDecimal[] awesomeOscillatorValues, Boolean[] increasedFlags) {
        IntStream.range(0, result.length)
                .forEach(idx -> result[idx] = new AOResult(
                        originalData[idx].getTickTime(),
                        awesomeOscillatorValues[idx],
                        increasedFlags[idx]));
    }

}
