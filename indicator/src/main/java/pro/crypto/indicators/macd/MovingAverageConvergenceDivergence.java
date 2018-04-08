package pro.crypto.indicators.macd;

import pro.crypto.exception.UnexpectedValueException;
import pro.crypto.exception.WrongIncomingParametersException;
import pro.crypto.helper.FakeTicksCreator;
import pro.crypto.helper.IndicatorTypeChecker;
import pro.crypto.helper.MathHelper;
import pro.crypto.indicators.ma.MovingAverageFactory;
import pro.crypto.model.Indicator;
import pro.crypto.model.IndicatorType;
import pro.crypto.model.request.MACDRequest;
import pro.crypto.model.request.MARequest;
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
        if (nonNull(movingAverageType) && !IndicatorTypeChecker.isMovingAverageType(movingAverageType)) {
            throw new WrongIncomingParametersException(format("Incoming original indicator type is not a moving average {indicator: {%s}}, movingAverageType: {%s}",
                    getType().toString(), movingAverageType.toString()));
        }
    }

    private BigDecimal[] calculateMACD() {
        MAResult[] slowMovingAverageResult = MovingAverageFactory.create(buildSlowMovingAverageCreationRequest()).getResult();
        MAResult[] fastMovingAverageResult = MovingAverageFactory.create(buildFastMovingAverageCreationRequest()).getResult();
        return calculateMACD(slowMovingAverageResult, fastMovingAverageResult);
    }

    private BigDecimal[] calculateMACD(MAResult[] slowMovingAverageResult, MAResult[] fastMovingAverageResult) {
        BigDecimal[] indicatorValues = new BigDecimal[slowMovingAverageResult.length];
        for (int i = 0; i < indicatorValues.length; i++) {
            indicatorValues[i] = calculateDifference(slowMovingAverageResult[i].getIndicatorValue(), fastMovingAverageResult[i].getIndicatorValue());
        }
        return indicatorValues;
    }

    private MARequest buildSlowMovingAverageCreationRequest() {
        return MARequest.builder()
                .originalData(originalData)
                .indicatorType(movingAverageType)
                .period(slowPeriod)
                .priceType(priceType)
                .build();
    }

    private MARequest buildFastMovingAverageCreationRequest() {
        return MARequest.builder()
                .originalData(originalData)
                .indicatorType(movingAverageType)
                .period(fastPeriod)
                .priceType(priceType)
                .build();
    }

    private BigDecimal calculateDifference(BigDecimal minuend, BigDecimal subtrahend) {
        return isNull(minuend) || isNull(subtrahend)
                ? null
                : MathHelper.scaleAndRound(minuend.subtract(subtrahend));
    }

    private BigDecimal[] calculateSignalLineValues(BigDecimal[] indicatorValues) {
        Tick[] fakeTicks = FakeTicksCreator.createWithCloseOnly(indicatorValues);
        MAResult[] emaIndicatorValue = MovingAverageFactory.create(buildSignalLineMovingAverageRequest(fakeTicks))
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

    private MARequest buildSignalLineMovingAverageRequest(Tick[] fakeTicks) {
        return MARequest.builder()
                .originalData(fakeTicks)
                .indicatorType(EXPONENTIAL_MOVING_AVERAGE)
                .period(signalPeriod)
                .priceType(CLOSE)
                .build();
    }

    private BigDecimal[] calculateBarChartValue(BigDecimal[] indicatorValues, BigDecimal[] signalLineValues) {
        BigDecimal[] barChartValues = new BigDecimal[indicatorValues.length];
        for (int i = 0; i < barChartValues.length; i++) {
            barChartValues[i] = calculateDifference(indicatorValues[i], signalLineValues[i]);
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
