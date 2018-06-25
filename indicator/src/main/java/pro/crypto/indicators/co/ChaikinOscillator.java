package pro.crypto.indicators.co;

import pro.crypto.helper.FakeTicksCreator;
import pro.crypto.helper.IndicatorResultExtractor;
import pro.crypto.indicators.adl.AccumulationDistributionLine;
import pro.crypto.indicators.ma.MovingAverageFactory;
import pro.crypto.model.Indicator;
import pro.crypto.model.IndicatorType;
import pro.crypto.model.request.ADLRequest;
import pro.crypto.model.request.CORequest;
import pro.crypto.model.request.MARequest;
import pro.crypto.model.result.ADLResult;
import pro.crypto.model.result.COResult;
import pro.crypto.model.result.MAResult;
import pro.crypto.model.tick.Tick;

import java.math.BigDecimal;
import java.util.stream.IntStream;

import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;
import static pro.crypto.helper.MathHelper.scaleAndRound;
import static pro.crypto.model.IndicatorType.CHAIKIN_OSCILLATOR;
import static pro.crypto.model.IndicatorType.EXPONENTIAL_MOVING_AVERAGE;
import static pro.crypto.model.tick.PriceType.CLOSE;

public class ChaikinOscillator implements Indicator<COResult> {

    private final Tick[] originalData;
    private final int slowPeriod;
    private final int fastPeriod;

    private COResult[] result;

    public ChaikinOscillator(CORequest request) {
        this.originalData = request.getOriginalData();
        this.slowPeriod = request.getSlowPeriod();
        this.fastPeriod = request.getFastPeriod();
        checkIncomingData();
    }

    @Override
    public IndicatorType getType() {
        return CHAIKIN_OSCILLATOR;
    }

    @Override
    public void calculate() {
        result = new COResult[originalData.length];
        ADLResult[] adlResult = calculateADL();
        MAResult[] slowEma = calculateSlowEmaForAdl(adlResult);
        MAResult[] fastEma = calculateFastEmaForAdl(adlResult);
        calculateChaikinOscillatorValues(slowEma, fastEma);
    }

    @Override
    public COResult[] getResult() {
        if (isNull(result)) {
            calculate();
        }
        return result;
    }

    private void checkIncomingData() {
        checkOriginalData(originalData);
        checkOriginalDataSize(originalData, fastPeriod);
        checkPeriod(slowPeriod);
        checkPeriod(fastPeriod);
    }

    private ADLResult[] calculateADL() {
        return new AccumulationDistributionLine(new ADLRequest(originalData)).getResult();
    }

    private MAResult[] calculateSlowEmaForAdl(ADLResult[] adlResult) {
        return MovingAverageFactory.create(MARequest.builder()
                .indicatorType(EXPONENTIAL_MOVING_AVERAGE)
                .originalData(createFakeTicks(adlResult))
                .priceType(CLOSE)
                .period(slowPeriod)
                .build())
                .getResult();
    }

    private MAResult[] calculateFastEmaForAdl(ADLResult[] adlResult) {
        return MovingAverageFactory.create(MARequest.builder()
                .indicatorType(EXPONENTIAL_MOVING_AVERAGE)
                .originalData(createFakeTicks(adlResult))
                .priceType(CLOSE)
                .period(fastPeriod)
                .build())
                .getResult();
    }

    private Tick[] createFakeTicks(ADLResult[] adlResult) {
        return FakeTicksCreator.createWithCloseOnly(IndicatorResultExtractor.extract(adlResult));
    }

    private void calculateChaikinOscillatorValues(MAResult[] slowEma, MAResult[] fastEma) {
        IntStream.range(0, result.length)
                .forEach(idx -> result[idx] = calculateChaikinOscillatorValue(slowEma, fastEma, idx));
    }

    private COResult calculateChaikinOscillatorValue(MAResult[] slowEma, MAResult[] fastEma, int currentIndex) {
        return new COResult(originalData[currentIndex].getTickTime(),
                calculateDifference(slowEma[currentIndex].getIndicatorValue(), fastEma[currentIndex].getIndicatorValue()));
    }

    private BigDecimal calculateDifference(BigDecimal slowEmaValue, BigDecimal fastEmaValue) {
        return nonNull(slowEmaValue) && nonNull(fastEmaValue)
                ? scaleAndRound(slowEmaValue.subtract(fastEmaValue))
                : null;
    }

}
