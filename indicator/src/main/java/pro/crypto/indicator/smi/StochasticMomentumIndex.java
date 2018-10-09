package pro.crypto.indicator.smi;

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
import static pro.crypto.model.IndicatorType.STOCHASTIC_MOMENTUM_INDEX;
import static pro.crypto.model.tick.PriceType.CLOSE;
import static pro.crypto.model.tick.PriceType.HIGH;
import static pro.crypto.model.tick.PriceType.LOW;

public class StochasticMomentumIndex implements Indicator<SMIResult> {

    private final Tick[] originalData;
    private final int period;
    private final int smoothingPeriod;
    private final IndicatorType movingAverageType;

    private SMIResult[] result;

    public StochasticMomentumIndex(IndicatorRequest creationRequest) {
        SMIRequest request = (SMIRequest) creationRequest;
        this.originalData = request.getOriginalData();
        this.period = request.getPeriod();
        this.smoothingPeriod = request.getSmoothingPeriod();
        this.movingAverageType = request.getMovingAverageType();
        checkIncomingData();
    }

    @Override
    public IndicatorType getType() {
        return STOCHASTIC_MOMENTUM_INDEX;
    }

    @Override
    public void calculate() {
        BigDecimal[] minValues = MinMaxFinder.findMinValues(PriceVolumeExtractor.extract(originalData, LOW), period);
        BigDecimal[] maxValues = MinMaxFinder.findMaxValues(PriceVolumeExtractor.extract(originalData, HIGH), period);
        BigDecimal[] doubleMAPriceDiffs = calculateDoubleMAPriceDifference(maxValues, minValues);
        BigDecimal[] doubleMAMaxMinDiffs = calculateDoubleMAMaxMinDifference(maxValues, minValues);
        BigDecimal[] stochasticMomentumValues = calculateStochasticMomentum(doubleMAPriceDiffs, doubleMAMaxMinDiffs);
        BigDecimal[] signalLineValues = calculateSignalLineValues(stochasticMomentumValues);
        buildStochasticMomentumResult(stochasticMomentumValues, signalLineValues);
    }

    @Override
    public SMIResult[] getResult() {
        if (isNull(result)) {
            calculate();
        }
        return result;
    }

    private void checkIncomingData() {
        checkOriginalData(originalData);
        checkOriginalDataSize(originalData, period + smoothingPeriod * 3 - 2);
        checkPeriod(period);
        checkPeriod(smoothingPeriod);
        checkMovingAverageType(movingAverageType);
    }

    private BigDecimal[] calculateDoubleMAPriceDifference(BigDecimal[] maxValues, BigDecimal[] minValues) {
        BigDecimal[] priceDifferences = calculatePriceDifferenceValues(maxValues, minValues);
        return calculateDoubleMovingAverage(priceDifferences);
    }

    private BigDecimal[] calculatePriceDifferenceValues(BigDecimal[] maxValues, BigDecimal[] minValues) {
        return IntStream.range(0, originalData.length)
                .mapToObj(idx -> calculatePriceDifference(maxValues[idx], minValues[idx], idx))
                .toArray(BigDecimal[]::new);
    }

    private BigDecimal calculatePriceDifference(BigDecimal maxValue, BigDecimal minValue, int currentIndex) {
        return nonNull(maxValue) && nonNull(minValue)
                ? calculatePriceDifferenceValue(maxValue, minValue, currentIndex)
                : null;
    }

    private BigDecimal calculatePriceDifferenceValue(BigDecimal maxValue, BigDecimal minValue, int currentIndex) {
        BigDecimal averagePrice = MathHelper.average(maxValue, minValue);
        return originalData[currentIndex].getClose().subtract(averagePrice);
    }

    private BigDecimal[] calculateDoubleMAMaxMinDifference(BigDecimal[] maxValues, BigDecimal[] minValues) {
        BigDecimal[] maxMinDiffs = calculateMaxMinDifferences(maxValues, minValues);
        return calculateDoubleMovingAverage(maxMinDiffs);
    }

    private BigDecimal[] calculateMaxMinDifferences(BigDecimal[] maxValues, BigDecimal[] minValues) {
        return IntStream.range(0, originalData.length)
                .mapToObj(idx -> calculateMaxMinDifference(maxValues[idx], minValues[idx]))
                .toArray(BigDecimal[]::new);
    }

