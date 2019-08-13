package pro.crypto.indicator.rwi;

import pro.crypto.helper.IndicatorResultExtractor;
import pro.crypto.helper.MathHelper;
import pro.crypto.indicator.atr.ATRRequest;
import pro.crypto.indicator.atr.AverageTrueRange;
import pro.crypto.model.Indicator;
import pro.crypto.model.indicator.IndicatorType;
import pro.crypto.model.tick.Tick;
import pro.crypto.request.IndicatorRequest;
import pro.crypto.response.SimpleIndicatorResult;

import java.math.BigDecimal;
import java.util.function.BiFunction;
import java.util.stream.IntStream;

import static java.util.Objects.isNull;
import static pro.crypto.model.indicator.IndicatorType.RANDOM_WALK_INDEX;

public class RandomWalkIndex implements Indicator<RWIResult> {

    private final Tick[] originalData;
    private final int period;

    private BigDecimal[] atrValues;

    private RWIResult[] result;

    public RandomWalkIndex(IndicatorRequest creationRequest) {
        RWIRequest request = (RWIRequest) creationRequest;
        this.originalData = request.getOriginalData();
        this.period = request.getPeriod();
        checkIncomingData();
    }

    @Override
    public IndicatorType getType() {
        return RANDOM_WALK_INDEX;
    }

    @Override
    public void calculate() {
        calculateAverageTrueRangeValues();
        BigDecimal[] rwiHighValues = calculateRandomWalkValues(this::calculatePotentialRandomWalkHigh);
        BigDecimal[] rwiLowValues = calculateRandomWalkValues(this::calculatePotentialRandomWalkLow);
        buildRandomWalkIndexResult(rwiHighValues, rwiLowValues);
    }

    @Override
    public RWIResult[] getResult() {
        if (isNull(result)) {
            calculate();
        }
        return result;
    }

    private void checkIncomingData() {
        checkOriginalData(originalData);
        checkOriginalDataSize(originalData, period * 2);
        checkPeriod(period);
    }

    private void calculateAverageTrueRangeValues() {
        atrValues = IndicatorResultExtractor.extractIndicatorValues(calculateAverageTrueRange());
    }

    private SimpleIndicatorResult[] calculateAverageTrueRange() {
        return new AverageTrueRange(buildATRRequest()).getResult();
    }

    private IndicatorRequest buildATRRequest() {
        return ATRRequest.builder()
                .originalData(originalData)
                .period(period)
                .build();
    }

    private BigDecimal[] calculateRandomWalkValues(BiFunction<Integer, Integer, BigDecimal> randomWalkFunction) {
        return IntStream.range(0, originalData.length)
                .mapToObj(idx -> calculateRandomWalk(idx, randomWalkFunction))
                .toArray(BigDecimal[]::new);
    }

    private BigDecimal calculateRandomWalk(int currentIndex, BiFunction<Integer, Integer, BigDecimal> randomWalkFunction) {
        return currentIndex >= period * 2 - 2
                ? calculateRandomWalkValue(currentIndex, randomWalkFunction)
                : null;
    }

    private BigDecimal calculateRandomWalkValue(int outerIndex, BiFunction<Integer, Integer, BigDecimal> randomWalkFunction) {
        return MathHelper.max(IntStream.range(1, period)
                .mapToObj(idx -> randomWalkFunction.apply(outerIndex, idx))
                .toArray(BigDecimal[]::new));
    }

    // (High(outer) - Low(outer - current)) / (ATR(outer - current) * SQRT(current + 1))
    private BigDecimal calculatePotentialRandomWalkHigh(int outerIndex, int currentIndex) {
        return MathHelper.divide(originalData[outerIndex].getHigh().subtract(originalData[outerIndex - currentIndex].getLow()),
                atrValues[outerIndex - currentIndex].multiply(calculateIndexSqrt(currentIndex)));
    }

    // (High(outer - current) - Low(outer)) / (ATR(outer - current) * SQRT(current + 1))
    private BigDecimal calculatePotentialRandomWalkLow(int outerIndex, int currentIndex) {
        return MathHelper.divide(originalData[outerIndex - currentIndex].getHigh().subtract(originalData[outerIndex].getLow()),
                atrValues[outerIndex - currentIndex].multiply(calculateIndexSqrt(currentIndex)));
    }

    private BigDecimal calculateIndexSqrt(int currentIndex) {
        return MathHelper.sqrt(new BigDecimal(currentIndex + 1));
    }

    private void buildRandomWalkIndexResult(BigDecimal[] rwiHighValues, BigDecimal[] rwiLowValues) {
        result = IntStream.range(0, originalData.length)
                .mapToObj(idx -> new RWIResult(originalData[idx].getTickTime(), rwiHighValues[idx], rwiLowValues[idx]))
                .toArray(RWIResult[]::new);
    }

}
