package pro.crypto.indicator.kst;

import pro.crypto.helper.FakeTicksCreator;
import pro.crypto.helper.IndicatorResultExtractor;
import pro.crypto.helper.MathHelper;
import pro.crypto.indicator.ma.MARequest;
import pro.crypto.indicator.ma.MAResult;
import pro.crypto.indicator.ma.MovingAverageFactory;
import pro.crypto.indicator.roc.RangeOfChange;
import pro.crypto.model.Indicator;
import pro.crypto.model.IndicatorType;
import pro.crypto.indicator.roc.ROCRequest;
import pro.crypto.indicator.roc.ROCResult;
import pro.crypto.model.tick.PriceType;
import pro.crypto.model.tick.Tick;

import java.math.BigDecimal;
import java.util.stream.IntStream;

import static java.util.Arrays.asList;
import static java.util.Collections.max;
import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;
import static pro.crypto.model.IndicatorType.KNOW_SURE_THING;
import static pro.crypto.model.IndicatorType.SIMPLE_MOVING_AVERAGE;
import static pro.crypto.model.tick.PriceType.CLOSE;

public class KnowSureThing implements Indicator<KSTResult> {

    private final Tick[] originalData;
    private final PriceType priceType;
    private final int lightestROCPeriod;
    private final int lightestSMAPeriod;
    private final int lightROCPeriod;
    private final int lightSMAPeriod;
    private final int heavyROCPeriod;
    private final int heavySMAPeriod;
    private final int heaviestROCPeriod;
    private final int heaviestSMAPeriod;
    private final int signalLinePeriod;

    private KSTResult[] result;

    public KnowSureThing(KSTRequest request) {
        this.originalData = request.getOriginalData();
        this.priceType = request.getPriceType();
        this.lightestROCPeriod = request.getLightestROCPeriod();
        this.lightestSMAPeriod = request.getLightestSMAPeriod();
        this.lightROCPeriod = request.getLightROCPeriod();
        this.lightSMAPeriod = request.getLightSMAPeriod();
        this.heavyROCPeriod = request.getHeavyROCPeriod();
        this.heavySMAPeriod = request.getHeavySMAPeriod();
        this.heaviestROCPeriod = request.getHeaviestROCPeriod();
        this.heaviestSMAPeriod = request.getHeaviestSMAPeriod();
        this.signalLinePeriod = request.getSignalLinePeriod();
        checkIncomingData();
    }

    @Override
    public IndicatorType getType() {
        return KNOW_SURE_THING;
    }

    @Override
    public void calculate() {
        BigDecimal[] knowSureThingValues = calculateKnowSureThing();
        BigDecimal[] signalLineValues = calculateSignalLine(knowSureThingValues);
        buildKnowSureThingResult(knowSureThingValues, signalLineValues);
    }

    @Override
    public KSTResult[] getResult() {
        if (isNull(result)) {
            calculate();
        }
        return result;
    }

    private void checkIncomingData() {
        checkOriginalData(originalData);
        checkPeriods();
        checkOriginalDataSize(originalData, calculateMaxSumIndex() + signalLinePeriod);
        checkPriceType(priceType);
    }

    private void checkPeriods() {
        checkPeriod(lightestROCPeriod);
        checkPeriod(lightestSMAPeriod);
        checkPeriod(lightROCPeriod);
        checkPeriod(lightSMAPeriod);
        checkPeriod(heavyROCPeriod);
        checkPeriod(heavySMAPeriod);
        checkPeriod(heaviestROCPeriod);
        checkPeriod(heaviestSMAPeriod);
        checkPeriod(signalLinePeriod);
    }

    private BigDecimal[] calculateKnowSureThing() {
        BigDecimal[] lightestROCValues = calculateLightestROCValues();
        BigDecimal[] lightROCValues = calculateLightROCValues();
        BigDecimal[] heavyROCValues = calculateHeavyROCValues();
        BigDecimal[] heaviestROCValues = calculateHeaviestROCValues();
        return calculateKnowSureThing(lightestROCValues, lightROCValues, heavyROCValues, heaviestROCValues);
    }

    private BigDecimal[] calculateLightestROCValues() {
        return calculateSmoothedRangeOfChange(lightestROCPeriod, lightestSMAPeriod);
    }

    private BigDecimal[] calculateLightROCValues() {
        return calculateSmoothedRangeOfChange(lightROCPeriod, lightSMAPeriod);
    }

    private BigDecimal[] calculateHeavyROCValues() {
        return calculateSmoothedRangeOfChange(heavyROCPeriod, heavySMAPeriod);
    }

