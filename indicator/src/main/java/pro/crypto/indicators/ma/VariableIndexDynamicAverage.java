package pro.crypto.indicators.ma;

import pro.crypto.helper.IndicatorResultExtractor;
import pro.crypto.helper.MathHelper;
import pro.crypto.indicators.cmo.ChandeMomentumOscillator;
import pro.crypto.model.IndicatorType;
import pro.crypto.model.request.CMORequest;
import pro.crypto.model.result.CMOResult;
import pro.crypto.model.result.MAResult;
import pro.crypto.model.tick.PriceType;
import pro.crypto.model.tick.Tick;

import java.math.BigDecimal;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import static java.util.Objects.isNull;
import static pro.crypto.model.IndicatorType.VARIABLE_INDEX_DYNAMIC_AVERAGE;

public class VariableIndexDynamicAverage extends MovingAverage {

    private final Tick[] originalData;
    private final int period;

    public VariableIndexDynamicAverage(Tick[] originalData, int period, PriceType priceType) {
        checkIncomingData(originalData, period, priceType);
        this.originalData = originalData;
        this.period = period;
        this.priceType = priceType;
    }

    @Override
    public IndicatorType getType() {
        return VARIABLE_INDEX_DYNAMIC_AVERAGE;
    }

    @Override
    public void calculate() {
        initResultArray(this.originalData.length);
        BigDecimal[] cmoAbsoluteValues = calculateAbsoluteChandeMomentumOscillator();
        BigDecimal[] alphaCoefficients = calculateAlphaCoefficients(cmoAbsoluteValues);
        calculateVariableIndexDynamicAverageResult(alphaCoefficients);
    }

    private BigDecimal[] calculateAbsoluteChandeMomentumOscillator() {
        return Stream.of(IndicatorResultExtractor.extract(calculateChandeMomentumOscillator()))
                .map(result -> MathHelper.divide(result, new BigDecimal(100)))
                .toArray(BigDecimal[]::new);
    }

    private CMOResult[] calculateChandeMomentumOscillator() {
        return new ChandeMomentumOscillator(buildCMORequest()).getResult();
    }

    private CMORequest buildCMORequest() {
        return CMORequest.builder()
                .originalData(originalData)
                .period(period)
                .build();
    }

    private BigDecimal[] calculateAlphaCoefficients(BigDecimal[] cmoAbsoluteValues) {
        BigDecimal simpleAlphaCoefficient = calculateSimpleAlphaCoefficient();
        return Stream.of(cmoAbsoluteValues)
                .map(cmo -> calculateAlphaCoefficient(cmo, simpleAlphaCoefficient))
                .toArray(BigDecimal[]::new);
    }

    private BigDecimal calculateAlphaCoefficient(BigDecimal cmoAbsoluteValue, BigDecimal simpleAlphaCoefficient) {
        if (isNull(cmoAbsoluteValue)) {
            return null;
        }
        return cmoAbsoluteValue.abs().multiply(simpleAlphaCoefficient);
    }

    private BigDecimal calculateSimpleAlphaCoefficient() {
        return MathHelper.divide(new BigDecimal(2), new BigDecimal(period).add(new BigDecimal(1)));
    }

    private void calculateVariableIndexDynamicAverageResult(BigDecimal[] alphaCoefficients) {
        fillInInitialPositions(originalData, period);
        calculateSimpleAverage(0, period - 1, originalData);
        IntStream.range(period, result.length)
                .forEach(idx -> result[idx] = calculateVariableIndexDynamicAverage(alphaCoefficients[idx], idx));
    }

    private MAResult calculateVariableIndexDynamicAverage(BigDecimal alphaCoefficient, int currentIndex) {
        return new MAResult(originalData[currentIndex].getTickTime(), calculateExponentialAverage(originalData, currentIndex, alphaCoefficient));
    }

}
