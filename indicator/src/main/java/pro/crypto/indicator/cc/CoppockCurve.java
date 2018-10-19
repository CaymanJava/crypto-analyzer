package pro.crypto.indicator.cc;

import pro.crypto.exception.WrongIncomingParametersException;
import pro.crypto.helper.FakeTicksCreator;
import pro.crypto.helper.IndicatorResultExtractor;
import pro.crypto.indicator.ma.MARequest;
import pro.crypto.indicator.ma.MovingAverageFactory;
import pro.crypto.indicator.roc.ROCRequest;
import pro.crypto.indicator.roc.RateOfChange;
import pro.crypto.model.Indicator;
import pro.crypto.model.IndicatorRequest;
import pro.crypto.model.IndicatorType;
import pro.crypto.model.SimpleIndicatorResult;
import pro.crypto.model.tick.PriceType;
import pro.crypto.model.tick.Tick;

import java.math.BigDecimal;
import java.util.stream.IntStream;

import static java.lang.String.format;
import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;
import static pro.crypto.model.IndicatorType.COPPOCK_CURVE;
import static pro.crypto.model.IndicatorType.WEIGHTED_MOVING_AVERAGE;
import static pro.crypto.model.tick.PriceType.CLOSE;

public class CoppockCurve implements Indicator<CCResult> {

    private final Tick[] originalData;
    private final int period;
    private final int shortROCPeriod;
    private final int longROCPeriod;
    private final PriceType priceType;

    private CCResult[] result;

    public CoppockCurve(IndicatorRequest creationRequest) {
        CCRequest request = (CCRequest) creationRequest;
        this.originalData = request.getOriginalData();
        this.period = request.getPeriod();
        this.shortROCPeriod = request.getShortROCPeriod();
        this.longROCPeriod = request.getLongROCPeriod();
        this.priceType = request.getPriceType();
        checkIncomingData();
    }

    @Override
    public IndicatorType getType() {
        return COPPOCK_CURVE;
    }

    @Override
    public void calculate() {
        result = new CCResult[originalData.length];
        BigDecimal[] longROCValues = calculateLongRateOfChange();
        BigDecimal[] shortROCValues = calculateShortRateOfChange();
        BigDecimal[] notSmoothedCCValues = calculateNotSmoothedCoppockCurve(longROCValues, shortROCValues);
        calculateCoppockCurveResult(notSmoothedCCValues);
    }

    @Override
    public CCResult[] getResult() {
        if (isNull(result)) {
            calculate();
        }
        return result;
    }

    private void checkIncomingData() {
        checkOriginalData(originalData);
        checkOriginalDataSize(originalData, period + longROCPeriod);
        checkPriceType(priceType);
        checkPeriods();
    }

    private void checkPeriods() {
        checkPeriod(period);
        checkPeriod(shortROCPeriod);
        checkPeriod(longROCPeriod);
        checkPeriodLength();
    }

    private void checkPeriodLength() {
        if (shortROCPeriod >= longROCPeriod) {
            throw new WrongIncomingParametersException(format("Short RoC Period should be less than Long Roc Period " +
                            "{indicator: {%s}, conversionLinePeriod: {%d}, baseLinePeriod: {%d}}",
                    getType().toString(), shortROCPeriod, longROCPeriod));
        }
    }

    private BigDecimal[] calculateLongRateOfChange() {
        return IndicatorResultExtractor.extractIndicatorValue(calculateRateOfChange(longROCPeriod));
    }

    private BigDecimal[] calculateShortRateOfChange() {
        return IndicatorResultExtractor.extractIndicatorValue(calculateRateOfChange(shortROCPeriod));
    }

    private SimpleIndicatorResult[] calculateRateOfChange(int longRocPeriod) {
        return new RateOfChange(buildROCRequest(longRocPeriod)).getResult();
    }

    private IndicatorRequest buildROCRequest(int longRocPeriod) {
        return ROCRequest.builder()
                .originalData(originalData)
                .period(longRocPeriod)
                .priceType(priceType)
                .build();
    }

    private BigDecimal[] calculateNotSmoothedCoppockCurve(BigDecimal[] longRoCValues, BigDecimal[] shortRocValues) {
        return IntStream.range(0, originalData.length)
                .mapToObj(idx -> calculateNotSmoothedCoppockCurve(longRoCValues[idx], shortRocValues[idx]))
                .toArray(BigDecimal[]::new);
    }

    private BigDecimal calculateNotSmoothedCoppockCurve(BigDecimal longRoCValue, BigDecimal shortRocValue) {
        return nonNull(longRoCValue) && nonNull(shortRocValue)
                ? longRoCValue.add(shortRocValue)
                : null;
    }

    private void calculateCoppockCurveResult(BigDecimal[] notSmoothedCCValues) {
        BigDecimal[] wmaResult = calculateWeightedMovingAverage(notSmoothedCCValues);
        IntStream.range(0, result.length)
                .forEach(idx -> result[idx] = new CCResult(originalData[idx].getTickTime(), wmaResult[idx]));
    }

    private BigDecimal[] calculateWeightedMovingAverage(BigDecimal[] notSmoothedCCValues) {
        BigDecimal[] wmaResult = IndicatorResultExtractor.extractIndicatorValue(calculateMovingAverage(notSmoothedCCValues));
        BigDecimal[] smoothedCC = new BigDecimal[originalData.length];
        System.arraycopy(wmaResult, 0, smoothedCC, longROCPeriod, wmaResult.length);
        return smoothedCC;
    }

    private SimpleIndicatorResult[] calculateMovingAverage(BigDecimal[] notSmoothedCCValues) {
        return MovingAverageFactory.create(buildMARequest(notSmoothedCCValues)).getResult();
    }

    private IndicatorRequest buildMARequest(BigDecimal[] notSmoothedCCValues) {
        return MARequest.builder()
                .originalData(FakeTicksCreator.createWithCloseOnly(notSmoothedCCValues))
                .period(period)
                .priceType(CLOSE)
                .indicatorType(WEIGHTED_MOVING_AVERAGE)
                .build();
    }

}
