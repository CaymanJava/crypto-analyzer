package pro.crypto.indicator.tmf;

import pro.crypto.helper.FakeTicksCreator;
import pro.crypto.helper.IndicatorResultExtractor;
import pro.crypto.helper.MathHelper;
import pro.crypto.helper.PriceVolumeExtractor;
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
import static pro.crypto.model.IndicatorType.TWIGGS_MONEY_FLOW;
import static pro.crypto.model.tick.PriceType.CLOSE;

public class TwiggsMoneyFlow implements Indicator<TMFResult> {

    private final Tick[] originalData;
    private final int period;

    private TMFResult[] result;

    public TwiggsMoneyFlow(IndicatorRequest creationRequest) {
        TMFRequest request = (TMFRequest) creationRequest;
        this.originalData = request.getOriginalData();
        this.period = request.getPeriod();
        checkIncomingData();
    }

    @Override
    public IndicatorType getType() {
        return TWIGGS_MONEY_FLOW;
    }

    @Override
    public void calculate() {
        BigDecimal[] twiggsMoneyFlowValues = calculateTwiggsMoneyFlowValues();
        BigDecimal[] signalLineValues = calculateSignalLineValues(twiggsMoneyFlowValues);
        buildTwiggsMoneyFlowResult(twiggsMoneyFlowValues, signalLineValues);
    }

    @Override
    public TMFResult[] getResult() {
        if (isNull(result)) {
            calculate();
        }
        return result;
    }

    private void checkIncomingData() {
        checkOriginalData(originalData);
        checkOriginalDataSize(originalData, period);
        checkPeriod(period);
    }

    private BigDecimal[] calculateTwiggsMoneyFlowValues() {
        BigDecimal[] smoothedVolumeRangeValues = calculateSmoothedVolumeRangeValues();
        BigDecimal[] smoothedVolumeValues = calculateSmoothedVolumeValues();
        return calculateTwiggsMoneyFlowValues(smoothedVolumeRangeValues, smoothedVolumeValues);
    }

    private BigDecimal[] calculateSmoothedVolumeValues() {
        BigDecimal[] volumes = PriceVolumeExtractor.extractBaseVolume(originalData);
        return calculateExponentialMovingAverageValues(volumes);
    }

    private BigDecimal[] calculateSmoothedVolumeRangeValues() {
        BigDecimal[] volumeRangeValues = calculateVolumeRangeValues();
        BigDecimal[] smoothedVolumeRangeValues = calculateExponentialMovingAverageValues(volumeRangeValues);
        BigDecimal[] result = new BigDecimal[originalData.length];
        System.arraycopy(smoothedVolumeRangeValues, 0, result, 1, smoothedVolumeRangeValues.length);
        return result;
    }

    private BigDecimal[] calculateVolumeRangeValues() {
        BigDecimal[] lowCloseMinValues = calculateLowCloseMinValues();
        BigDecimal[] highCloseMaxValues = calculateHighCloseMaxValues();
        return calculateVolumeRangeValues(lowCloseMinValues, highCloseMaxValues);
    }

    private BigDecimal[] calculateLowCloseMinValues() {
        BigDecimal[] lowCloseMinValues = new BigDecimal[originalData.length];
        IntStream.range(1, originalData.length)
                .forEach(idx -> lowCloseMinValues[idx] = MathHelper.min(originalData[idx].getLow(), originalData[idx - 1].getClose()));
        return lowCloseMinValues;
    }

    private BigDecimal[] calculateHighCloseMaxValues() {
        BigDecimal[] highCloseMaxValues = new BigDecimal[originalData.length];
        IntStream.range(1, originalData.length)
                .forEach(idx -> highCloseMaxValues[idx] = MathHelper.max(originalData[idx].getHigh(), originalData[idx - 1].getClose()));
        return highCloseMaxValues;
    }

