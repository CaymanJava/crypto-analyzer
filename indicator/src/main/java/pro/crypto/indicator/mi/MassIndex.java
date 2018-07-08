package pro.crypto.indicator.mi;

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
import java.util.Arrays;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;
import static pro.crypto.model.IndicatorType.EXPONENTIAL_MOVING_AVERAGE;
import static pro.crypto.model.IndicatorType.MASS_INDEX;
import static pro.crypto.model.tick.PriceType.CLOSE;

public class MassIndex implements Indicator<MIResult> {

    private final static int EXPONENTIAL_MOVING_AVERAGE_PERIOD = 9;

    private final Tick[] originalData;
    private final int period;

    private MIResult[] result;

    public MassIndex(IndicatorRequest creationRequest) {
        MIRequest request = (MIRequest) creationRequest;
        this.originalData = request.getOriginalData();
        this.period = request.getPeriod();
        checkIncomingData();
    }

    @Override
    public IndicatorType getType() {
        return MASS_INDEX;
    }

    @Override
    public void calculate() {
        result = new MIResult[originalData.length];
        BigDecimal[] highLowDifferential = calculateHighLowDifferential();
        BigDecimal[] singleMovingAverage = calculateSingleMovingAverage(highLowDifferential);
        BigDecimal[] doubleMovingAverage = calculateDoubleMovingAverage(singleMovingAverage);
        calculateMassIndex(singleMovingAverage, doubleMovingAverage);
    }

    @Override
    public MIResult[] getResult() {
        if (isNull(result)) {
            calculate();
        }
        return result;
    }

    private void checkIncomingData() {
        checkOriginalData(originalData);
        checkOriginalDataSize(originalData, EXPONENTIAL_MOVING_AVERAGE_PERIOD * 2 + period);
        checkPeriod(period);
    }

    private BigDecimal[] calculateHighLowDifferential() {
        return Stream.of(originalData)
                .map(this::calculateHighLowDifferential)
                .toArray(BigDecimal[]::new);
    }

    private BigDecimal calculateHighLowDifferential(Tick tick) {
        return tick.getHigh().subtract(tick.getLow());
    }

    private BigDecimal[] calculateSingleMovingAverage(BigDecimal[] highLowDifferential) {
        return IndicatorResultExtractor.extract(calculateExponentialMovingAverage(highLowDifferential));
    }

    private BigDecimal[] calculateDoubleMovingAverage(BigDecimal[] singleMovingAverage) {
        BigDecimal[] doubleEMA = IndicatorResultExtractor.extract(calculateExponentialMovingAverage(singleMovingAverage));
        BigDecimal[] result = new BigDecimal[originalData.length];
        System.arraycopy(doubleEMA, 0, result, EXPONENTIAL_MOVING_AVERAGE_PERIOD - 1, doubleEMA.length);
        return result;
    }

    private SimpleIndicatorResult[] calculateExponentialMovingAverage(BigDecimal[] values) {
        return MovingAverageFactory.create(buildMARequest(values)).getResult();
    }

    private IndicatorRequest buildMARequest(BigDecimal[] values) {
        return MARequest.builder()
                .originalData(FakeTicksCreator.createWithCloseOnly(values))
                .period(EXPONENTIAL_MOVING_AVERAGE_PERIOD)
                .priceType(CLOSE)
                .indicatorType(EXPONENTIAL_MOVING_AVERAGE)
                .build();
    }

    private void calculateMassIndex(BigDecimal[] singleMovingAverage, BigDecimal[] doubleMovingAverage) {
        fillInInitialPositions();
        fillInRemainPositions(singleMovingAverage, doubleMovingAverage);
    }

    private void fillInInitialPositions() {
        IntStream.range(0, EXPONENTIAL_MOVING_AVERAGE_PERIOD * 2 + period - 3)
                .forEach(idx ->  result[idx] = new MIResult(originalData[idx].getTickTime(), null));
    }

    private void fillInRemainPositions(BigDecimal[] singleMovingAverage, BigDecimal[] doubleMovingAverage) {
        BigDecimal[] emaRatio = calculateEMARation(singleMovingAverage, doubleMovingAverage);
        IntStream.range(EXPONENTIAL_MOVING_AVERAGE_PERIOD * 2 + period - 3, result.length)
                .forEach(idx -> result[idx] = new MIResult(originalData[idx].getTickTime(), calculateMassIndex(emaRatio, idx)));
    }

    private BigDecimal[] calculateEMARation(BigDecimal[] singleMovingAverage, BigDecimal[] doubleMovingAverage) {
        return IntStream.range(0, originalData.length)
                .mapToObj(idx -> calculateEMARation(singleMovingAverage[idx], doubleMovingAverage[idx]))
                .toArray(BigDecimal[]::new);
    }

    private BigDecimal calculateEMARation(BigDecimal singleMovingAverageValue, BigDecimal doubleMovingAverageValue) {
        return nonNull(singleMovingAverageValue) && nonNull(doubleMovingAverageValue)
                ? MathHelper.divide(singleMovingAverageValue, doubleMovingAverageValue)
                : null;
    }

    private BigDecimal calculateMassIndex(BigDecimal[] emaRatio, int index) {
        return MathHelper.sum(Arrays.copyOfRange(emaRatio, index - period + 1, index + 1));
    }

}
