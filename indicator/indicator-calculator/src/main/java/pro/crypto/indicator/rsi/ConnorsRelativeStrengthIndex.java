package pro.crypto.indicator.rsi;

import pro.crypto.helper.FakeTicksCreator;
import pro.crypto.helper.IndicatorResultExtractor;
import pro.crypto.helper.MathHelper;
import pro.crypto.model.Indicator;
import pro.crypto.model.indicator.IndicatorType;
import pro.crypto.model.tick.Tick;
import pro.crypto.request.IndicatorRequest;

import java.math.BigDecimal;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.IntStream;

import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;
import static pro.crypto.model.indicator.IndicatorType.CONNORS_RELATIVE_STRENGTH_INDEX;

public class ConnorsRelativeStrengthIndex implements Indicator<RSIResult> {

    private final Tick[] originalData;
    private final IndicatorType movingAverageType;
    private final int simpleRsiPeriod;
    private final int streakRsiPeriod;
    private final int percentRankPeriod;

    private RSIResult[] result;

    public ConnorsRelativeStrengthIndex(IndicatorRequest creationRequest) {
        CRSIRequest request = (CRSIRequest) creationRequest;
        this.originalData = request.getOriginalData();
        this.movingAverageType = request.getMovingAverageType();
        this.simpleRsiPeriod = request.getSimpleRsiPeriod();
        this.streakRsiPeriod = request.getStreakRsiPeriod();
        this.percentRankPeriod = request.getPercentRankPeriod();
        checkIncomingData();
    }

    @Override
    public IndicatorType getType() {
        return CONNORS_RELATIVE_STRENGTH_INDEX;
    }

    @Override
    public void calculate() {
        result = new RSIResult[originalData.length];
        BigDecimal[] simpleRelativeStrengthIndexValues = calculateSimpleRelativeStrengthIndex();
        BigDecimal[] streakRelativeStrengthIndexValues = calculateStreakRelativeStrengthIndex();
        BigDecimal[] percentRankValues = calculatePercentRank();
        calculateConnorsRelativeStrengthIndexValues(simpleRelativeStrengthIndexValues, streakRelativeStrengthIndexValues, percentRankValues);
    }

    @Override
    public RSIResult[] getResult() {
        if (isNull(result)) {
            calculate();
        }
        return result;
    }

    private void checkIncomingData() {
        checkOriginalData(originalData);
        checkOriginalDataSize(originalData, simpleRsiPeriod);
        checkOriginalDataSize(originalData, streakRsiPeriod);
        checkOriginalDataSize(originalData, percentRankPeriod);
        checkPeriod(simpleRsiPeriod);
        checkPeriod(streakRsiPeriod);
        checkPeriod(percentRankPeriod);
        checkMovingAverageType(movingAverageType);
    }

    private BigDecimal[] calculateSimpleRelativeStrengthIndex() {
        return IndicatorResultExtractor.extractIndicatorValues(new RelativeStrengthIndex(buildRSIRequest(originalData, simpleRsiPeriod)).getResult());
    }

    private BigDecimal[] calculateStreakRelativeStrengthIndex() {
        BigDecimal[] trendDurationValues = calculateTrendDuration();
        Tick[] fakeTicks = FakeTicksCreator.createWithCloseOnly(trendDurationValues);
        return IndicatorResultExtractor.extractIndicatorValues(new RelativeStrengthIndex(buildRSIRequest(fakeTicks, streakRsiPeriod)).getResult());
    }

    private BigDecimal[] calculateTrendDuration() {
        BigDecimal[] trendDurationValues = new BigDecimal[originalData.length];
        trendDurationValues[0] = BigDecimal.ZERO;
        IntStream.range(1, trendDurationValues.length)
                .forEach(idx -> trendDurationValues[idx] = calculateTrendDurationValue(trendDurationValues[idx - 1], idx));
        return trendDurationValues;
    }

    private BigDecimal calculateTrendDurationValue(BigDecimal previousTrendDurationValue, int currentIndex) {
        BigDecimal difference = originalData[currentIndex].getClose().subtract(originalData[currentIndex - 1].getClose());
        if (difference.compareTo(BigDecimal.ZERO) > 0) {
            return calculateUpTrendValue(previousTrendDurationValue);
        }
        if (difference.compareTo(BigDecimal.ZERO) < 0) {
            return calculateDownTrendValue(previousTrendDurationValue);
        }
        return BigDecimal.ZERO;
    }

