package pro.crypto.indicator.vo;

import pro.crypto.exception.WrongIncomingParametersException;
import pro.crypto.helper.FakeTicksCreator;
import pro.crypto.helper.IndicatorResultExtractor;
import pro.crypto.helper.MathHelper;
import pro.crypto.indicator.ma.MARequest;
import pro.crypto.indicator.ma.MovingAverageFactory;
import pro.crypto.model.Indicator;
import pro.crypto.model.IndicatorRequest;
import pro.crypto.model.IndicatorType;
import pro.crypto.model.SimpleIndicatorResult;
import pro.crypto.model.tick.Tick;

import java.math.BigDecimal;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import static java.lang.String.format;
import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;
import static pro.crypto.model.IndicatorType.EXPONENTIAL_MOVING_AVERAGE;
import static pro.crypto.model.IndicatorType.VOLUME_OSCILLATOR;
import static pro.crypto.model.tick.PriceType.CLOSE;

public class VolumeOscillator implements Indicator<VOResult> {

    private final Tick[] originalData;
    private final int shortPeriod;
    private final int longPeriod;

    private VOResult[] result;

    public VolumeOscillator(IndicatorRequest creationRequest) {
        VORequest request = (VORequest) creationRequest;
        this.originalData = request.getOriginalData();
        this.shortPeriod = request.getShortPeriod();
        this.longPeriod = request.getLongPeriod();
        checkIncomingData();
    }

    @Override
    public IndicatorType getType() {
        return VOLUME_OSCILLATOR;
    }

    @Override
    public void calculate() {
        BigDecimal[] volumes = extractVolumes();
        BigDecimal[] shortMAValues = calculateExponentialMovingAverageValues(volumes, shortPeriod);
        BigDecimal[] longMAValues = calculateExponentialMovingAverageValues(volumes, longPeriod);
        buildVolumeOscillatorResult(shortMAValues, longMAValues);
    }

    @Override
    public VOResult[] getResult() {
        if (isNull(result)) {
            calculate();
        }
        return result;
    }

    private void checkIncomingData() {
        checkOriginalData(originalData);
        checkOriginalDataSize(originalData, longPeriod);
        checkPeriods();
    }

    private void checkPeriods() {
        checkPeriod(shortPeriod);
        checkPeriod(longPeriod);
        checkPeriodsLength();
    }

    private void checkPeriodsLength() {
        if (shortPeriod > longPeriod) {
            throw new WrongIncomingParametersException(format("Incorrect period values " +
                            "{indicator: {%s}, shortPeriod: {%d}, longPeriod: {%d}}",
                    getType().toString(), shortPeriod, longPeriod));
        }
    }

    private BigDecimal[] extractVolumes() {
        return Stream.of(originalData)
                .map(Tick::getBaseVolume)
                .toArray(BigDecimal[]::new);
    }

    private BigDecimal[] calculateExponentialMovingAverageValues(BigDecimal[] volumes, int period) {
        return IndicatorResultExtractor.extractIndicatorValue(calculateExponentialMovingAverage(volumes, period));
    }

    private SimpleIndicatorResult[] calculateExponentialMovingAverage(BigDecimal[] volumes, int period) {
        return MovingAverageFactory.create(buildEMARequest(volumes, period)).getResult();
    }

    private IndicatorRequest buildEMARequest(BigDecimal[] volumes, int period) {
        return MARequest.builder()
                .originalData(FakeTicksCreator.createWithCloseOnly(volumes))
                .period(period)
                .priceType(CLOSE)
                .indicatorType(EXPONENTIAL_MOVING_AVERAGE)
                .build();
    }

    private void buildVolumeOscillatorResult(BigDecimal[] shortMAValues, BigDecimal[] longMAValues) {
        result = IntStream.range(0, originalData.length)
                .mapToObj(idx -> new VOResult(
                        originalData[idx].getTickTime(),
                        calculateVolumeOscillator(shortMAValues[idx], longMAValues[idx])
                ))
                .toArray(VOResult[]::new);
    }

    private BigDecimal calculateVolumeOscillator(BigDecimal shortMAValue, BigDecimal longMAValue) {
        return nonNull(shortMAValue) && nonNull(longMAValue)
                ? calculateVolumeOscillatorValue(shortMAValue, longMAValue)
                : null;
    }

    // 100 * (ShortMA - LongMA) / LongMA
    private BigDecimal calculateVolumeOscillatorValue(BigDecimal shortMAValue, BigDecimal longMAValue) {
        return MathHelper.divide(new BigDecimal(100).multiply(shortMAValue.subtract(longMAValue)), longMAValue);
    }

}
