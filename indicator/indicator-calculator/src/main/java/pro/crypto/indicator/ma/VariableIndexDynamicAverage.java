package pro.crypto.indicator.ma;

import pro.crypto.helper.IndicatorResultExtractor;
import pro.crypto.helper.MathHelper;
import pro.crypto.indicator.cmo.CMORequest;
import pro.crypto.indicator.cmo.ChandeMomentumOscillator;
import pro.crypto.model.indicator.IndicatorType;
import pro.crypto.model.tick.PriceType;
import pro.crypto.model.tick.Tick;
import pro.crypto.request.IndicatorRequest;
import pro.crypto.response.SimpleIndicatorResult;

import java.math.BigDecimal;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import static java.util.Objects.isNull;
import static pro.crypto.model.indicator.IndicatorType.SIMPLE_MOVING_AVERAGE;
import static pro.crypto.model.indicator.IndicatorType.VARIABLE_INDEX_DYNAMIC_AVERAGE;

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
        return Stream.of(IndicatorResultExtractor.extractIndicatorValues(calculateChandeMomentumOscillator()))
                .map(result -> MathHelper.divide(result, new BigDecimal(100)))
                .toArray(BigDecimal[]::new);
    }

    private SimpleIndicatorResult[] calculateChandeMomentumOscillator() {
        return new ChandeMomentumOscillator(buildCMORequest()).getResult();
    }

    private IndicatorRequest buildCMORequest() {
        return CMORequest.builder()
                .originalData(originalData)
                .period(period)
                .signalLinePeriod(2)
                .movingAverageType(SIMPLE_MOVING_AVERAGE)
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
