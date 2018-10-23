package pro.crypto.indicator.eft;

import pro.crypto.helper.MathHelper;
import pro.crypto.helper.MedianPriceCalculator;
import pro.crypto.helper.MinMaxFinder;
import pro.crypto.helper.PriceVolumeExtractor;
import pro.crypto.model.Indicator;
import pro.crypto.model.IndicatorRequest;
import pro.crypto.model.IndicatorType;
import pro.crypto.model.tick.Tick;

import java.math.BigDecimal;
import java.util.stream.IntStream;

import static java.math.BigDecimal.*;
import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;
import static pro.crypto.model.IndicatorType.EHLERS_FISHER_TRANSFORM;
import static pro.crypto.model.tick.PriceType.HIGH;
import static pro.crypto.model.tick.PriceType.LOW;

public class EhlersFisherTransform implements Indicator<EFTResult> {

    private final Tick[] originalData;
    private final int period;

    private BigDecimal[] valuesForTransform;
    private EFTResult[] result;

    public EhlersFisherTransform(IndicatorRequest creationRequest) {
        EFTRequest request = (EFTRequest) creationRequest;
        this.originalData = request.getOriginalData();
        this.period = request.getPeriod();
        checkIncomingData();
    }

    @Override
    public IndicatorType getType() {
        return EHLERS_FISHER_TRANSFORM;
    }

    @Override
    public void calculate() {
        result = new EFTResult[originalData.length];
        calculateValuesForTransform();
        calculateEhlersFisherTransformResult();
    }

    @Override
    public EFTResult[] getResult() {
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

    private void calculateValuesForTransform() {
        BigDecimal[] medianPrices = MedianPriceCalculator.calculate(originalData);
        BigDecimal[] maxValues = MinMaxFinder.findMaxValues(PriceVolumeExtractor.extractPrices(originalData, HIGH), period);
        BigDecimal[] minValues = MinMaxFinder.findMinValues(PriceVolumeExtractor.extractPrices(originalData, LOW), period);
        calculateValuesForTransform(medianPrices, maxValues, minValues);
    }

    private void calculateValuesForTransform(BigDecimal[] medianPrices, BigDecimal[] maxValues, BigDecimal[] minValues) {
        valuesForTransform = new BigDecimal[originalData.length];
        IntStream.range(0, originalData.length)
                .forEach(idx -> valuesForTransform[idx] = calculateValueForTransform(medianPrices[idx], maxValues[idx], minValues[idx], idx));
    }

    private BigDecimal calculateValueForTransform(BigDecimal medianPrice, BigDecimal maxValue, BigDecimal minValue, int currentIndex) {
        return isPossibleToCalculate(medianPrice, maxValue, minValue, currentIndex)
                ? calculateValueForTransform(medianPrice, maxValue, minValue, valuesForTransform[currentIndex - 1])
                : null;
    }

    private boolean isPossibleToCalculate(BigDecimal medianPrice, BigDecimal maxValue, BigDecimal minValue, int currentIndex) {
        return currentIndex != 0 && nonNull(medianPrice) && nonNull(maxValue) && nonNull(minValue);
    }

    // Value(i) = 0.33 * 2 * ((MP - Min) / (Max - Min) - 0.5) + 0.67 * Value[i - 1];
    private BigDecimal calculateValueForTransform(BigDecimal medianPrice, BigDecimal maxValue, BigDecimal minValue, BigDecimal previousValue) {
        if (isNull(previousValue)) {
            return ZERO;
        }
        return roundIfNecessary(calculateValue(medianPrice, maxValue, minValue, previousValue));
    }

    private BigDecimal calculateValue(BigDecimal medianPrice, BigDecimal maxValue, BigDecimal minValue, BigDecimal previousValue) {
        return new BigDecimal(0.33)
                .multiply(new BigDecimal(2))
                .multiply(calculateRatio(medianPrice, maxValue, minValue)
                        .subtract(new BigDecimal(0.5)))
                .add(new BigDecimal(0.67)
                        .multiply(previousValue));
    }

    private BigDecimal calculateRatio(BigDecimal medianPrice, BigDecimal maxValue, BigDecimal minValue) {
        return MathHelper.divide(medianPrice.subtract(minValue), maxValue.subtract(minValue));
    }

    // If Value(i) > 0.99 then Value(i) = 0.999;
    // If Value(i) < -0.99 then Value(i) = -0.999;
    private BigDecimal roundIfNecessary(BigDecimal value) {
        return value.compareTo(ZERO) > 0
                ? MathHelper.min(value, new BigDecimal(0.9999))
                : MathHelper.max(value, new BigDecimal(-0.9999));
    }

    private void calculateEhlersFisherTransformResult() {
        BigDecimal[] ethValues = calculateEhlersFisherTransformValues();
        IntStream.range(0, result.length)
                .forEach(idx -> result[idx] = buildEhlersFisherTransformResult(ethValues, idx));
    }

    private EFTResult buildEhlersFisherTransformResult(BigDecimal[] ethValues, int currentIndex) {
        return currentIndex != 0
                ? new EFTResult(originalData[currentIndex].getTickTime(), ethValues[currentIndex], ethValues[currentIndex - 1])
                : new EFTResult(originalData[0].getTickTime(), null, null);
    }

    private BigDecimal[] calculateEhlersFisherTransformValues() {
        BigDecimal[] ehlersFisherTransformValues = new BigDecimal[originalData.length];
        IntStream.range(1, ehlersFisherTransformValues.length)
                .forEach(idx ->
                        ehlersFisherTransformValues[idx] = calculateEhlersFisherTransform(valuesForTransform[idx], ehlersFisherTransformValues[idx - 1]));
        return ehlersFisherTransformValues;
    }

    private BigDecimal calculateEhlersFisherTransform(BigDecimal valueForTransform, BigDecimal previousEFT) {
        return nonNull(valueForTransform) && valueForTransform.compareTo(ZERO) != 0
                ? calculateEhlersFisherTransformValue(valueForTransform, previousEFT)
                : null;
    }

    // Fish(i) = 0.5 * Ln((1 + Value(i)) / (1 - Value(i))) + 0.5 * Fish[i - 1]
    private BigDecimal calculateEhlersFisherTransformValue(BigDecimal valueForTransform, BigDecimal previousEFT) {
        return MathHelper.scaleAndRound(new BigDecimal(0.5)
                .multiply(MathHelper.ln(calculateRatio(valueForTransform)))
                .add(new BigDecimal(0.5).multiply(isNull(previousEFT) ? ZERO : previousEFT)));
    }

    private BigDecimal calculateRatio(BigDecimal valueForTransform) {
        return MathHelper.divide(ONE.add(valueForTransform), ONE.subtract(valueForTransform));
    }

}
