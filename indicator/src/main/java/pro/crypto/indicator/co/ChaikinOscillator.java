package pro.crypto.indicator.co;

import pro.crypto.helper.FakeTicksCreator;
import pro.crypto.helper.IndicatorResultExtractor;
import pro.crypto.indicator.adl.ADLRequest;
import pro.crypto.indicator.adl.AccumulationDistributionLine;
import pro.crypto.indicator.ma.MARequest;
import pro.crypto.indicator.ma.MovingAverageFactory;
import pro.crypto.model.Indicator;
import pro.crypto.model.IndicatorRequest;
import pro.crypto.model.IndicatorType;
import pro.crypto.model.SimpleIndicatorResult;
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

    public ChaikinOscillator(IndicatorRequest creationRequest) {
        CORequest request = (CORequest) creationRequest;
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
        SimpleIndicatorResult[] adlResult = calculateADL();
        SimpleIndicatorResult[] slowEma = calculateEmaForAdl(adlResult, slowPeriod);
        SimpleIndicatorResult[] fastEma = calculateEmaForAdl(adlResult, fastPeriod);
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

    private SimpleIndicatorResult[] calculateADL() {
        return new AccumulationDistributionLine(new ADLRequest(originalData)).getResult();
    }

    private SimpleIndicatorResult[] calculateEmaForAdl(SimpleIndicatorResult[] adlResult, int period) {
        return MovingAverageFactory.create(MARequest.builder()
                .indicatorType(EXPONENTIAL_MOVING_AVERAGE)
                .originalData(createFakeTicks(adlResult))
                .priceType(CLOSE)
                .period(period)
                .build())
                .getResult();
    }

    private Tick[] createFakeTicks(SimpleIndicatorResult[] adlResult) {
        return FakeTicksCreator.createWithCloseOnly(IndicatorResultExtractor.extractIndicatorValues(adlResult));
    }

    private void calculateChaikinOscillatorValues(SimpleIndicatorResult[] slowEma, SimpleIndicatorResult[] fastEma) {
        IntStream.range(0, result.length)
                .forEach(idx -> result[idx] = calculateChaikinOscillatorValue(slowEma, fastEma, idx));
    }

    private COResult calculateChaikinOscillatorValue(SimpleIndicatorResult[] slowEma, SimpleIndicatorResult[] fastEma, int currentIndex) {
        return new COResult(originalData[currentIndex].getTickTime(),
                calculateDifference(slowEma[currentIndex].getIndicatorValue(), fastEma[currentIndex].getIndicatorValue()));
    }

    private BigDecimal calculateDifference(BigDecimal slowEmaValue, BigDecimal fastEmaValue) {
        return nonNull(slowEmaValue) && nonNull(fastEmaValue)
                ? scaleAndRound(slowEmaValue.subtract(fastEmaValue))
                : null;
    }

}