    private BigDecimal calculateMaxMinDifference(BigDecimal maxValue, BigDecimal minValue) {
        return nonNull(maxValue) && nonNull(minValue)
                ? maxValue.subtract(minValue)
                : null;
    }

    private BigDecimal[] calculateDoubleMovingAverage(BigDecimal[] values) {
        BigDecimal[] movingAverageValues = calculateMovingAverageValues(values);
        BigDecimal[] doubleMovingAverageValues = calculateMovingAverageValues(movingAverageValues);
        BigDecimal[] result = new BigDecimal[originalData.length];
        System.arraycopy(doubleMovingAverageValues, 0, result, period + smoothingPeriod - 2, doubleMovingAverageValues.length);
        return result;
    }

    private BigDecimal[] calculateStochasticMomentum(BigDecimal[] doubleMAPriceDiffs, BigDecimal[] doubleMAMaxMinDiffs) {
        BigDecimal[] stochasticMomentumValues = calculateStochasticMomentumValues(doubleMAPriceDiffs, doubleMAMaxMinDiffs);
        BigDecimal[] smoothedSMIValues = calculateMovingAverageValues(stochasticMomentumValues);
        BigDecimal[] result = new BigDecimal[originalData.length];
        System.arraycopy(smoothedSMIValues, 0, result, period + (smoothingPeriod * 2) - 3, smoothedSMIValues.length);
        return result;
    }

    private BigDecimal[] calculateStochasticMomentumValues(BigDecimal[] doubleMAPriceDiffs, BigDecimal[] doubleMAMaxMinDiffs) {
        return IntStream.range(0, originalData.length)
                .mapToObj(idx -> calculateStochasticMomentum(doubleMAPriceDiffs[idx], doubleMAMaxMinDiffs[idx]))
                .toArray(BigDecimal[]::new);
    }

    private BigDecimal calculateStochasticMomentum(BigDecimal doubleMAPriceDiff, BigDecimal doubleMAMaxMinDiff) {
        return nonNull(doubleMAPriceDiff) && nonNull(doubleMAMaxMinDiff)
                ? calculateStochasticMomentumValue(doubleMAPriceDiff, doubleMAMaxMinDiff)
                : null;
    }

    // 100 * (PriceDiff / (MaxMinDiff / 2))
    private BigDecimal calculateStochasticMomentumValue(BigDecimal doubleMAPriceDiff, BigDecimal doubleMAMaxMinDiff) {
        BigDecimal halfDiffValue = MathHelper.divide(doubleMAMaxMinDiff, new BigDecimal(2));
        return nonNull(halfDiffValue) && halfDiffValue.compareTo(BigDecimal.ZERO) != 0
                ? MathHelper.divide(new BigDecimal(100).multiply(doubleMAPriceDiff), halfDiffValue)
                : BigDecimal.ZERO;
    }

    private BigDecimal[] calculateSignalLineValues(BigDecimal[] stochasticMomentumValues) {
        BigDecimal[] signalLineValues = calculateMovingAverageValues(stochasticMomentumValues);
        BigDecimal[] result = new BigDecimal[originalData.length];
        System.arraycopy(signalLineValues, 0, result, period + (smoothingPeriod * 3) - 4, signalLineValues.length);
        return result;
    }

    private BigDecimal[] calculateMovingAverageValues(BigDecimal[] values) {
        return IndicatorResultExtractor.extractIndicatorValue(calculateMovingAverage(values));
    }

    private SimpleIndicatorResult[] calculateMovingAverage(BigDecimal[] priceDifferences) {
        return MovingAverageFactory.create(buildMARequest(priceDifferences)).getResult();
    }

    private IndicatorRequest buildMARequest(BigDecimal[] priceDifferences) {
        return MARequest.builder()
                .originalData(FakeTicksCreator.createWithCloseOnly(priceDifferences))
                .priceType(CLOSE)
                .indicatorType(movingAverageType)
                .period(smoothingPeriod)
                .build();
    }

    private void buildStochasticMomentumResult(BigDecimal[] stochasticMomentumValues, BigDecimal[] signalLineValues) {
        result = IntStream.range(0, originalData.length)
                .mapToObj(idx -> new SMIResult(originalData[idx].getTickTime(), stochasticMomentumValues[idx], signalLineValues[idx]))
                .toArray(SMIResult[]::new);
    }

}