    private BigDecimal calculateUpTrendValue(BigDecimal previousTrendDurationValue) {
        if (previousTrendDurationValue.compareTo(BigDecimal.ZERO) < 0) {
            return BigDecimal.ONE;
        }
        return previousTrendDurationValue.add(BigDecimal.ONE);
    }

    private BigDecimal calculateDownTrendValue(BigDecimal previousTrendDurationValue) {
        if (previousTrendDurationValue.compareTo(BigDecimal.ZERO) > 0) {
            return new BigDecimal(-1);
        }
        return previousTrendDurationValue.subtract(BigDecimal.ONE);
    }

    private IndicatorRequest buildRSIRequest(Tick[] data, int period) {
        return RSIRequest.builder()
                .originalData(data)
                .movingAverageType(movingAverageType)
                .period(period)
                .build();
    }

    private BigDecimal[] calculatePercentRank() {
        BigDecimal[] percentagePriceChanges = calculatePercentagePriceChanges();
        return calculatePercentRankValues(percentagePriceChanges);
    }

    private BigDecimal[] calculatePercentagePriceChanges() {
        BigDecimal[] percentagePriceChanges = new BigDecimal[originalData.length];
        percentagePriceChanges[0] = BigDecimal.ZERO;
        IntStream.range(1, percentagePriceChanges.length)
                .forEach(idx -> percentagePriceChanges[idx] = calculatePercentagePriceChange(idx));
        return percentagePriceChanges;
    }

    private BigDecimal calculatePercentagePriceChange(int currentIndex) {
        return MathHelper.divide(
                originalData[currentIndex].getClose().subtract(originalData[currentIndex - 1].getClose()).multiply(new BigDecimal(100)),
                originalData[currentIndex].getClose());
    }

    private BigDecimal[] calculatePercentRankValues(BigDecimal[] percentagePriceChanges) {
        return IntStream.range(0, originalData.length)
                .mapToObj(idx -> calculatePercentRank(percentagePriceChanges, idx))
                .toArray(BigDecimal[]::new);
    }

    private BigDecimal calculatePercentRank(BigDecimal[] percentagePriceChanges, int currentIndex) {
        return currentIndex >= percentRankPeriod - 1
                ? calculatePercentRankValue(percentagePriceChanges, currentIndex)
                : null;
    }

    private BigDecimal calculatePercentRankValue(BigDecimal[] percentagePriceChanges, int currentIndex) {
        BigDecimal percentRankValue = countPercentageInPeriodLessThanCurrent(percentagePriceChanges, currentIndex);
        return MathHelper.divide(percentRankValue.multiply(new BigDecimal(100)), new BigDecimal(percentRankPeriod));
    }

    private BigDecimal countPercentageInPeriodLessThanCurrent(BigDecimal[] percentagePriceChanges, int currentIndex) {
        AtomicInteger percentRankValue = new AtomicInteger(0);
        IntStream.range(currentIndex - percentRankPeriod + 1, currentIndex)
                .filter(idx -> percentagePriceChanges[idx].compareTo(percentagePriceChanges[currentIndex]) < 0)
                .forEach(idx -> percentRankValue.incrementAndGet());
        return new BigDecimal(percentRankValue.get());
    }

    private void calculateConnorsRelativeStrengthIndexValues(BigDecimal[] simpleRSI, BigDecimal[] streakRSI, BigDecimal[] percentRank) {
        IntStream.range(0, result.length)
                .forEach(idx -> result[idx] = new RSIResult(
                        originalData[idx].getTickTime(),
                        calculateConnorsRelativeStrengthIndexValue(simpleRSI[idx], streakRSI[idx], percentRank[idx])));
    }

    private BigDecimal calculateConnorsRelativeStrengthIndexValue(BigDecimal simpleRSIValue, BigDecimal streakRSIValue, BigDecimal percentRankValue) {
        return nonNull(simpleRSIValue) && nonNull(streakRSIValue) && nonNull(percentRankValue)
                ? MathHelper.divide(MathHelper.sum(simpleRSIValue, streakRSIValue, percentRankValue), new BigDecimal(3))
                : null;
    }

}
