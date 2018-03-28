package pro.crypto.indicators.macd;

import pro.crypto.exception.UnexpectedValueException;
import pro.crypto.exception.WrongIncomingParametersException;
import pro.crypto.helper.FakeTicksCreator;
import pro.crypto.helper.MathHelper;
import pro.crypto.indicators.ma.MovingAverageFactory;
import pro.crypto.model.Indicator;
import pro.crypto.model.IndicatorType;
import pro.crypto.model.request.MACDCreationRequest;
import pro.crypto.model.request.MACreationRequest;
import pro.crypto.model.result.MACDResult;
import pro.crypto.model.result.MAResult;
import pro.crypto.model.tick.PriceType;
import pro.crypto.model.tick.Tick;

import java.math.BigDecimal;

import static java.lang.String.format;
import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;
import static pro.crypto.model.IndicatorType.*;
import static pro.crypto.model.tick.PriceType.CLOSE;

public class MovingAverageConvergenceDivergence implements Indicator<MACDResult> {

    private final Tick[] originalData;
    private final IndicatorType movingAverageType;
    private final PriceType priceType;
    private final int slowPeriod;
    private final int fastPeriod;
    private final int signalPeriod;

    private MACDResult[] result;

    public MovingAverageConvergenceDivergence(MACDCreationRequest request) {
        this.originalData = request.getOriginalData();
        this.movingAverageType = request.getMovingAverageType();
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
        BigDecimal[] indicatorValues = countMACD();
        BigDecimal[] signalLineValues = countSignalLineValues(indicatorValues);
        BigDecimal[] barChartValues = countBarChartValue(indicatorValues, signalLineValues);
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
        checkPriceType(priceType);
        checkPeriods();
        checkMovingAverageType();
    }

    private void checkPeriods() {
        checkPeriod(slowPeriod);
        checkPeriod(fastPeriod);
        checkPeriod(signalPeriod);
        checkIncomingDataLength();
    }

    private void checkMovingAverageType() {
        if (!isMovingAverageType()) {
            throw new WrongIncomingParametersException(format("Incoming original indicator type is not supported {indicator: {%s}, movingAverageType: {%s}}",
                    getType().toString(), movingAverageType));
        }
    }

    private boolean isMovingAverageType() {
        return movingAverageType == SIMPLE_MOVING_AVERAGE ||
                movingAverageType == EXPONENTIAL_MOVING_AVERAGE ||
                movingAverageType == WEIGHTED_MOVING_AVERAGE ||
                movingAverageType == SMOOTHED_MOVING_AVERAGE ||
                movingAverageType == HULL_MOVING_AVERAGE;
    }

    private BigDecimal[] countMACD() {
        MAResult[] slowMovingAverageResult = MovingAverageFactory.createMovingAverage(buildSlowMovingAverageCreationRequest()).getResult();
        MAResult[] fastMovingAverageResult = MovingAverageFactory.createMovingAverage(buildFastMovingAverageCreationRequest()).getResult();
        return countMACD(slowMovingAverageResult, fastMovingAverageResult);
    }

    private BigDecimal[] countMACD(MAResult[] slowMovingAverageResult, MAResult[] fastMovingAverageResult) {
        BigDecimal[] indicatorValues = new BigDecimal[slowMovingAverageResult.length];
        for (int i = 0; i < indicatorValues.length; i++) {
            indicatorValues[i] = countDifference(slowMovingAverageResult[i].getIndicatorValue(), fastMovingAverageResult[i].getIndicatorValue());
        }
        return indicatorValues;
    }

    private MACreationRequest buildSlowMovingAverageCreationRequest() {
        return MACreationRequest.builder()
                .originalData(originalData)
                .indicatorType(movingAverageType)
                .period(slowPeriod)
                .priceType(priceType)
                .build();
    }

    private MACreationRequest buildFastMovingAverageCreationRequest() {
        return MACreationRequest.builder()
                .originalData(originalData)
                .indicatorType(movingAverageType)
                .period(fastPeriod)
                .priceType(priceType)
                .build();
    }

    private BigDecimal countDifference(BigDecimal minuend, BigDecimal subtrahend) {
        return isNull(minuend) || isNull(subtrahend)
                ? null
                : MathHelper.scaleAndRound(minuend.subtract(subtrahend));
    }

    private BigDecimal[] countSignalLineValues(BigDecimal[] indicatorValues) {
        Tick[] fakeTicks = FakeTicksCreator.createFakeTicksWithCloseOnly(indicatorValues);
        MAResult[] emaIndicatorValue = MovingAverageFactory.createMovingAverage(buildSignalLineMovingAverageRequest(fakeTicks))
                .getResult();
        return copyEmaIndicatorValueToResultArray(indicatorValues, emaIndicatorValue);
    }

    private BigDecimal[] copyEmaIndicatorValueToResultArray(BigDecimal[] indicatorValues, MAResult[] emaIndicatorValue) {
        int startPosition = findSignalLineStartPosition(indicatorValues);
        BigDecimal[] signalLineResult = new BigDecimal[indicatorValues.length];
        for (int i = 0; i < emaIndicatorValue.length; i++) {
            signalLineResult[i + startPosition] = emaIndicatorValue[i].getIndicatorValue();
        }
        return signalLineResult;
    }

    private int findSignalLineStartPosition(BigDecimal[] indicatorValues) {
        for (int i = 0; i < indicatorValues.length; i++) {
            if (nonNull(indicatorValues[i])) {
                return i;
            }
        }
        throw new UnexpectedValueException(format("Can't find any non null value in array of indicator value's {indicator: {%s}}", getType().toString()));
    }

    private MACreationRequest buildSignalLineMovingAverageRequest(Tick[] fakeTicks) {
        return MACreationRequest.builder()
                .originalData(fakeTicks)
                .indicatorType(EXPONENTIAL_MOVING_AVERAGE)
                .period(signalPeriod)
                .priceType(CLOSE)
                .build();
    }

    private BigDecimal[] countBarChartValue(BigDecimal[] indicatorValues, BigDecimal[] signalLineValues) {
        BigDecimal[] barChartValues = new BigDecimal[indicatorValues.length];
        for (int i = 0; i < barChartValues.length; i++) {
            barChartValues[i] = countDifference(indicatorValues[i], signalLineValues[i]);
        }
        return barChartValues;
    }

    private void buildResult(BigDecimal[] indicatorValues, BigDecimal[] signalLineValues, BigDecimal[] barChartValues) {
        for (int i = 0; i < result.length; i++) {
            result[i] = buildMACDResult(indicatorValues[i], signalLineValues[i], barChartValues[i], i);
        }
    }

    private MACDResult buildMACDResult(BigDecimal indicatorValue, BigDecimal signalLineValue, BigDecimal barChartValue, int currentIndex) {
        return new MACDResult(
                originalData[currentIndex].getTickTime(),
                originalData[currentIndex].getPriceByType(priceType),
                indicatorValue,
                signalLineValue,
                barChartValue);
    }

    private void checkIncomingDataLength() {
        if (originalData.length <= slowPeriod + signalPeriod) {
            throw new WrongIncomingParametersException(format("Incoming tick data is not enough " +
                            "{indicator: {%s}, tickLength: {%d}, slowPeriod: {%d}, signalPeriod: {%d}}",
                    getType().toString(), originalData.length, slowPeriod, signalPeriod));
        }
    }

}
