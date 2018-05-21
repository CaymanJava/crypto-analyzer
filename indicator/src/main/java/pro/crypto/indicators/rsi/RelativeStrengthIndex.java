package pro.crypto.indicators.rsi;

import pro.crypto.helper.FakeTicksCreator;
import pro.crypto.helper.IndicatorResultExtractor;
import pro.crypto.helper.MathHelper;
import pro.crypto.indicators.ma.MovingAverageFactory;
import pro.crypto.model.Indicator;
import pro.crypto.model.IndicatorType;
import pro.crypto.model.request.MARequest;
import pro.crypto.model.request.RSIRequest;
import pro.crypto.model.result.MAResult;
import pro.crypto.model.result.RSIResult;
import pro.crypto.model.tick.Tick;

import java.math.BigDecimal;
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
        BigDecimal[] priceDifferences = calculatePriceDifference();
        BigDecimal[] positivePriceDifferences = extractPositivePriceDifferences(priceDifferences);
        BigDecimal[] negativePriceDifferences = extractNegativePriceDifferences(priceDifferences);
        calculateRelativeStrengthIndex(positivePriceDifferences, negativePriceDifferences);
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

    private BigDecimal[] calculatePriceDifference() {
        BigDecimal[] priceDifferences = new BigDecimal[originalData.length];
        priceDifferences[0] = BigDecimal.ZERO;
        for (int i = 1; i < priceDifferences.length; i++) {
            priceDifferences[i] = originalData[i].getClose().subtract(originalData[i - 1].getClose());
        }
        return priceDifferences;
    }

    private BigDecimal[] extractPositivePriceDifferences(BigDecimal[] priceDifferences) {
        return Stream.of(priceDifferences)
                .map(priceDifference -> priceDifference.compareTo(BigDecimal.ZERO) > 0
                            ? priceDifference
                            : BigDecimal.ZERO)
                .toArray(BigDecimal[]::new);
    }

    private BigDecimal[] extractNegativePriceDifferences(BigDecimal[] priceDifferences) {
        return Stream.of(priceDifferences)
                .map(priceDifference -> priceDifference.compareTo(BigDecimal.ZERO) < 0
                        ? priceDifference.abs()
                        : BigDecimal.ZERO)
                .toArray(BigDecimal[]::new);
    }

    private void calculateRelativeStrengthIndex(BigDecimal[] positivePriceDifferences, BigDecimal[] negativePriceDifferences) {
        BigDecimal[] positivePriceMovingAverageValues = calculateMovingAveragePriceValues(positivePriceDifferences);
        BigDecimal[] negativePriceMovingAverageValues = calculateMovingAveragePriceValues(negativePriceDifferences);
        calculateRelativeStrengthIndexValues(positivePriceMovingAverageValues, negativePriceMovingAverageValues);
    }

    private BigDecimal[] calculateMovingAveragePriceValues(BigDecimal[] positivePriceDifferences) {
        Tick[] faceTicks = buildFakeTicksForMovingAverage(positivePriceDifferences);
        return IndicatorResultExtractor.extract(calculateMovingAverage(faceTicks));
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
        for (int i = 0; i < result.length; i++) {
            result[i] = buildRelativeStrengthIndexResult(positivePriceMovingAverageValues[i], negativePriceMovingAverageValues[i], i);
        }
    }

    private RSIResult buildRelativeStrengthIndexResult(BigDecimal averageGain, BigDecimal averageLoss, int currentIndex) {
        return isNull(averageGain) || isNull(averageLoss)
                ? new RSIResult(originalData[currentIndex].getTickTime(), null)
                : calculateRelativeStrengthIndexValue(averageGain, averageLoss, currentIndex);
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
