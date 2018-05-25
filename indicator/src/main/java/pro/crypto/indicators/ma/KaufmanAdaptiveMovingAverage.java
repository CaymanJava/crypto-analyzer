package pro.crypto.indicators.ma;

import pro.crypto.helper.MathHelper;
import pro.crypto.model.IndicatorType;
import pro.crypto.model.result.MAResult;
import pro.crypto.model.tick.PriceType;
import pro.crypto.model.tick.Tick;

import java.math.BigDecimal;
import java.util.Arrays;

import static java.util.Objects.nonNull;
import static pro.crypto.model.IndicatorType.KAUFMAN_ADAPTIVE_MOVING_AVERAGE;

public class KaufmanAdaptiveMovingAverage extends MovingAverage {

    private final static BigDecimal FASTEST_SMOOTHING_CONSTANT = new BigDecimal(0.6666666667); // 2 / (2 + 1)
    private final static BigDecimal SLOWEST_SMOOTHING_CONSTANT = new BigDecimal(0.064516129); // 2 / (30 + 1)
    private final Tick[] originalData;
    private final int period;

    KaufmanAdaptiveMovingAverage(Tick[] originalData, int period, PriceType priceType) {
        checkIncomingData(originalData, period, priceType);
        this.originalData = originalData;
        this.period = period;
        this.priceType = priceType;
    }

    @Override
    public IndicatorType getType() {
        return KAUFMAN_ADAPTIVE_MOVING_AVERAGE;
    }

    @Override
    public void calculate() {
        initResultArray(this.originalData.length);
        BigDecimal[] efficiencyRatios = calculateEfficiencyRatios();
        BigDecimal[] scaledSmoothingConstants = calculateScaledSmoothingConstants(efficiencyRatios);
        calculateKaufmanAdaptiveMovingAverage(scaledSmoothingConstants);
    }

    private BigDecimal[] calculateEfficiencyRatios() {
        BigDecimal[] changes = calculateChanges();
        BigDecimal[] volatilityValues = calculateVolatility();
        return calculateEfficiencyRatios(changes, volatilityValues);
    }

    private BigDecimal[] calculateChanges() {
        BigDecimal[] changes = new BigDecimal[originalData.length];
        for (int currentIndex = period; currentIndex < changes.length; currentIndex++) {
            changes[currentIndex] = calculatePriceChange(currentIndex, period);
        }
        return changes;
    }

    private BigDecimal[] calculateVolatility() {
        BigDecimal[] volatilityValues = new BigDecimal[originalData.length];
        BigDecimal[] oneDayPriceChanges = calculateOneDaysPriceChanges();
        for (int currentIndex = period; currentIndex < volatilityValues.length; currentIndex++) {
            volatilityValues[currentIndex] = calculateVolatility(oneDayPriceChanges, currentIndex);
        }
        return volatilityValues;
    }

    private BigDecimal[] calculateOneDaysPriceChanges() {
        BigDecimal[] oneDayPriceChanges = new BigDecimal[originalData.length];
        for (int currentIndex = 1; currentIndex < oneDayPriceChanges.length; currentIndex++) {
            oneDayPriceChanges[currentIndex] = calculatePriceChange(currentIndex, 1);
        }
        return oneDayPriceChanges;
    }

    // ABS(Price(i) - Price(i - n))
    private BigDecimal calculatePriceChange(int currentIndex, int period) {
        return originalData[currentIndex].getPriceByType(priceType)
                .subtract(originalData[currentIndex - period].getPriceByType(priceType))
                .abs();
    }

    // ∑(ABS(Price(i) - Price(i - 1)))
    private BigDecimal calculateVolatility(BigDecimal[] oneDayPriceChanges, int currentIndex) {
        return MathHelper.sum(Arrays.copyOfRange(oneDayPriceChanges, currentIndex - period + 1, currentIndex + 1));
    }

    private BigDecimal[] calculateEfficiencyRatios(BigDecimal[] changes, BigDecimal[] volatilityValues) {
        BigDecimal[] efficiencyRatios = new BigDecimal[originalData.length];
        for (int currentIndex = 0; currentIndex < efficiencyRatios.length; currentIndex++) {
            efficiencyRatios[currentIndex] = calculateEfficiencyRatio(changes[currentIndex], volatilityValues[currentIndex]);
        }
        return efficiencyRatios;
    }

    private BigDecimal calculateEfficiencyRatio(BigDecimal change, BigDecimal volatilityValue) {
        return nonNull(change) && nonNull(volatilityValue)
                ? MathHelper.divide(change, volatilityValue)
                : null;
    }

    private BigDecimal[] calculateScaledSmoothingConstants(BigDecimal[] efficiencyRatios) {
        BigDecimal[] scaledSmoothingConstants = new BigDecimal[originalData.length];
        for (int currentIndex = 0; currentIndex < scaledSmoothingConstants.length; currentIndex++) {
            scaledSmoothingConstants[currentIndex] = calculateScaledSmoothingConstant(efficiencyRatios[currentIndex]);
        }
        return scaledSmoothingConstants;
    }

    private BigDecimal calculateScaledSmoothingConstant(BigDecimal efficiencyRatio) {
        return nonNull(efficiencyRatio)
                ? calculateScaledSmoothingConstantValues(efficiencyRatio)
                : null;
    }

    // (ER * (fastest SC - slowest SC) + slowest SC) ^ 2
    private BigDecimal calculateScaledSmoothingConstantValues(BigDecimal efficiencyRatio) {
        return efficiencyRatio
                .multiply(FASTEST_SMOOTHING_CONSTANT.subtract(SLOWEST_SMOOTHING_CONSTANT))
                .add(SLOWEST_SMOOTHING_CONSTANT)
                .pow(2);
    }

    private void calculateKaufmanAdaptiveMovingAverage(BigDecimal[] scaledSmoothingConstants) {
        fillInInitialPositions(originalData, period);
        calculateSimpleAverage(0, period - 1, originalData);
        for (int currentIndex = period; currentIndex < result.length; currentIndex++) {
            result[currentIndex] = calculateKaufmanAdaptiveMovingAverage(scaledSmoothingConstants[currentIndex], currentIndex);
        }
    }

    private MAResult calculateKaufmanAdaptiveMovingAverage(BigDecimal scaledSmoothingConstant, int currentIndex) {
        return new MAResult(originalData[currentIndex].getTickTime(), calculateKaufmanAdaptiveMovingAverageValue(scaledSmoothingConstant, currentIndex));
    }

    // KAMA(i) = KAMA(i - 1) + SC(i) x (Price(i) - KAMA(i - 1))
    private BigDecimal calculateKaufmanAdaptiveMovingAverageValue(BigDecimal scaledSmoothingConstant, int currentIndex) {
        return MathHelper.scaleAndRound(result[currentIndex - 1].getIndicatorValue()
                .add(scaledSmoothingConstant
                        .multiply(originalData[currentIndex].getPriceByType(priceType)
                                .subtract(result[currentIndex - 1].getIndicatorValue()))));
    }

}
