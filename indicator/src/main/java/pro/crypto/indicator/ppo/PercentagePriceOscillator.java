package pro.crypto.indicator.ppo;

import pro.crypto.helper.FakeTicksCreator;
import pro.crypto.helper.IndicatorResultExtractor;
import pro.crypto.helper.MathHelper;
import pro.crypto.indicator.ma.MARequest;
import pro.crypto.indicator.ma.MovingAverageFactory;
import pro.crypto.model.Indicator;
import pro.crypto.model.IndicatorRequest;
import pro.crypto.model.IndicatorType;
import pro.crypto.model.SimpleIndicatorResult;
import pro.crypto.model.tick.PriceType;
import pro.crypto.model.tick.Tick;

import java.math.BigDecimal;
import java.util.stream.IntStream;

import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;
import static pro.crypto.model.IndicatorType.PERCENTAGE_PRICE_OSCILLATOR;

public class PercentagePriceOscillator implements Indicator<PPOResult> {

    private final Tick[] originalData;
    private final IndicatorType movingAverageType;
    private final PriceType priceType;
    private final int slowPeriod;
    private final int fastPeriod;
    private final int signalPeriod;

    private PPOResult[] result;

    public PercentagePriceOscillator(IndicatorRequest creationRequest) {
        PPORequest request = (PPORequest) creationRequest;
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
        return PERCENTAGE_PRICE_OSCILLATOR;
    }

    @Override
    public void calculate() {
        BigDecimal[] ppoValues = calculatePercentagePriceOscillatorValues();
        BigDecimal[] signalLineValues = calculateSignalLineValues(ppoValues);
        BigDecimal[] barChartValues = calculateBarChartValues(ppoValues, signalLineValues);
        buildResult(ppoValues, signalLineValues, barChartValues);
    }

    @Override
    public PPOResult[] getResult() {
        if (isNull(result)) {
            calculate();
        }
        return result;
    }

    private void checkIncomingData() {
        checkOriginalData(originalData);
        checkOriginalDataSize(originalData, fastPeriod + signalPeriod);
        checkPriceType(priceType);
        checkMovingAverageType(movingAverageType);
        checkPeriods();
    }

    private void checkPeriods() {
        checkPeriod(slowPeriod);
        checkPeriod(fastPeriod);
        checkPeriod(signalPeriod);
    }

    private BigDecimal[] calculatePercentagePriceOscillatorValues() {
        BigDecimal[] slowMAValues = calculateMovingAverage(originalData, slowPeriod);
        BigDecimal[] fastMAValues = calculateMovingAverage(originalData, fastPeriod);
        return calculatePercentagePriceOscillatorValues(slowMAValues, fastMAValues);
    }

    private BigDecimal[] calculatePercentagePriceOscillatorValues(BigDecimal[] slowMAValues, BigDecimal[] fastMAValues) {
        return IntStream.range(0, originalData.length)
                .mapToObj(idx -> calculatePercentagePriceOscillator(slowMAValues[idx], fastMAValues[idx]))
                .toArray(BigDecimal[]::new);
    }

    private BigDecimal calculatePercentagePriceOscillator(BigDecimal slowMAValue, BigDecimal fastMAValue) {
        return nonNull(slowMAValue) && nonNull(fastMAValue)
                ? calculatePercentagePriceOscillatorValues(slowMAValue, fastMAValue)
                : null;
    }

    private BigDecimal calculatePercentagePriceOscillatorValues(BigDecimal slowMAValue, BigDecimal fastMAValue) {
        return MathHelper.divide(slowMAValue.subtract(fastMAValue)
                        .multiply(new BigDecimal(100)),
                fastMAValue);
    }

    private BigDecimal[] calculateSignalLineValues(BigDecimal[] ppoValues) {
        BigDecimal[] signalLineValues = calculateMovingAverage(FakeTicksCreator.createWithCloseOnly(ppoValues), signalPeriod);
        BigDecimal[] result = new BigDecimal[originalData.length];
        System.arraycopy(signalLineValues, 0, result, fastPeriod - 1, signalLineValues.length);
        return result;
    }

    private BigDecimal[] calculateMovingAverage(Tick[] data, int period) {
        return IndicatorResultExtractor.extract(calculateMovingAverageResult(data, period));
    }

    private SimpleIndicatorResult[] calculateMovingAverageResult(Tick[] data, int period) {
        return MovingAverageFactory.create(buildMARequest(data, period)).getResult();
    }

    private IndicatorRequest buildMARequest(Tick[] data, int period) {
        return MARequest.builder()
                .originalData(data)
                .period(period)
                .priceType(priceType)
                .indicatorType(movingAverageType)
                .build();
    }

    private BigDecimal[] calculateBarChartValues(BigDecimal[] ppoValues, BigDecimal[] signalLineValues) {
        return IntStream.range(0, originalData.length)
                .mapToObj(idx -> calculateBarChartValues(ppoValues[idx], signalLineValues[idx]))
                .toArray(BigDecimal[]::new);
    }

    private BigDecimal calculateBarChartValues(BigDecimal ppoValue, BigDecimal signalLineValue) {
        return nonNull(ppoValue) && nonNull(signalLineValue)
                ? MathHelper.scaleAndRound(ppoValue.subtract(signalLineValue))
                : null;
    }

    private void buildResult(BigDecimal[] ppoValues, BigDecimal[] signalLineValues, BigDecimal[] barChartValues) {
        result = IntStream.range(0, originalData.length)
                .mapToObj(idx -> new PPOResult(originalData[idx].getTickTime(), ppoValues[idx], signalLineValues[idx], barChartValues[idx]))
                .toArray(PPOResult[]::new);
    }

}
