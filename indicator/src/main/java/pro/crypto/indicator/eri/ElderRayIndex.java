package pro.crypto.indicator.eri;

import pro.crypto.helper.FakeTicksCreator;
import pro.crypto.helper.IndicatorResultExtractor;
import pro.crypto.helper.MathHelper;
import pro.crypto.indicator.ma.MARequest;
import pro.crypto.indicator.ma.MovingAverageFactory;
import pro.crypto.model.Indicator;
import pro.crypto.model.IndicatorRequest;
import pro.crypto.model.IndicatorType;
import pro.crypto.model.SimpleIndicatorResult;
import pro.crypto.model.tick.Tick;

import java.math.BigDecimal;
import java.util.stream.IntStream;

import static java.lang.Integer.max;
import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;
import static pro.crypto.model.IndicatorType.*;
import static pro.crypto.model.tick.PriceType.CLOSE;

public class ElderRayIndex implements Indicator<ERIResult> {

    private final Tick[] originalData;
    private final int period;
    private final int signalLinePeriod;
    private final int smoothLinePeriod;

    private ERIResult[] result;

    public ElderRayIndex(IndicatorRequest creationRequest) {
        ERIRequest request = (ERIRequest) creationRequest;
        this.originalData = request.getOriginalData();
        this.period = request.getPeriod();
        this.signalLinePeriod = request.getSignalLinePeriod();
        this.smoothLinePeriod = request.getSmoothLinePeriod();
        checkIncomingData();
    }

    @Override
    public IndicatorType getType() {
        return ELDER_RAY_INDEX;
    }

    @Override
    public void calculate() {
        result = new ERIResult[originalData.length];
        BigDecimal[] movingAverageValues = calculateMovingAverageValues();
        BigDecimal[] indicatorValues = calculateElderRayIndicatorValues(movingAverageValues);
        BigDecimal[] signalLineValues = calculateSignalLineValues(indicatorValues);
        BigDecimal[] smoothedLineValues = calculateSmoothedLineValues(indicatorValues);
        buildElderRayIndicatorResult(movingAverageValues, indicatorValues, signalLineValues, smoothedLineValues);
    }

    @Override
    public ERIResult[] getResult() {
        if (isNull(result)) {
            calculate();
        }
        return result;
    }

    private void checkIncomingData() {
        checkOriginalData(originalData);
        checkOriginalDataSize(originalData, period + max(signalLinePeriod, smoothLinePeriod));
        checkPeriod(period);
        checkPeriod(signalLinePeriod);
        checkPeriod(smoothLinePeriod);
    }

    private BigDecimal[] calculateMovingAverageValues() {
        return IndicatorResultExtractor.extractIndicatorValue(calculateExponentialMovingAverage());
    }

    private SimpleIndicatorResult[] calculateExponentialMovingAverage() {
        return MovingAverageFactory.create(buildEMARequest()).getResult();
    }

    private IndicatorRequest buildEMARequest() {
        return buildMARequest(originalData, EXPONENTIAL_MOVING_AVERAGE, period);
    }

    private BigDecimal[] calculateElderRayIndicatorValues(BigDecimal[] movingAverageValues) {
        return IntStream.range(0, result.length)
                .mapToObj(idx -> calculateElderRayIndicator(originalData[idx], movingAverageValues[idx]))
                .toArray(BigDecimal[]::new);
    }

    private BigDecimal calculateElderRayIndicator(Tick tick, BigDecimal movingAverageValue) {
        return nonNull(movingAverageValue)
                ? calculateElderRayIndicatorValue(tick, movingAverageValue)
                : null;
    }

    private BigDecimal calculateElderRayIndicatorValue(Tick tick, BigDecimal movingAverageValue) {
        return MathHelper.average(tick.getHigh(), tick.getLow()).subtract(movingAverageValue);
    }

    private BigDecimal[] calculateSignalLineValues(BigDecimal[] indicatorValues) {
        BigDecimal[] signalLineValues = IndicatorResultExtractor.extractIndicatorValue(calculateSimpleMovingAverage(indicatorValues));
        BigDecimal[] result = new BigDecimal[originalData.length];
        System.arraycopy(signalLineValues, 0, result, period - 1, signalLineValues.length);
        return result;
    }

    private SimpleIndicatorResult[] calculateSimpleMovingAverage(BigDecimal[] indicatorValues) {
        return MovingAverageFactory.create(buildSMARequest(indicatorValues)).getResult();
    }

    private IndicatorRequest buildSMARequest(BigDecimal[] indicatorValues) {
        return buildMARequest(FakeTicksCreator.createWithCloseOnly(indicatorValues), SIMPLE_MOVING_AVERAGE, signalLinePeriod);
    }

    private BigDecimal[] calculateSmoothedLineValues(BigDecimal[] indicatorValues) {
        BigDecimal[] smoothedLineValues = IndicatorResultExtractor.extractIndicatorValue(calculateSmoothedMovingAverage(indicatorValues));
        BigDecimal[] result = new BigDecimal[originalData.length];
        System.arraycopy(smoothedLineValues, 0, result, period - 1, smoothedLineValues.length);
        return result;
    }

    private SimpleIndicatorResult[] calculateSmoothedMovingAverage(BigDecimal[] indicatorValues) {
        return MovingAverageFactory.create(buildSMMARequest(indicatorValues)).getResult();
    }

    private IndicatorRequest buildSMMARequest(BigDecimal[] indicatorValues) {
        return buildMARequest(FakeTicksCreator.createWithCloseOnly(indicatorValues), SMOOTHED_MOVING_AVERAGE, smoothLinePeriod);
    }

    private IndicatorRequest buildMARequest(Tick[] ticks, IndicatorType indicatorType, int period) {
        return MARequest.builder()
                .originalData(ticks)
                .period(period)
                .priceType(CLOSE)
                .indicatorType(indicatorType)
                .build();
    }

    private void buildElderRayIndicatorResult(BigDecimal[] movingAverageValues, BigDecimal[] indicatorValues,
                                              BigDecimal[] signalLineValues, BigDecimal[] smoothedLineValues) {
        result = IntStream.range(0, indicatorValues.length)
                .mapToObj(idx -> new ERIResult(originalData[idx].getTickTime(), movingAverageValues[idx],
                        indicatorValues[idx], signalLineValues[idx], smoothedLineValues[idx]))
                .toArray(ERIResult[]::new);

    }

}