    private BigDecimal[] calculateVolumeRangeValues(BigDecimal[] lowCloseMinValues, BigDecimal[] highCloseMaxValues) {
        return IntStream.range(0, originalData.length)
                .mapToObj(idx -> calculateVolumeRange(lowCloseMinValues[idx], highCloseMaxValues[idx], idx))
                .toArray(BigDecimal[]::new);
    }

    private BigDecimal calculateVolumeRange(BigDecimal lowCloseMinValue, BigDecimal highCloseMaxValue, int currentIndex) {
        return nonNull(lowCloseMinValue) && nonNull(highCloseMaxValue)
                ? calculateVolumeRangeValue(lowCloseMinValue, highCloseMaxValue, currentIndex)
                : null;
    }

    // (2 * Close - LL - HH) / (HH - LL) * Volume
    private BigDecimal calculateVolumeRangeValue(BigDecimal lowCloseMinValue, BigDecimal highCloseMaxValue, int currentIndex) {
        if (highCloseMaxValue.subtract(lowCloseMinValue).compareTo(BigDecimal.ZERO) == 0) {
            return BigDecimal.ZERO;
        }
        BigDecimal range = calculateRange(lowCloseMinValue, highCloseMaxValue, currentIndex);
        return range.multiply(originalData[currentIndex].getBaseVolume());
    }

    private BigDecimal calculateRange(BigDecimal lowCloseMinValue, BigDecimal highCloseMaxValue, int currentIndex) {
        return MathHelper.divide(new BigDecimal(2).multiply(originalData[currentIndex].getClose()).subtract(lowCloseMinValue).subtract(highCloseMaxValue),
                highCloseMaxValue.subtract(lowCloseMinValue));
    }

    private BigDecimal[] calculateTwiggsMoneyFlowValues(BigDecimal[] smoothedVolumeRangeValues, BigDecimal[] smoothedVolumeValues) {
        return IntStream.range(0, originalData.length)
                .mapToObj(idx -> calculateTwiggsMoneyFlow(smoothedVolumeRangeValues[idx], smoothedVolumeValues[idx]))
                .toArray(BigDecimal[]::new);
    }

    private BigDecimal calculateTwiggsMoneyFlow(BigDecimal smoothedVolumeRangeValue, BigDecimal smoothedVolumeValue) {
        return nonNull(smoothedVolumeRangeValue) && nonNull(smoothedVolumeValue)
                ? MathHelper.divide(new BigDecimal(100).multiply(smoothedVolumeRangeValue), smoothedVolumeValue)
                : null;
    }

    private BigDecimal[] calculateSignalLineValues(BigDecimal[] twiggsMoneyFlowValues) {
        BigDecimal[] signalLineValues = calculateExponentialMovingAverageValues(twiggsMoneyFlowValues);
        BigDecimal[] result = new BigDecimal[originalData.length];
        System.arraycopy(signalLineValues, 0, result, period, signalLineValues.length);
        return result;
    }

    private BigDecimal[] calculateExponentialMovingAverageValues(BigDecimal[] values) {
        return IndicatorResultExtractor.extractIndicatorValue(calculateMovingAverage(values));
    }

    private SimpleIndicatorResult[] calculateMovingAverage(BigDecimal[] values) {
        return MovingAverageFactory.create(buildMARequest(values)).getResult();
    }

    private IndicatorRequest buildMARequest(BigDecimal[] values) {
        return MARequest.builder()
                .originalData(FakeTicksCreator.createWithCloseOnly(values))
                .period(period)
                .priceType(CLOSE)
                .indicatorType(EXPONENTIAL_MOVING_AVERAGE)
                .build();
    }

    private void buildTwiggsMoneyFlowResult(BigDecimal[] twiggsMoneyFlowValues, BigDecimal[] signalLineValues) {
        result = IntStream.range(0, originalData.length)
                .mapToObj(idx -> new TMFResult(originalData[idx].getTickTime(), twiggsMoneyFlowValues[idx], signalLineValues[idx]))
                .toArray(TMFResult[]::new);
    }

}
