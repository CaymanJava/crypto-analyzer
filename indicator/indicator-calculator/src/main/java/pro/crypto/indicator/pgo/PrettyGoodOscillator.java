package pro.crypto.indicator.pgo;

import pro.crypto.helper.FakeTicksCreator;
import pro.crypto.helper.IndicatorResultExtractor;
import pro.crypto.helper.MathHelper;
import pro.crypto.indicator.atr.ATRRequest;
import pro.crypto.indicator.atr.AverageTrueRange;
import pro.crypto.indicator.ma.MARequest;
import pro.crypto.indicator.ma.MovingAverageFactory;
import pro.crypto.model.Indicator;
import pro.crypto.model.indicator.IndicatorType;
import pro.crypto.model.tick.Tick;
import pro.crypto.request.IndicatorRequest;
import pro.crypto.response.SimpleIndicatorResult;

import java.math.BigDecimal;
import java.util.stream.IntStream;

import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;
import static pro.crypto.model.indicator.IndicatorType.EXPONENTIAL_MOVING_AVERAGE;
import static pro.crypto.model.indicator.IndicatorType.PRETTY_GOOD_OSCILLATOR;
import static pro.crypto.model.indicator.IndicatorType.SIMPLE_MOVING_AVERAGE;
import static pro.crypto.model.tick.PriceType.CLOSE;

public class PrettyGoodOscillator implements Indicator<PGOResult> {

    private final Tick[] originalData;
    private final int period;

    private PGOResult[] result;

    public PrettyGoodOscillator(IndicatorRequest creationRequest) {
        PGORequest request = (PGORequest) creationRequest;
        this.originalData = request.getOriginalData();
        this.period = request.getPeriod();
        checkIncomingData();
    }

    @Override
    public IndicatorType getType() {
        return PRETTY_GOOD_OSCILLATOR;
    }

    @Override
    public void calculate() {
        BigDecimal[] atrValues = calculateAverageTrueRangeValues();
        BigDecimal[] smoothedAtrValues = smoothATRValues(atrValues);
        BigDecimal[] smaValues = calculateSimpleMovingAverageValues();
        calculatePrettyGoodOscillator(smoothedAtrValues, smaValues);
    }

    @Override
    public PGOResult[] getResult() {
        if (isNull(result)) {
            calculate();
        }
        return result;
    }

    private void checkIncomingData() {
        checkOriginalData(originalData);
        checkOriginalDataSize(originalData, period * 2);
        checkPeriod(period);
    }

    private BigDecimal[] calculateAverageTrueRangeValues() {
        return IndicatorResultExtractor.extractIndicatorValues(calculateAverageTrueRange());
    }

    private SimpleIndicatorResult[] calculateAverageTrueRange() {
        return new AverageTrueRange(buildATRRequest()).getResult();
    }

    private IndicatorRequest buildATRRequest() {
        return ATRRequest.builder()
                .originalData(originalData)
                .period(period)
                .build();
    }

    private BigDecimal[] smoothATRValues(BigDecimal[] atrValues) {
        BigDecimal[] emaValues = calculateMovingAverageValues(FakeTicksCreator.createWithCloseOnly(atrValues), EXPONENTIAL_MOVING_AVERAGE);
        BigDecimal[] result = new BigDecimal[originalData.length];
        System.arraycopy(emaValues, 0, result, period - 1, emaValues.length);
        return result;
    }

    private BigDecimal[] calculateSimpleMovingAverageValues() {
        return calculateMovingAverageValues(originalData, SIMPLE_MOVING_AVERAGE);
    }

    private BigDecimal[] calculateMovingAverageValues(Tick[] data, IndicatorType movingAverageType) {
        return IndicatorResultExtractor.extractIndicatorValues(calculateMovingAverage(data, movingAverageType));
    }

    private SimpleIndicatorResult[] calculateMovingAverage(Tick[] data, IndicatorType movingAverageType) {
        return MovingAverageFactory.create(buildMARequest(data, movingAverageType)).getResult();
    }

    private IndicatorRequest buildMARequest(Tick[] data, IndicatorType movingAverageType) {
        return MARequest.builder()
                .originalData(data)
                .priceType(CLOSE)
                .period(period)
                .indicatorType(movingAverageType)
                .build();
    }

    private void calculatePrettyGoodOscillator(BigDecimal[] smoothedAtrValues, BigDecimal[] smaValues) {
        result = IntStream.range(0, originalData.length)
                .mapToObj(idx -> buildPGOResult(smoothedAtrValues, smaValues, idx))
                .toArray(PGOResult[]::new);
    }

    private PGOResult buildPGOResult(BigDecimal[] smoothedAtrValues, BigDecimal[] smaValues, int currentIndex) {
        return new PGOResult(
                originalData[currentIndex].getTickTime(),
                calculatePrettyGoodOscillator(smoothedAtrValues[currentIndex], smaValues[currentIndex], originalData[currentIndex].getClose())
        );
    }

    private BigDecimal calculatePrettyGoodOscillator(BigDecimal smoothedAtrValue, BigDecimal smaValue, BigDecimal closePrice) {
        return nonNull(smoothedAtrValue) && nonNull(smaValue)
                ? calculatePrettyGoodOscillatorValue(smoothedAtrValue, smaValue, closePrice)
                : null;
    }

    private BigDecimal calculatePrettyGoodOscillatorValue(BigDecimal smoothedAtrValue, BigDecimal smaValue, BigDecimal closePrice) {
        return MathHelper.divide(closePrice.subtract(smaValue), smoothedAtrValue);
    }

}
