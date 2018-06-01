package pro.crypto.indicators.rsi;

import pro.crypto.helper.IndicatorResultExtractor;
import pro.crypto.helper.MathHelper;
import pro.crypto.helper.MinMaxFinder;
import pro.crypto.model.Indicator;
import pro.crypto.model.IndicatorType;
import pro.crypto.model.request.RSIRequest;
import pro.crypto.model.request.StochRSIRequest;
import pro.crypto.model.result.RSIResult;
import pro.crypto.model.tick.Tick;

import java.math.BigDecimal;
import java.util.Objects;
import java.util.stream.Stream;

import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;
import static pro.crypto.model.IndicatorType.STOCHASTIC_RELATIVE_STRENGTH_INDEX;

public class StochasticRelativeStrengthIndex implements Indicator<RSIResult> {

    private final Tick[] originalData;
    private final IndicatorType movingAverageType;
    private final int rsiPeriod;
    private final int stochPeriod;

    private RSIResult[] result;

    public StochasticRelativeStrengthIndex(StochRSIRequest request) {
        this.originalData = request.getOriginalData();
        this.movingAverageType = request.getMovingAverageType();
        this.rsiPeriod = request.getRsiPeriod();
        this.stochPeriod = request.getStochPeriod();
        checkIncomingData();
    }

    @Override
    public IndicatorType getType() {
        return STOCHASTIC_RELATIVE_STRENGTH_INDEX;
    }

    @Override
    public void calculate() {
        result = new RSIResult[originalData.length];
        BigDecimal[] relativeStrengthIndexValues = calculateRelativeStrengthIndexValues();
        calculateStochasticRelativeStrengthIndex(relativeStrengthIndexValues);
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
        checkPeriod(stochPeriod);
        checkPeriod(rsiPeriod);
        checkMovingAverageType(movingAverageType);
        checkOriginalDataSize(originalData, rsiPeriod + stochPeriod);
    }

    private BigDecimal[] calculateRelativeStrengthIndexValues() {
        return IndicatorResultExtractor.extract(calculateRelativeStrengthIndex());
    }

    private RSIResult[] calculateRelativeStrengthIndex() {
        return new RelativeStrengthIndex(buildRSIRequest()).getResult();
    }

    private RSIRequest buildRSIRequest() {
        return RSIRequest.builder()
                .originalData(originalData)
                .movingAverageType(movingAverageType)
                .period(rsiPeriod)
                .build();
    }

    private void calculateStochasticRelativeStrengthIndex(BigDecimal[] relativeStrengthIndexValues) {
        BigDecimal[] minValues = calculateMinValues(relativeStrengthIndexValues);
        BigDecimal[] maxValues = calculateMaxValues(relativeStrengthIndexValues);
        calculateStochasticRelativeStrengthIndex(relativeStrengthIndexValues, minValues, maxValues);
    }

    private BigDecimal[] calculateMinValues(BigDecimal[] relativeStrengthIndexValues) {
        BigDecimal[] minValues = MinMaxFinder.findMinValues(extractNonNullValues(relativeStrengthIndexValues), stochPeriod);
        return addEmptyFields(relativeStrengthIndexValues, minValues);
    }

    private BigDecimal[] calculateMaxValues(BigDecimal[] relativeStrengthIndexValues) {
        BigDecimal[] maxValues = MinMaxFinder.findMaxValues(extractNonNullValues(relativeStrengthIndexValues), stochPeriod);
        return addEmptyFields(relativeStrengthIndexValues, maxValues);
    }

    private BigDecimal[] addEmptyFields(BigDecimal[] relativeStrengthIndexValues, BigDecimal[] values) {
        BigDecimal[] result = new BigDecimal[relativeStrengthIndexValues.length];
        for (int i = 0; i < result.length; i++) {
            result[i] = isNull(relativeStrengthIndexValues[i]) ? null : values[i - rsiPeriod + 1];
        }
        return result;
    }

    private BigDecimal[] extractNonNullValues(BigDecimal[] relativeStrengthIndexValues) {
        return Stream.of(relativeStrengthIndexValues)
                .filter(Objects::nonNull)
                .toArray(BigDecimal[]::new);
    }

    private void calculateStochasticRelativeStrengthIndex(BigDecimal[] relativeStrengthIndexValues, BigDecimal[] minValues, BigDecimal[] maxValues) {
        for (int currentIndex = 0; currentIndex < result.length; currentIndex++) {
            result[currentIndex] = new RSIResult(
                    originalData[currentIndex].getTickTime(),
                    calculateStochasticRelativeStrengthIndex(
                            relativeStrengthIndexValues[currentIndex],
                            minValues[currentIndex],
                            maxValues[currentIndex])
            );
        }
    }

    private BigDecimal calculateStochasticRelativeStrengthIndex(BigDecimal relativeStrengthIndexValue, BigDecimal minValue, BigDecimal maxValue) {
        return nonNull(relativeStrengthIndexValue) && nonNull(minValue) && nonNull(maxValue)
                ? calculateStochasticRelativeStrengthIndexValue(relativeStrengthIndexValue, minValue, maxValue)
                : null;
    }

    //StochRSIn = (RSIn - MIN(RSI)) / (MAX(RSI) - MIN(RSI))
    private BigDecimal calculateStochasticRelativeStrengthIndexValue(BigDecimal relativeStrengthIndexValue, BigDecimal minValue, BigDecimal maxValue) {
        return MathHelper.divide(relativeStrengthIndexValue.subtract(minValue), maxValue.subtract(minValue));
    }

}
