package pro.crypto.indicators.ao;

import pro.crypto.helper.FakeTicksCreator;
import pro.crypto.helper.MAResultExtractor;
import pro.crypto.helper.MathHelper;
import pro.crypto.helper.MedianPriceCalculator;
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

    private final static int SLOW_PERIOD = 5;
    private final static int FAST_PERIOD = 34;
    private final Tick[] originalData;

    private AOResult[] result;

    public AwesomeOscillator(AORequest request) {
        this.originalData = request.getOriginalData();
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
        Boolean[] increasedFlags = calculateIncreasedFlags(awesomeOscillatorValues);
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
        checkOriginalDataSize(originalData, FAST_PERIOD);
    }

    private BigDecimal[] calculateSlowMovingAverage(BigDecimal[] medianPrices) {
        return MAResultExtractor.extract(calculateSimpleMovingAverage(medianPrices, SLOW_PERIOD));
    }

    private BigDecimal[] calculateFastMovingAverage(BigDecimal[] medianPrices) {
        return MAResultExtractor.extract(calculateSimpleMovingAverage(medianPrices, FAST_PERIOD));
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

    private Boolean[] calculateIncreasedFlags(BigDecimal[] awesomeOscillatorValues) {
        Boolean[] increasedFlags = new Boolean[originalData.length];
        for (int currentIndex = 1; currentIndex < increasedFlags.length; currentIndex++) {
            increasedFlags[currentIndex] = defineIncreasedFlag(awesomeOscillatorValues, currentIndex);
        }
        return increasedFlags;
    }

    private Boolean defineIncreasedFlag(BigDecimal[] awesomeOscillatorValues, int currentIndex) {
        return isNull(awesomeOscillatorValues[currentIndex]) && isNull(awesomeOscillatorValues[currentIndex - 1])
                ? null
                : defineIncreasedFlag(awesomeOscillatorValues[currentIndex], awesomeOscillatorValues[currentIndex - 1]);
    }

    private Boolean defineIncreasedFlag(BigDecimal currentValue, BigDecimal previousValue) {
        return !(isNull(previousValue) && nonNull(currentValue)) && currentValue.compareTo(previousValue) >= 0;
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
