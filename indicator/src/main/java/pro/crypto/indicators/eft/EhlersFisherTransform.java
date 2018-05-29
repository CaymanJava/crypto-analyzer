package pro.crypto.indicators.eft;

import pro.crypto.helper.*;
import pro.crypto.model.Indicator;
import pro.crypto.model.IndicatorType;
import pro.crypto.model.request.EFTRequest;
import pro.crypto.model.result.EFTResult;
import pro.crypto.model.tick.Tick;

import java.math.BigDecimal;

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

    public EhlersFisherTransform(EFTRequest request) {
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
        BigDecimal[] maxValues = MinMaxCalculator.calculateMaxValues(PriceExtractor.extractValuesByType(originalData, HIGH), period);
        BigDecimal[] minValues = MinMaxCalculator.calculateMinValues(PriceExtractor.extractValuesByType(originalData, LOW), period);
        calculateValuesForTransform(medianPrices, maxValues, minValues);
    }

    private void calculateValuesForTransform(BigDecimal[] medianPrices, BigDecimal[] maxValues, BigDecimal[] minValues) {
        valuesForTransform = new BigDecimal[originalData.length];
        for (int currentIndex = 1; currentIndex < valuesForTransform.length; currentIndex++) {
            valuesForTransform[currentIndex] = calculateValueForTransform(medianPrices[currentIndex], maxValues[currentIndex], minValues[currentIndex], currentIndex);
        }
    }

    private BigDecimal calculateValueForTransform(BigDecimal medianPrice, BigDecimal maxValue, BigDecimal minValue, int currentIndex) {
        return nonNull(medianPrice) && nonNull(maxValue) && nonNull(minValue)
                ? calculateValueForTransform(medianPrice, maxValue, minValue, valuesForTransform[currentIndex - 1])
                : null;
    }

    // Value(i) = 0.33 * 2 * ((MP - Min) / (Max - Min) - 0.5) + 0.67 * Value[i - 1];
    // If Value(i) > 0.99 then Value(i) = 0.999;
    // If Value(i) < -0.99 then Value(i) = -0.999;
    private BigDecimal calculateValueForTransform(BigDecimal medianPrice, BigDecimal maxValue, BigDecimal minValue, BigDecimal previousValue) {
        if (isNull(previousValue)) {
            return BigDecimal.ZERO;
        }
        BigDecimal value = new BigDecimal(0.33)
                .multiply(new BigDecimal(2))
                .multiply(calculateRatio(medianPrice, maxValue, minValue)
                        .subtract(new BigDecimal(0.5)))
                .add(new BigDecimal(0.67)
                        .multiply(previousValue));
        return roundIfNecessary(value);
    }

    private BigDecimal calculateRatio(BigDecimal medianPrice, BigDecimal maxValue, BigDecimal minValue) {
        return MathHelper.divide(medianPrice.subtract(minValue), maxValue.subtract(minValue));
    }

    private BigDecimal roundIfNecessary(BigDecimal value) {
        return value.compareTo(BigDecimal.ZERO) > 0
                ? MathHelper.min(value, new BigDecimal(0.9999))
                : MathHelper.max(value, new BigDecimal(-0.9999));
    }

    private void calculateEhlersFisherTransformResult() {
        BigDecimal[] ethValues = calculateEhlersFisherTransformValues();
        fillInFirstValue();
        for (int currentIndex = 1; currentIndex < result.length; currentIndex++) {
            result[currentIndex] = new EFTResult(originalData[currentIndex].getTickTime(), ethValues[currentIndex], ethValues[currentIndex - 1]);
        }
    }

    private BigDecimal[] calculateEhlersFisherTransformValues() {
        BigDecimal[] ehlersFisherTransformValues = new BigDecimal[originalData.length];
        for (int currentIndex = 1; currentIndex < ehlersFisherTransformValues.length; currentIndex++) {
            ehlersFisherTransformValues[currentIndex] = calculateEhlersFisherTransform(valuesForTransform[currentIndex], ehlersFisherTransformValues[currentIndex - 1]);
        }
        return ehlersFisherTransformValues;
    }

    private BigDecimal calculateEhlersFisherTransform(BigDecimal valueForTransform, BigDecimal previousEFT) {
        return nonNull(valueForTransform) && valueForTransform.compareTo(BigDecimal.ZERO) != 0
                ? calculateEhlersFisherTransformValue(valueForTransform, previousEFT)
                : null;
    }

    // Fish(i) = 0.5 * Ln((1 + Value(i)) / (1 - Value(i))) + 0.5 * Fish[i - 1]
    private BigDecimal calculateEhlersFisherTransformValue(BigDecimal valueForTransform, BigDecimal previousEFT) {
        return MathHelper.scaleAndRound(new BigDecimal(0.5)
                .multiply(MathHelper.ln(calculateRatio(valueForTransform)))
                .add(new BigDecimal(0.5).multiply(isNull(previousEFT) ? BigDecimal.ZERO : previousEFT)));
    }

    private BigDecimal calculateRatio(BigDecimal valueForTransform) {
        return MathHelper.divide(BigDecimal.ONE.add(valueForTransform), BigDecimal.ONE.subtract(valueForTransform));
    }

    private void fillInFirstValue() {
        result[0] = new EFTResult(originalData[0].getTickTime(), null, null);
    }

}
