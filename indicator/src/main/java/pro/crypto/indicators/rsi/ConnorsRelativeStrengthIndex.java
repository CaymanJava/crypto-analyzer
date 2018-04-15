package pro.crypto.indicators.rsi;

import pro.crypto.helper.FakeTicksCreator;
import pro.crypto.helper.MathHelper;
import pro.crypto.model.Indicator;
import pro.crypto.model.IndicatorType;
import pro.crypto.model.request.CRSIRequest;
import pro.crypto.model.request.RSIRequest;
import pro.crypto.model.result.RSIResult;
import pro.crypto.model.tick.Tick;

import java.math.BigDecimal;
import java.util.stream.Stream;

import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;
import static pro.crypto.model.IndicatorType.CONNORS_RELATIVE_STRENGTH_INDEX;

public class ConnorsRelativeStrengthIndex implements Indicator<RSIResult> {

    private final Tick[] originalData;
    private final IndicatorType movingAverageType;
    private final int simpleRsiPeriod;
    private final int streakRsiPeriod;
    private final int percentRankPeriod;

    private RSIResult[] result;

    public ConnorsRelativeStrengthIndex(CRSIRequest request) {
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
        return extractIndicatorResult(new RelativeStrengthIndex(buildRSIRequest(originalData, simpleRsiPeriod)).getResult());
    }

    private BigDecimal[] calculateStreakRelativeStrengthIndex() {
        BigDecimal[] trendDurationValues = calculateTrendDuration();
        Tick[] fakeTicks = FakeTicksCreator.createWithCloseOnly(trendDurationValues);
        return extractIndicatorResult(new RelativeStrengthIndex(buildRSIRequest(fakeTicks, streakRsiPeriod)).getResult());
    }

    private BigDecimal[] extractIndicatorResult(RSIResult[] result) {
        return Stream.of(result)
                .map(RSIResult::getIndicatorValue)
                .toArray(BigDecimal[]::new);
    }

    private BigDecimal[] calculateTrendDuration() {
        BigDecimal[] trendDurationValues = new BigDecimal[originalData.length];
        trendDurationValues[0] = BigDecimal.ZERO;
        for (int currentIndex = 1; currentIndex < trendDurationValues.length; currentIndex++) {
            trendDurationValues[currentIndex] = calculateTrendDurationValue(trendDurationValues[currentIndex - 1], currentIndex);
        }
        return trendDurationValues;
    }

    private BigDecimal calculateTrendDurationValue(BigDecimal previousTrendDurationValue, int currentIndex) {
        BigDecimal difference = originalData[currentIndex].getClose().subtract(originalData[currentIndex - 1].getClose());
        if (difference.compareTo(BigDecimal.ZERO) > 0) {
            return calculateUpTrendValue(previousTrendDurationValue);
        } else if (difference.compareTo(BigDecimal.ZERO) < 0) {
            return calculateDownTrendValue(previousTrendDurationValue);
        } else {
            return BigDecimal.ZERO;
        }
    }

    private BigDecimal calculateUpTrendValue(BigDecimal previousTrendDurationValue) {
        if (previousTrendDurationValue.compareTo(BigDecimal.ZERO) < 0) {
            return BigDecimal.ONE;
        } else {
            return previousTrendDurationValue.add(BigDecimal.ONE);
        }
    }

    private BigDecimal calculateDownTrendValue(BigDecimal previousTrendDurationValue) {
        if (previousTrendDurationValue.compareTo(BigDecimal.ZERO) > 0) {
            return new BigDecimal(-1);
        } else {
            return previousTrendDurationValue.subtract(BigDecimal.ONE);
        }
    }

    private RSIRequest buildRSIRequest(Tick[] data, int period) {
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
        for (int currentIndex = 1; currentIndex < percentagePriceChanges.length; currentIndex++) {
            percentagePriceChanges[currentIndex] = calculatePercentagePriceChange(currentIndex);
        }
        return percentagePriceChanges;
    }

    private BigDecimal calculatePercentagePriceChange(int currentIndex) {
        BigDecimal relativeChange = MathHelper.divide(
                originalData[currentIndex].getClose().subtract(originalData[currentIndex - 1].getClose()),
                originalData[currentIndex].getClose());
        return nonNull(relativeChange)
                ? MathHelper.scaleAndRound(relativeChange.multiply(new BigDecimal(100)))
                : null;
    }

    private BigDecimal[] calculatePercentRankValues(BigDecimal[] percentagePriceChanges) {
        BigDecimal[] percentRankValues = new BigDecimal[originalData.length];
        for (int currentIndex = percentRankPeriod - 1; currentIndex < percentRankValues.length; currentIndex++) {
            percentRankValues[currentIndex] = calculatePercentRank(percentagePriceChanges, currentIndex);
        }
        return percentRankValues;
    }

    private BigDecimal calculatePercentRank(BigDecimal[] percentagePriceChanges, int currentIndex) {
        BigDecimal percentRankValue = countPercentageInPeriodLessThanCurrent(percentagePriceChanges, currentIndex);
        BigDecimal relativeRank = MathHelper.divide(percentRankValue, new BigDecimal(percentRankPeriod));
        return nonNull(relativeRank)
                ? MathHelper.scaleAndRound(relativeRank.multiply(new BigDecimal(100)))
                : null;
    }

    private BigDecimal countPercentageInPeriodLessThanCurrent(BigDecimal[] percentagePriceChanges, int currentIndex) {
        int percentRankValue = 0;
        for (int i = currentIndex - percentRankPeriod + 1; i < currentIndex; i++) {
            if (percentagePriceChanges[i].compareTo(percentagePriceChanges[currentIndex]) < 0) {
                percentRankValue++;
            }
        }
        return new BigDecimal(percentRankValue);
    }

    private void calculateConnorsRelativeStrengthIndexValues(BigDecimal[] simpleRSI, BigDecimal[] streakRSI, BigDecimal[] percentRank) {
        for (int currentIndex = 0; currentIndex < result.length; currentIndex++) {
            result[currentIndex] = new RSIResult(
                    originalData[currentIndex].getTickTime(),
                    calculateConnorsRelativeStrengthIndexValue(simpleRSI[currentIndex], streakRSI[currentIndex], percentRank[currentIndex]));
        }
    }

    private BigDecimal calculateConnorsRelativeStrengthIndexValue(BigDecimal simpleRSIValue, BigDecimal streakRSIValue, BigDecimal percentRankValue) {
        return isNull(simpleRSIValue) || isNull(streakRSIValue) || isNull(percentRankValue)
                ? null
                : MathHelper.divide(MathHelper.sum(simpleRSIValue, streakRSIValue, percentRankValue), new BigDecimal(3));
    }

}
