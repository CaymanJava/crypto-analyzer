package pro.crypto.indicator.rv;

import pro.crypto.helper.FakeTicksCreator;
import pro.crypto.helper.IndicatorResultExtractor;
import pro.crypto.helper.MathHelper;
import pro.crypto.helper.PriceDifferencesCalculator;
import pro.crypto.indicator.ma.MARequest;
import pro.crypto.indicator.ma.MovingAverageFactory;
import pro.crypto.indicator.stdev.StDevRequest;
import pro.crypto.indicator.stdev.StandardDeviation;
import pro.crypto.model.BigDecimalTuple;
import pro.crypto.model.Indicator;
import pro.crypto.model.indicator.IndicatorType;
import pro.crypto.model.tick.PriceType;
import pro.crypto.model.tick.Tick;
import pro.crypto.request.IndicatorRequest;
import pro.crypto.response.SimpleIndicatorResult;

import java.math.BigDecimal;
import java.util.function.Function;
import java.util.stream.IntStream;

import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;
import static pro.crypto.model.indicator.IndicatorType.EXPONENTIAL_MOVING_AVERAGE;
import static pro.crypto.model.indicator.IndicatorType.RELATIVE_VOLATILITY;
import static pro.crypto.model.indicator.IndicatorType.SIMPLE_MOVING_AVERAGE;
import static pro.crypto.model.tick.PriceType.CLOSE;

public class RelativeVolatility implements Indicator<RVResult> {

    private final Tick[] originalData;
    private final int period;
    private final int stDevPeriod;
    private final PriceType priceType;

    private RVResult[] result;

    public RelativeVolatility(IndicatorRequest creationRequest) {
        RVRequest request = (RVRequest) creationRequest;
        this.originalData = request.getOriginalData();
        this.period = request.getPeriod();
        this.stDevPeriod = request.getStDevPeriod();
        this.priceType = request.getPriceType();
        checkIncomingData();
    }

    @Override
    public IndicatorType getType() {
        return RELATIVE_VOLATILITY;
    }

    @Override
    public void calculate() {
        BigDecimal[] stDevValues = calculateStandardDeviationValues();
        BigDecimalTuple[] stDevDiffValues = calculateStandardDeviationDifference(stDevValues);
        BigDecimal[] rvHighs = calculateHighRelativeVolatility(stDevDiffValues);
        BigDecimal[] rvLows = calculateLowRelativeVolatility(stDevDiffValues);
        calculateRelativeVolatilityResult(rvHighs, rvLows);
    }

    @Override
    public RVResult[] getResult() {
        if (isNull(result)) {
            calculate();
        }
        return result;
    }

    private void checkIncomingData() {
        checkOriginalData(originalData);
        checkOriginalDataSize(originalData, period + stDevPeriod);
        checkPeriod(period);
        checkPeriod(stDevPeriod);
        checkPriceType(priceType);
    }

    private BigDecimal[] calculateStandardDeviationValues() {
        return IndicatorResultExtractor.extractIndicatorValues(calculateStandardDeviation());
    }

    private SimpleIndicatorResult[] calculateStandardDeviation() {
        return new StandardDeviation(buildStDevRequest()).getResult();
    }

    private IndicatorRequest buildStDevRequest() {
        return StDevRequest.builder()
                .originalData(originalData)
                .period(stDevPeriod)
                .movingAverageType(SIMPLE_MOVING_AVERAGE)
                .priceType(priceType)
                .build();
    }

    private BigDecimalTuple[] calculateStandardDeviationDifference(BigDecimal[] stDevValues) {
        BigDecimalTuple[] priceDifferences = PriceDifferencesCalculator.calculatePriceDifference(originalData, priceType);
        return IntStream.range(0, originalData.length)
                .mapToObj(idx -> calculateStandardDeviationDifference(stDevValues[idx], priceDifferences[idx]))
                .toArray(BigDecimalTuple[]::new);
    }

    private BigDecimalTuple calculateStandardDeviationDifference(BigDecimal stDevValue, BigDecimalTuple priceDifference) {
        return nonNull(stDevValue)
                ? defineStandardDeviationDifference(stDevValue, priceDifference)
                : null;
    }

    private BigDecimalTuple defineStandardDeviationDifference(BigDecimal stDevValue, BigDecimalTuple priceDifference) {
        return priceDifference.getLeft().compareTo(BigDecimal.ZERO) == 0 && priceDifference.getRight().compareTo(BigDecimal.ZERO) != 0
                ? new BigDecimalTuple(BigDecimal.ZERO, stDevValue)
                : new BigDecimalTuple(stDevValue, BigDecimal.ZERO);
    }

    private BigDecimal[] calculateHighRelativeVolatility(BigDecimalTuple[] stDevDiffValues) {
        return calculateMovingAverageValues(stDevDiffValues, BigDecimalTuple::getLeft);
    }

    private BigDecimal[] calculateLowRelativeVolatility(BigDecimalTuple[] stDevDiffValues) {
        return calculateMovingAverageValues(stDevDiffValues, BigDecimalTuple::getRight);
    }

    private BigDecimal[] calculateMovingAverageValues(BigDecimalTuple[] stDevDiffValues, Function<BigDecimalTuple, BigDecimal> extractFunction) {
        BigDecimal[] maValues = IndicatorResultExtractor.extractIndicatorValues(calculateMovingAverage(stDevDiffValues, extractFunction));
        BigDecimal[] result = new BigDecimal[originalData.length];
        System.arraycopy(maValues, 0, result, stDevPeriod - 1, maValues.length);
        return result;
    }

    private SimpleIndicatorResult[] calculateMovingAverage(BigDecimalTuple[] stDevDiffValues, Function<BigDecimalTuple, BigDecimal> extractFunction) {
        return MovingAverageFactory.create(buildMARequest(stDevDiffValues, extractFunction)).getResult();
    }

    private IndicatorRequest buildMARequest(BigDecimalTuple[] stDevDiffValues, Function<BigDecimalTuple, BigDecimal> extractFunction) {
        return MARequest.builder()
                .originalData(FakeTicksCreator.createWithCloseOnly(extractDifferences(stDevDiffValues, extractFunction)))
                .priceType(CLOSE)
                .period(period)
                .indicatorType(EXPONENTIAL_MOVING_AVERAGE)
                .build();
    }

    private BigDecimal[] extractDifferences(BigDecimalTuple[] stDevDiffValues, Function<BigDecimalTuple, BigDecimal> extractFunction) {
        return IntStream.range(0, stDevDiffValues.length)
                .filter(idx -> nonNull(stDevDiffValues[idx]))
                .mapToObj(idx -> extractFunction.apply(stDevDiffValues[idx]))
                .toArray(BigDecimal[]::new);
    }

    private void calculateRelativeVolatilityResult(BigDecimal[] rvHighs, BigDecimal[] rvLows) {
        result = IntStream.range(0, originalData.length)
                .mapToObj(idx -> new RVResult(originalData[idx].getTickTime(), calculateRelativeVolatility(rvHighs[idx], rvLows[idx])))
                .toArray(RVResult[]::new);
    }

    private BigDecimal calculateRelativeVolatility(BigDecimal rvHigh, BigDecimal rvLow) {
        return nonNull(rvHigh) && nonNull(rvLow)
                ? calculateRelativeVolatilityValue(rvHigh, rvLow)
                : null;
    }

    private BigDecimal calculateRelativeVolatilityValue(BigDecimal rvHigh, BigDecimal rvLow) {
        return MathHelper.divide(new BigDecimal(100).multiply(rvHigh), rvHigh.add(rvLow));
    }

}