    private BigDecimal[] calculateHeaviestROCValues() {
        return calculateSmoothedRangeOfChange(heaviestROCPeriod, heaviestSMAPeriod);
    }

    private BigDecimal[] calculateSmoothedRangeOfChange(int rangeOfChangePeriod, int movingAveragePeriod) {
        BigDecimal[] rocValues = calculateRangeOfChangeValues(rangeOfChangePeriod);
        BigDecimal[] smoothedRocValues = calculateMovingAverageValues(rocValues, movingAveragePeriod);
        BigDecimal[] result = new BigDecimal[originalData.length];
        System.arraycopy(smoothedRocValues, 0, result, rangeOfChangePeriod, smoothedRocValues.length);
        return result;
    }

    private BigDecimal[] calculateRangeOfChangeValues(int period) {
        return IndicatorResultExtractor.extract(calculateRangeOfChange(period));
    }

    private ROCResult[] calculateRangeOfChange(int period) {
        return new RangeOfChange(buildROCRequest(period)).getResult();
    }

    private ROCRequest buildROCRequest(int period) {
        return ROCRequest.builder()
                .originalData(originalData)
                .period(period)
                .priceType(priceType)
                .build();
    }

    private BigDecimal[] calculateKnowSureThing(BigDecimal[] lightestROCValues, BigDecimal[] lightROCValues,
                                                BigDecimal[] heavyROCValues, BigDecimal[] heaviestROCValues) {
        return IntStream.range(0, originalData.length)
                .mapToObj(idx -> calculateKnowSureThing(lightestROCValues[idx], lightROCValues[idx], heavyROCValues[idx], heaviestROCValues[idx]))
                .toArray(BigDecimal[]::new);
    }

    private BigDecimal calculateKnowSureThing(BigDecimal lightestROCValue, BigDecimal lightROCValue,
                                              BigDecimal heavyROCValue, BigDecimal heaviestROCValue) {
        return nonNull(lightestROCValue) && nonNull(lightROCValue) && nonNull(heavyROCValue) && nonNull(heaviestROCValue)
                ? calculateKnowSureThingValue(lightestROCValue, lightROCValue, heavyROCValue, heaviestROCValue)
                : null;
    }

    // KST = (RCMA(lightest) * 1) + (RCMA(light) * 2) + (RCMA(heavy) * 3) + (RCMA(heaviest) * 4)
    private BigDecimal calculateKnowSureThingValue(BigDecimal lightestROCValue, BigDecimal lightROCValue,
                                                   BigDecimal heavyROCValue, BigDecimal heaviestROCValue) {
        return MathHelper.sum(lightestROCValue, lightROCValue.multiply(new BigDecimal(2)),
                heavyROCValue.multiply(new BigDecimal(3)), heaviestROCValue.multiply(new BigDecimal(4)));
    }

    private BigDecimal[] calculateSignalLine(BigDecimal[] knowSureThingValues) {
        BigDecimal[] smoothedKSTValues = calculateMovingAverageValues(knowSureThingValues, signalLinePeriod);
        BigDecimal[] result = new BigDecimal[originalData.length];
        System.arraycopy(smoothedKSTValues, 0, result, calculateMaxSumIndex() - 1, smoothedKSTValues.length);
        return result;
    }

    private BigDecimal[] calculateMovingAverageValues(BigDecimal[] values, int period) {
        return IndicatorResultExtractor.extract(calculateMovingAverage(values, period));
    }

    private MAResult[] calculateMovingAverage(BigDecimal[] values, int period) {
        return MovingAverageFactory.create(buildSMARequest(values, period)).getResult();
    }

    private MARequest buildSMARequest(BigDecimal[] values, int period) {
        return MARequest.builder()
                .originalData(FakeTicksCreator.createWithCloseOnly(values))
                .indicatorType(SIMPLE_MOVING_AVERAGE)
                .period(period)
                .priceType(CLOSE)
                .build();
    }

    private int calculateMaxSumIndex() {
        return max(asList(lightestROCPeriod + lightestSMAPeriod, lightROCPeriod + lightSMAPeriod,
                heavyROCPeriod + heavySMAPeriod, heaviestROCPeriod + heaviestSMAPeriod));
    }

    private void buildKnowSureThingResult(BigDecimal[] knowSureThingValues, BigDecimal[] signalLineValues) {
        result = IntStream.range(0, originalData.length)
                .mapToObj(idx -> new KSTResult(originalData[idx].getTickTime(), knowSureThingValues[idx], signalLineValues[idx]))
                .toArray(KSTResult[]::new);
    }

}
