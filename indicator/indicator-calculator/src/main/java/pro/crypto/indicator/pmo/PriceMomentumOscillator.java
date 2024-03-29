package pro.crypto.indicator.pmo;

import pro.crypto.helper.FakeTicksCreator;
import pro.crypto.helper.IndicatorResultExtractor;
import pro.crypto.helper.MathHelper;
import pro.crypto.indicator.ma.MARequest;
import pro.crypto.indicator.ma.MovingAverageFactory;
import pro.crypto.indicator.roc.ROCRequest;
import pro.crypto.indicator.roc.RateOfChange;
import pro.crypto.model.Indicator;
import pro.crypto.model.indicator.IndicatorType;
import pro.crypto.model.tick.PriceType;
import pro.crypto.model.tick.Tick;
import pro.crypto.request.IndicatorRequest;
import pro.crypto.response.SimpleIndicatorResult;

import java.math.BigDecimal;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;
import static pro.crypto.model.indicator.IndicatorType.EXPONENTIAL_MOVING_AVERAGE;
import static pro.crypto.model.indicator.IndicatorType.PRICE_MOMENTUM_OSCILLATOR;
import static pro.crypto.model.tick.PriceType.CLOSE;

public class PriceMomentumOscillator implements Indicator<PMOResult> {

    private final static int ROC_PERIOD = 1;

    private final Tick[] originalData;
    private final PriceType priceType;
    private final int smoothingPeriod;
    private final int doubleSmoothingPeriod;
    private final int signalPeriod;

    private PMOResult[] result;

    public PriceMomentumOscillator(IndicatorRequest creationRequest) {
        PMORequest request = (PMORequest) creationRequest;
        this.originalData = request.getOriginalData();
        this.priceType = request.getPriceType();
        this.smoothingPeriod = request.getSmoothingPeriod();
        this.doubleSmoothingPeriod = request.getDoubleSmoothingPeriod();
        this.signalPeriod = request.getSignalPeriod();
        checkIncomingData();
    }

    @Override
    public IndicatorType getType() {
        return PRICE_MOMENTUM_OSCILLATOR;
    }

    @Override
    public void calculate() {
        result = new PMOResult[originalData.length];
        BigDecimal[] priceMomentumOscillatorValues = calculatePriceMomentumOscillatorValues();
        BigDecimal[] signalLineValues = calculateSignalLineValues(priceMomentumOscillatorValues);
        buildPriceMomentumOscillatorResult(priceMomentumOscillatorValues, signalLineValues);
    }

    @Override
    public PMOResult[] getResult() {
        if (isNull(result)) {
            calculate();
        }
        return result;
    }

    private void checkIncomingData() {
        checkPriceType(priceType);
        checkOriginalData(originalData);
        checkOriginalDataSize(originalData, smoothingPeriod + doubleSmoothingPeriod + signalPeriod);
        checkPeriod(smoothingPeriod);
        checkPeriod(doubleSmoothingPeriod);
        checkPeriod(signalPeriod);
    }

    private BigDecimal[] calculatePriceMomentumOscillatorValues() {
        BigDecimal[] rocValues = calculateRateOfChangeValues();
        BigDecimal[] smoothedRocValues = smoothRateOfChangeValues(rocValues);
        return calculatePriceMomentumOscillatorValues(smoothedRocValues);
    }

    private BigDecimal[] calculateRateOfChangeValues() {
        return IndicatorResultExtractor.extractIndicatorValues(calculateRateOfChange());
    }

    private SimpleIndicatorResult[] calculateRateOfChange() {
        return new RateOfChange(buildROCRequest()).getResult();
    }

    private IndicatorRequest buildROCRequest() {
        return ROCRequest.builder()
                .originalData(originalData)
                .priceType(priceType)
                .period(ROC_PERIOD)
                .build();
    }

    private BigDecimal[] smoothRateOfChangeValues(BigDecimal[] rocValues) {
        BigDecimal[] smoothedValues = smoothedRateOfChange(rocValues);
        BigDecimal[] result = new BigDecimal[originalData.length];
        System.arraycopy(smoothedValues, 0, result, ROC_PERIOD, smoothedValues.length);
        return result;
    }

    private BigDecimal[] smoothedRateOfChange(BigDecimal[] rocValues) {
        return Stream.of(IndicatorResultExtractor.extractIndicatorValues(
                calculateExponentialMovingAverage(rocValues, calculateAlphaCoefficient(smoothingPeriod), smoothingPeriod)))
                .map(this::multiplyByTen)
                .toArray(BigDecimal[]::new);
    }

    private BigDecimal multiplyByTen(BigDecimal value) {
        return nonNull(value) ? value.multiply(new BigDecimal(10)) : null;
    }

    private BigDecimal[] calculatePriceMomentumOscillatorValues(BigDecimal[] smoothedRocValues) {
        BigDecimal[] pmoValues = calculatePriceMomentumOscillator(smoothedRocValues);
        BigDecimal[] result = new BigDecimal[originalData.length];
        System.arraycopy(pmoValues, 0, result, smoothingPeriod, pmoValues.length);
        return result;
    }

    private BigDecimal[] calculatePriceMomentumOscillator(BigDecimal[] smoothedRocValues) {
        return IndicatorResultExtractor.extractIndicatorValues(
                calculateExponentialMovingAverage(smoothedRocValues, calculateAlphaCoefficient(doubleSmoothingPeriod), doubleSmoothingPeriod));
    }

    private BigDecimal[] calculateSignalLineValues(BigDecimal[] priceMomentumOscillatorValues) {
        BigDecimal[] signalLineValues = calculateSignalLine(priceMomentumOscillatorValues);
        BigDecimal[] result = new BigDecimal[originalData.length];
        System.arraycopy(signalLineValues, 0, result, smoothingPeriod + doubleSmoothingPeriod - 1, signalLineValues.length);
        return result;
    }

    private BigDecimal[] calculateSignalLine(BigDecimal[] priceMomentumOscillatorValues) {
        return IndicatorResultExtractor.extractIndicatorValues(
                calculateExponentialMovingAverage(priceMomentumOscillatorValues, null, signalPeriod));
    }

    private BigDecimal calculateAlphaCoefficient(int period) {
        return MathHelper.divide(new BigDecimal(2), new BigDecimal(period));
    }

    private SimpleIndicatorResult[] calculateExponentialMovingAverage(BigDecimal[] prices, BigDecimal alphaCoefficient, int period) {
        return MovingAverageFactory.create(buildEMARequest(prices, alphaCoefficient, period)).getResult();
    }

    private IndicatorRequest buildEMARequest(BigDecimal[] values, BigDecimal alphaCoefficient, int period) {
        return MARequest.builder()
                .originalData(FakeTicksCreator.createWithCloseOnly(values))
                .alphaCoefficient(alphaCoefficient)
                .indicatorType(EXPONENTIAL_MOVING_AVERAGE)
                .priceType(CLOSE)
                .period(period)
                .build();
    }

    private void buildPriceMomentumOscillatorResult(BigDecimal[] priceMomentumOscillatorValues, BigDecimal[] signalLineValues) {
        IntStream.range(0, result.length)
                .forEach(currentIndex -> buildPriceMomentumOscillatorResult(currentIndex, priceMomentumOscillatorValues[currentIndex], signalLineValues[currentIndex]));
    }

    private void buildPriceMomentumOscillatorResult(int currentIndex, BigDecimal priceMomentumOscillatorValue, BigDecimal signalLineValue) {
        result[currentIndex] = new PMOResult(originalData[currentIndex].getTickTime(), priceMomentumOscillatorValue, signalLineValue);
    }

}
