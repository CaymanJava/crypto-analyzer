package pro.crypto.indicator.rsi;

import pro.crypto.helper.IndicatorResultExtractor;
import pro.crypto.helper.MathHelper;
import pro.crypto.helper.MinMaxFinder;
import pro.crypto.model.Indicator;
import pro.crypto.model.IndicatorType;
import pro.crypto.model.tick.Tick;

import java.math.BigDecimal;
import java.util.Objects;
import java.util.stream.IntStream;
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
        return IntStream.range(0, relativeStrengthIndexValues.length)
                .mapToObj(idx -> nonNull(relativeStrengthIndexValues[idx]) ? values[idx - rsiPeriod + 1] : null)
                .toArray(BigDecimal[]::new);
    }

    private BigDecimal[] extractNonNullValues(BigDecimal[] relativeStrengthIndexValues) {
        return Stream.of(relativeStrengthIndexValues)
                .filter(Objects::nonNull)
                .toArray(BigDecimal[]::new);
    }

    private void calculateStochasticRelativeStrengthIndex(BigDecimal[] relativeStrengthIndexValues, BigDecimal[] minValues, BigDecimal[] maxValues) {
        IntStream.range(0, result.length)
                .forEach(idx -> result[idx] = new RSIResult(
                        originalData[idx].getTickTime(),
                        calculateStochasticRelativeStrengthIndex(
                                relativeStrengthIndexValues[idx],
                                minValues[idx],
                                maxValues[idx])
                ));
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
