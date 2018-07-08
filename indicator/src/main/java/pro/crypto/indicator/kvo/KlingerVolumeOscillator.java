package pro.crypto.indicator.kvo;

import pro.crypto.helper.FakeTicksCreator;
import pro.crypto.helper.IndicatorResultExtractor;
import pro.crypto.helper.MathHelper;
import pro.crypto.helper.TypicalPriceCalculator;
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
import static pro.crypto.model.IndicatorType.EXPONENTIAL_MOVING_AVERAGE;
import static pro.crypto.model.IndicatorType.KLINGER_VOLUME_OSCILLATOR;
import static pro.crypto.model.tick.PriceType.CLOSE;

public class KlingerVolumeOscillator implements Indicator<KVOResult> {

    private final Tick[] originalData;
    private final int shortPeriod;
    private final int longPeriod;
    private final int signalPeriod;

    private KVOResult[] result;

    public KlingerVolumeOscillator(IndicatorRequest creationRequest) {
        KVORequest request = (KVORequest) creationRequest;
        this.originalData = request.getOriginalData();
        this.shortPeriod = request.getShortPeriod();
        this.longPeriod = request.getLongPeriod();
        this.signalPeriod = request.getSignalPeriod();
        checkIncomingData();
    }

    @Override
    public IndicatorType getType() {
        return KLINGER_VOLUME_OSCILLATOR;
    }

    @Override
    public void calculate() {
        result = new KVOResult[originalData.length];
        BigDecimal[] trendValues = calculateTrendValues();
        BigDecimal[] klingerVolumeOscillatorValues = calculateKlingerVolumeOscillatorValues(trendValues);
        BigDecimal[] signalLineValues = calculateSignalLineValues(klingerVolumeOscillatorValues);
        buildKlingerVolumeOscillatorResult(klingerVolumeOscillatorValues, signalLineValues);
    }

    @Override
    public KVOResult[] getResult() {
        if (isNull(result)) {
            calculate();
        }
        return result;
    }

    private void checkIncomingData() {
        checkOriginalData(originalData);
        checkOriginalDataSize(originalData, shortPeriod + longPeriod + signalPeriod);
        checkPeriod(shortPeriod);
        checkPeriod(longPeriod);
        checkPeriod(signalPeriod);
    }

    private BigDecimal[] calculateTrendValues() {
        BigDecimal[] typicalPrices = TypicalPriceCalculator.calculateTypicalPrices(originalData);
        return IntStream.range(0, originalData.length)
                .mapToObj(idx -> calculateTrend(typicalPrices, idx))
                .toArray(BigDecimal[]::new);
    }

    private BigDecimal calculateTrend(BigDecimal[] typicalPrices, int currentIndex) {
        return currentIndex > 0
                ? calculateTrendValue(typicalPrices, currentIndex)
                : null;
    }

    private BigDecimal calculateTrendValue(BigDecimal[] typicalPrices, int currentIndex) {
        return typicalPrices[currentIndex].compareTo(typicalPrices[currentIndex - 1]) > 0
                ? originalData[currentIndex].getBaseVolume()
                : originalData[currentIndex].getBaseVolume().negate();
    }

    private BigDecimal[] calculateKlingerVolumeOscillatorValues(BigDecimal[] trendValues) {
        BigDecimal[] shortEmaValues = calculateExponentialMovingAverage(trendValues, shortPeriod, 1);
        BigDecimal[] longEmaValues = calculateExponentialMovingAverage(trendValues, longPeriod, 1);
        return calculateKlingerOscillatorValues(shortEmaValues, longEmaValues);
    }

    private BigDecimal[] calculateKlingerOscillatorValues(BigDecimal[] shortEmaValues, BigDecimal[] longEmaValues) {
        return IntStream.range(0, originalData.length)
                .mapToObj(idx -> calculateKlingerOscillator(shortEmaValues[idx], longEmaValues[idx]))
                .toArray(BigDecimal[]::new);
    }

    private BigDecimal calculateKlingerOscillator(BigDecimal shortEmaValue, BigDecimal longEmaValue) {
        return nonNull(shortEmaValue) && nonNull(longEmaValue)
                ? shortEmaValue.subtract(longEmaValue)
                : null;
    }

    private BigDecimal[] calculateSignalLineValues(BigDecimal[] klingerVolumeOscillatorValues) {
        return calculateExponentialMovingAverage(klingerVolumeOscillatorValues, signalPeriod, longPeriod);
    }

    private BigDecimal[] calculateExponentialMovingAverage(BigDecimal[] values, int period, int shiftCopy) {
        BigDecimal[] maValues = IndicatorResultExtractor.extract(calculateMovingAverage(values, period));
        BigDecimal[] result = new BigDecimal[originalData.length];
        System.arraycopy(maValues, 0, result, shiftCopy, maValues.length);
        return result;
    }

    private SimpleIndicatorResult[] calculateMovingAverage(BigDecimal[] values, int period) {
        return MovingAverageFactory.create(buildMARequest(values, period)).getResult();
    }

    private IndicatorRequest buildMARequest(BigDecimal[] values, int period) {
        return MARequest.builder()
                .originalData(FakeTicksCreator.createWithCloseOnly(values))
                .indicatorType(EXPONENTIAL_MOVING_AVERAGE)
                .period(period)
                .priceType(CLOSE)
                .build();
    }

    private void buildKlingerVolumeOscillatorResult(BigDecimal[] klingerVolumeOscillatorValues, BigDecimal[] signalLineValues) {
        IntStream.range(0, result.length)
                .forEach(idx -> result[idx] = new KVOResult(
                        originalData[idx].getTickTime(),
                        MathHelper.scaleAndRound(klingerVolumeOscillatorValues[idx]),
                        signalLineValues[idx]));
    }

}
