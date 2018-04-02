package pro.crypto.indicators.co;

import pro.crypto.exception.WrongIncomingParametersException;
import pro.crypto.helper.FakeTicksCreator;
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
import java.util.stream.Stream;

import static java.lang.String.format;
import static java.util.Objects.isNull;
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
        ADLResult[] adlResult = countADL();
        MAResult[] slowEma = countSlowEmaForAdl(adlResult);
        MAResult[] fastEma = countFastEmaForAdl(adlResult);
        countChaikinOscillatorValues(slowEma, fastEma);
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
        checkPeriods();
    }

    private void checkPeriods() {
        checkPeriod(slowPeriod);
        checkPeriod(fastPeriod);
        checkIncomingDataLength();
    }

    private void checkIncomingDataLength() {
        if (originalData.length <= fastPeriod) {
            throw new WrongIncomingParametersException(format("Incoming tick data is not enough " +
                            "{indicator: {%s}, tickLength: {%d}, fastPeriod: {%d}}",
                    getType().toString(), originalData.length, fastPeriod));
        }
    }

    private ADLResult[] countADL() {
        return new AccumulationDistributionLine(new ADLRequest(originalData)).getResult();
    }

    private MAResult[] countSlowEmaForAdl(ADLResult[] adlResult) {
        return MovingAverageFactory.createMovingAverage(MARequest.builder()
                .indicatorType(EXPONENTIAL_MOVING_AVERAGE)
                .originalData(createFakeTicks(adlResult))
                .priceType(CLOSE)
                .period(slowPeriod)
                .build())
                .getResult();
    }

    private MAResult[] countFastEmaForAdl(ADLResult[] adlResult) {
        return MovingAverageFactory.createMovingAverage(MARequest.builder()
                .indicatorType(EXPONENTIAL_MOVING_AVERAGE)
                .originalData(createFakeTicks(adlResult))
                .priceType(CLOSE)
                .period(fastPeriod)
                .build())
                .getResult();
    }

    private Tick[] createFakeTicks(ADLResult[] adlResult) {
        return FakeTicksCreator.createFakeTicksWithCloseOnly(Stream.of(adlResult)
                .map(ADLResult::getIndicatorValue)
                .toArray(BigDecimal[]::new)
        );
    }

    private void countChaikinOscillatorValues(MAResult[] slowEma, MAResult[] fastEma) {
        for (int i = 0; i < result.length; i++) {
            result[i] = countChaikinOscillatorValue(slowEma, fastEma, i);
        }
    }

    private COResult countChaikinOscillatorValue(MAResult[] slowEma, MAResult[] fastEma, int currentIndex) {
        return new COResult(originalData[currentIndex].getTickTime(),
                countDifference(slowEma[currentIndex].getIndicatorValue(), fastEma[currentIndex].getIndicatorValue()));
    }

    private BigDecimal countDifference(BigDecimal slowEmaValue, BigDecimal fastEmaValue) {
        return isNull(slowEmaValue) || isNull(fastEmaValue)
                ? null
                : scaleAndRound(slowEmaValue.subtract(fastEmaValue));
    }

}
