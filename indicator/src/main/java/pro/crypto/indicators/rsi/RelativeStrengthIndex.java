package pro.crypto.indicators.rsi;

import pro.crypto.helper.*;
import pro.crypto.helper.model.BigDecimalTuple;
import pro.crypto.indicators.ma.MovingAverageFactory;
import pro.crypto.model.Indicator;
import pro.crypto.model.IndicatorType;
import pro.crypto.model.request.MARequest;
import pro.crypto.model.request.RSIRequest;
import pro.crypto.model.result.MAResult;
import pro.crypto.model.result.RSIResult;
import pro.crypto.model.tick.Tick;

import java.math.BigDecimal;
import java.util.function.Function;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;
import static pro.crypto.model.IndicatorType.RELATIVE_STRENGTH_INDEX;
import static pro.crypto.model.tick.PriceType.CLOSE;

public class RelativeStrengthIndex implements Indicator<RSIResult> {

    private final Tick[] originalData;
    private final IndicatorType movingAverageType;
    private final int period;

    private RSIResult[] result;

    public RelativeStrengthIndex(RSIRequest request) {
        this.originalData = request.getOriginalData();
        this.movingAverageType = request.getMovingAverageType();
        this.period = request.getPeriod();
        checkIncomingData();
    }

    @Override
    public IndicatorType getType() {
        return RELATIVE_STRENGTH_INDEX;
    }

    @Override
    public void calculate() {
        result = new RSIResult[originalData.length];
        calculateRelativeStrengthIndex(PriceDifferencesCalculator.calculateCloseDifference(originalData));
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
        checkOriginalDataSize(originalData, period);
        checkPeriod(period);
        checkMovingAverageType(movingAverageType);
    }

    private void calculateRelativeStrengthIndex(BigDecimalTuple[] priceDifferences) {
        BigDecimal[] positivePriceMovingAverageValues = calculateMovingAveragePriceValues(extractDifferences(priceDifferences, BigDecimalTuple::getLeft));
        BigDecimal[] negativePriceMovingAverageValues = calculateMovingAveragePriceValues(extractDifferences(priceDifferences, BigDecimalTuple::getRight));
        calculateRelativeStrengthIndexValues(positivePriceMovingAverageValues, negativePriceMovingAverageValues);
    }

    private BigDecimal[] extractDifferences(BigDecimalTuple[] priceDifferences, Function<BigDecimalTuple, BigDecimal> getDifference) {
        return Stream.of(priceDifferences)
                .map(getDifference)
                .toArray(BigDecimal[]::new);
    }

    private BigDecimal[] calculateMovingAveragePriceValues(BigDecimal[] positivePriceDifferences) {
        Tick[] fakeTicks = buildFakeTicksForMovingAverage(positivePriceDifferences);
        return IndicatorResultExtractor.extract(calculateMovingAverage(fakeTicks));
    }

    private MAResult[] calculateMovingAverage(Tick[] faceTicks) {
        return MovingAverageFactory.create(buildMovingAverageRequest(faceTicks)).getResult();
    }

    private Tick[] buildFakeTicksForMovingAverage(BigDecimal[] positivePriceDifferences) {
        return FakeTicksCreator.createWithCloseOnly(positivePriceDifferences);
    }

    private MARequest buildMovingAverageRequest(Tick[] ticks) {
        return MARequest.builder()
                .originalData(ticks)
                .indicatorType(movingAverageType)
                .priceType(CLOSE)
                .period(period)
                .build();
    }

    private void calculateRelativeStrengthIndexValues(BigDecimal[] positivePriceMovingAverageValues, BigDecimal[] negativePriceMovingAverageValues) {
        IntStream.range(0, result.length)
                .forEach(idx -> result[idx] = buildRelativeStrengthIndexResult(positivePriceMovingAverageValues[idx],
                        negativePriceMovingAverageValues[idx], idx));
    }

    private RSIResult buildRelativeStrengthIndexResult(BigDecimal averageGain, BigDecimal averageLoss, int currentIndex) {
        return nonNull(averageGain) && nonNull(averageLoss)
                ? calculateRelativeStrengthIndexValue(averageGain, averageLoss, currentIndex)
                : new RSIResult(originalData[currentIndex].getTickTime(), null);
    }

    private RSIResult calculateRelativeStrengthIndexValue(BigDecimal averageGain, BigDecimal averageLoss, int currentIndex) {
        return isZeroValue(averageLoss)
                ? new RSIResult(originalData[currentIndex].getTickTime(), MathHelper.scaleAndRound(new BigDecimal(100)))
                : new RSIResult(originalData[currentIndex].getTickTime(), calculateRelativeStrengthIndexValue(averageGain, averageLoss));
    }

    private boolean isZeroValue(BigDecimal negativePriceMovingAverageValue) {
        return negativePriceMovingAverageValue.compareTo(BigDecimal.ZERO) == 0;
    }

    //RSI = 100 - (100 / (RS + 1))
    //RS = MA(positive) / MA(negative)
    private BigDecimal calculateRelativeStrengthIndexValue(BigDecimal averageGain, BigDecimal averageLoss) {
        BigDecimal relativeValue = MathHelper.divide(new BigDecimal(100),
                BigDecimal.ONE.add(calculateRelativeStrength(averageGain, averageLoss)));
        return nonNull(relativeValue)
                ? new BigDecimal(100).subtract(relativeValue)
                : null;
    }

    private BigDecimal calculateRelativeStrength(BigDecimal averageGain, BigDecimal averageLoss) {
        return MathHelper.divide(averageGain, averageLoss);
    }

}
