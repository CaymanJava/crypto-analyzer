package pro.crypto.indicator.rsi;

import pro.crypto.helper.FakeTicksCreator;
import pro.crypto.helper.IndicatorResultExtractor;
import pro.crypto.helper.MathHelper;
import pro.crypto.helper.PriceDifferencesCalculator;
import pro.crypto.indicator.ma.MARequest;
import pro.crypto.indicator.ma.MovingAverageFactory;
import pro.crypto.model.BigDecimalTuple;
import pro.crypto.model.Indicator;
import pro.crypto.model.indicator.IndicatorType;
import pro.crypto.model.tick.Tick;
import pro.crypto.request.IndicatorRequest;
import pro.crypto.response.SimpleIndicatorResult;

import java.math.BigDecimal;
import java.util.function.Function;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;
import static pro.crypto.model.indicator.IndicatorType.RELATIVE_STRENGTH_INDEX;
import static pro.crypto.model.tick.PriceType.CLOSE;

public class RelativeStrengthIndex implements Indicator<RSIResult> {

    private final Tick[] originalData;
    private final IndicatorType movingAverageType;
    private final int period;

    private RSIResult[] result;

    public RelativeStrengthIndex(IndicatorRequest creationRequest) {
        RSIRequest request = (RSIRequest) creationRequest;
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
        calculateRelativeStrengthIndex(PriceDifferencesCalculator.calculatePriceDifference(originalData, CLOSE));
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
        return IndicatorResultExtractor.extractIndicatorValues(calculateMovingAverage(fakeTicks));
    }

    private SimpleIndicatorResult[] calculateMovingAverage(Tick[] faceTicks) {
        return MovingAverageFactory.create(buildMovingAverageRequest(faceTicks)).getResult();
    }

    private Tick[] buildFakeTicksForMovingAverage(BigDecimal[] positivePriceDifferences) {
        return FakeTicksCreator.createWithCloseOnly(positivePriceDifferences);
    }

    private IndicatorRequest buildMovingAverageRequest(Tick[] ticks) {
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
    // If we use MA with price deviation (like TEMA) we'll get sometimes values > 100 and < 0
    // I don't know how correct it is to use such MA for RSI calculation =)
    private BigDecimal calculateRelativeStrengthIndexValue(BigDecimal averageGain, BigDecimal averageLoss) {
        BigDecimal rsiValue = calculateRelativeStrengthIndex(averageGain, averageLoss);
        if (isNull(rsiValue)){
            return null;
        }
        if (rsiValue.compareTo(BigDecimal.ZERO) < 0) {
            return BigDecimal.ZERO;
        }
        if (rsiValue.compareTo(new BigDecimal(100)) > 0) {
            return new BigDecimal(100);
        }
        return rsiValue;
    }

    private BigDecimal calculateRelativeStrengthIndex(BigDecimal averageGain, BigDecimal averageLoss) {
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
