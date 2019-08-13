package pro.crypto.indicator.ma;

import pro.crypto.helper.MathHelper;
import pro.crypto.model.indicator.IndicatorType;
import pro.crypto.model.tick.PriceType;
import pro.crypto.model.tick.Tick;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.stream.IntStream;

import static java.util.Objects.nonNull;
import static pro.crypto.model.indicator.IndicatorType.KAUFMAN_ADAPTIVE_MOVING_AVERAGE;

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
        return IntStream.range(0, originalData.length)
                .mapToObj(idx -> calculatePriceChange(idx, period))
                .toArray(BigDecimal[]::new);
    }

    private BigDecimal[] calculateVolatility() {
        BigDecimal[] oneDayPriceChanges = calculateOneDaysPriceChanges();
        return IntStream.range(0, originalData.length)
                .mapToObj(idx -> calculateVolatility(oneDayPriceChanges, idx))
                .toArray(BigDecimal[]::new);
    }

    private BigDecimal[] calculateOneDaysPriceChanges() {
        return IntStream.range(0, originalData.length)
                .mapToObj(idx -> calculatePriceChange(idx, 1))
                .toArray(BigDecimal[]::new);
    }

    private BigDecimal calculatePriceChange(int currentIndex, int period) {
        return currentIndex >= period
                ? calculatePriceChangeValue(currentIndex, period)
                : null;
    }

    // ABS(Price(i) - Price(i - n))
    private BigDecimal calculatePriceChangeValue(int currentIndex, int period) {
        return originalData[currentIndex].getPriceByType(priceType)
                .subtract(originalData[currentIndex - period].getPriceByType(priceType))
                .abs();
    }

    private BigDecimal calculateVolatility(BigDecimal[] oneDayPriceChanges, int currentIndex) {
        return currentIndex >= period
                ? calculateVolatilityValue(oneDayPriceChanges, currentIndex)
                : null;
    }

    // ∑(ABS(Price(i) - Price(i - 1)))
    private BigDecimal calculateVolatilityValue(BigDecimal[] oneDayPriceChanges, int currentIndex) {
        return MathHelper.sum(Arrays.copyOfRange(oneDayPriceChanges, currentIndex - period + 1, currentIndex + 1));
    }

    private BigDecimal[] calculateEfficiencyRatios(BigDecimal[] changes, BigDecimal[] volatilityValues) {
        return IntStream.range(0, originalData.length)
                .mapToObj(idx -> calculateEfficiencyRatio(changes[idx], volatilityValues[idx]))
                .toArray(BigDecimal[]::new);
    }

    private BigDecimal calculateEfficiencyRatio(BigDecimal change, BigDecimal volatilityValue) {
        return nonNull(change) && nonNull(volatilityValue)
                ? MathHelper.divide(change, volatilityValue)
                : null;
    }

    private BigDecimal[] calculateScaledSmoothingConstants(BigDecimal[] efficiencyRatios) {
        return IntStream.range(0, originalData.length)
                .mapToObj(idx -> calculateScaledSmoothingConstant(efficiencyRatios[idx]))
                .toArray(BigDecimal[]::new);
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
        IntStream.range(period, result.length)
                .forEach(idx -> result[idx] = calculateKaufmanAdaptiveMovingAverage(scaledSmoothingConstants[idx], idx));
    }

    // KAMA(i) = KAMA(i - 1) + SC(i) x (Price(i) - KAMA(i - 1))
    private MAResult calculateKaufmanAdaptiveMovingAverage(BigDecimal scaledSmoothingConstant, int currentIndex) {
        return new MAResult(originalData[currentIndex].getTickTime(), calculateExponentialAverage(originalData, currentIndex, scaledSmoothingConstant));
    }

}
