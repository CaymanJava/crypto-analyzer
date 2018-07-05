package pro.crypto.indicator.macd;

import pro.crypto.exception.UnexpectedValueException;
import pro.crypto.helper.FakeTicksCreator;
import pro.crypto.helper.IndicatorResultExtractor;
import pro.crypto.helper.MathHelper;
import pro.crypto.indicator.ma.MAResult;
import pro.crypto.indicator.ma.MovingAverageFactory;
import pro.crypto.model.Indicator;
import pro.crypto.model.IndicatorType;
import pro.crypto.indicator.ma.MARequest;
import pro.crypto.model.tick.PriceType;
import pro.crypto.model.tick.Tick;

import java.math.BigDecimal;
import java.util.stream.IntStream;

import static java.lang.String.format;
import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;
import static pro.crypto.model.IndicatorType.EXPONENTIAL_MOVING_AVERAGE;
import static pro.crypto.model.IndicatorType.MOVING_AVERAGE_CONVERGENCE_DIVERGENCE;
import static pro.crypto.model.tick.PriceType.CLOSE;

public class MovingAverageConvergenceDivergence implements Indicator<MACDResult> {

    private final Tick[] originalData;
    private final IndicatorType movingAverageType;
    private final PriceType priceType;
    private final int slowPeriod;
    private final int fastPeriod;
    private final int signalPeriod;

    private MACDResult[] result;

    public MovingAverageConvergenceDivergence(MACDRequest request) {
        this.originalData = request.getOriginalData();
        this.movingAverageType = isNull(request.getMovingAverageType()) ? EXPONENTIAL_MOVING_AVERAGE : request.getMovingAverageType();
        this.priceType = request.getPriceType();
        this.slowPeriod = request.getSlowPeriod();
        this.fastPeriod = request.getFastPeriod();
        this.signalPeriod = request.getSignalPeriod();
        checkIncomingData();
    }

    @Override
    public IndicatorType getType() {
        return MOVING_AVERAGE_CONVERGENCE_DIVERGENCE;
    }

    @Override
    public void calculate() {
        this.result = new MACDResult[originalData.length];
        BigDecimal[] indicatorValues = calculateMACD();
        BigDecimal[] signalLineValues = calculateSignalLineValues(indicatorValues);
        BigDecimal[] barChartValues = calculateBarChartValue(indicatorValues, signalLineValues);
        buildResult(indicatorValues, signalLineValues, barChartValues);
    }

    @Override
    public MACDResult[] getResult() {
        if (isNull(result)) {
            calculate();
        }
        return result;
    }

    private void checkIncomingData() {
        checkOriginalData(originalData);
        checkOriginalDataSize(originalData, fastPeriod + signalPeriod);
        checkPriceType(priceType);
        checkPeriods();
        checkMovingAverageType(movingAverageType);
    }

    private void checkPeriods() {
        checkPeriod(slowPeriod);
        checkPeriod(fastPeriod);
        checkPeriod(signalPeriod);
    }

    private BigDecimal[] calculateMACD() {
        MAResult[] slowMovingAverageResult = MovingAverageFactory.create(buildMARequest(slowPeriod)).getResult();
        MAResult[] fastMovingAverageResult = MovingAverageFactory.create(buildMARequest(fastPeriod)).getResult();
        return calculateMACD(slowMovingAverageResult, fastMovingAverageResult);
    }

    private BigDecimal[] calculateMACD(MAResult[] slowMovingAverageResult, MAResult[] fastMovingAverageResult) {
        return IntStream.range(0, slowMovingAverageResult.length)
                .mapToObj(idx -> calculateDifference(slowMovingAverageResult[idx].getIndicatorValue(), fastMovingAverageResult[idx].getIndicatorValue()))
                .toArray(BigDecimal[]::new);
    }

    private MARequest buildMARequest(int period) {
        return MARequest.builder()
                .originalData(originalData)
                .indicatorType(movingAverageType)
                .period(period)
                .priceType(priceType)
                .build();
    }

    private BigDecimal calculateDifference(BigDecimal minuend, BigDecimal subtrahend) {
        return nonNull(minuend) && nonNull(subtrahend)
                ? MathHelper.scaleAndRound(minuend.subtract(subtrahend))
                : null;
    }

    private BigDecimal[] calculateSignalLineValues(BigDecimal[] indicatorValues) {
        BigDecimal[] emaIndicatorValue = IndicatorResultExtractor.extract(MovingAverageFactory.create(buildSignalLineMovingAverageRequest(indicatorValues))
                .getResult());
        return copyEmaIndicatorValueToResultArray(indicatorValues, emaIndicatorValue);
    }

    private BigDecimal[] copyEmaIndicatorValueToResultArray(BigDecimal[] indicatorValues, BigDecimal[] emaIndicatorValue) {
        int startPosition = findSignalLineStartPosition(indicatorValues);
        BigDecimal[] signalLineResult = new BigDecimal[indicatorValues.length];
        System.arraycopy(emaIndicatorValue, 0, signalLineResult, startPosition, emaIndicatorValue.length);
        return signalLineResult;
    }

    private int findSignalLineStartPosition(BigDecimal[] indicatorValues) {
        return IntStream.range(0, indicatorValues.length)
                .filter(idx -> nonNull(indicatorValues[idx]))
                .findFirst()
                .orElseThrow(() -> new UnexpectedValueException(format("Can't find any non null value in array of indicator value's {indicator: {%s}}",
                        getType().toString())));
    }

    private MARequest buildSignalLineMovingAverageRequest(BigDecimal[] indicatorValues) {
        return MARequest.builder()
                .originalData(FakeTicksCreator.createWithCloseOnly(indicatorValues))
                .indicatorType(EXPONENTIAL_MOVING_AVERAGE)
                .period(signalPeriod)
                .priceType(CLOSE)
                .build();
    }

    private BigDecimal[] calculateBarChartValue(BigDecimal[] indicatorValues, BigDecimal[] signalLineValues) {
        return IntStream.range(0, indicatorValues.length)
                .mapToObj(idx -> calculateDifference(indicatorValues[idx], signalLineValues[idx]))
                .toArray(BigDecimal[]::new);
    }

    private void buildResult(BigDecimal[] indicatorValues, BigDecimal[] signalLineValues, BigDecimal[] barChartValues) {
        IntStream.range(0, result.length)
                .forEach(idx -> result[idx] = buildMACDResult(indicatorValues[idx], signalLineValues[idx], barChartValues[idx], idx));
    }

    private MACDResult buildMACDResult(BigDecimal indicatorValue, BigDecimal signalLineValue, BigDecimal barChartValue, int currentIndex) {
        return new MACDResult(
                originalData[currentIndex].getTickTime(),
                indicatorValue,
                signalLineValue,
                barChartValue);
    }

}
