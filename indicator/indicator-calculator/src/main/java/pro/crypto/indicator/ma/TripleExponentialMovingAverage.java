package pro.crypto.indicator.ma;

import pro.crypto.helper.FakeTicksCreator;
import pro.crypto.helper.IndicatorResultExtractor;
import pro.crypto.model.indicator.IndicatorType;
import pro.crypto.model.tick.PriceType;
import pro.crypto.model.tick.Tick;
import pro.crypto.request.IndicatorRequest;

import java.math.BigDecimal;
import java.util.stream.IntStream;

import static java.util.Objects.nonNull;
import static pro.crypto.model.indicator.IndicatorType.DOUBLE_EXPONENTIAL_MOVING_AVERAGE;
import static pro.crypto.model.indicator.IndicatorType.EXPONENTIAL_MOVING_AVERAGE;
import static pro.crypto.model.indicator.IndicatorType.TRIPLE_EXPONENTIAL_MOVING_AVERAGE;
import static pro.crypto.model.tick.PriceType.CLOSE;

public class TripleExponentialMovingAverage extends MovingAverage {

    private final Tick[] originalData;
    private final int period;
    private final BigDecimal alphaCoefficient;

    TripleExponentialMovingAverage(Tick[] originalData, int period, PriceType priceType, BigDecimal alphaCoefficient) {
        this.originalData = originalData;
        this.period = period;
        this.priceType = priceType;
        this.alphaCoefficient = alphaCoefficient;
        checkIncomingData();
    }

    @Override
    public IndicatorType getType() {
        return TRIPLE_EXPONENTIAL_MOVING_AVERAGE;
    }

    @Override
    public void calculate() {
        BigDecimal[] demaValues = calculateDEMAValues();
        BigDecimal[] priceDeviationErrors = calculatePriceDeviationErrors(demaValues);
        BigDecimal[] emaPriceDeviationErrors = calculateEMAPriceDeviationErrors(priceDeviationErrors);
        BigDecimal[] temaValues = calculateTEMAValues(demaValues, emaPriceDeviationErrors);
        buildTripleExponentialMovingAverageResult(temaValues);
    }

    private void checkIncomingData() {
        checkIncomingData(originalData, period, priceType);
        checkOriginalDataSize(originalData, 3 * period - 2);
    }

    private BigDecimal[] calculateDEMAValues() {
        return IndicatorResultExtractor.extractIndicatorValues(calculateDEMA());
    }

    private MAResult[] calculateDEMA() {
        return MovingAverageFactory.create(buildDEMARequest()).getResult();
    }

    private IndicatorRequest buildDEMARequest() {
        return MARequest.builder()
                .originalData(originalData)
                .indicatorType(DOUBLE_EXPONENTIAL_MOVING_AVERAGE)
                .period(period)
                .priceType(priceType)
                .alphaCoefficient(alphaCoefficient)
                .build();
    }

    private BigDecimal[] calculatePriceDeviationErrors(BigDecimal[] demaValues) {
        return IntStream.range(0, originalData.length)
                .mapToObj(idx -> calculatePriceDeviationError(demaValues[idx], idx))
                .toArray(BigDecimal[]::new);
    }

    private BigDecimal calculatePriceDeviationError(BigDecimal demaValue, int currentIndex) {
        return nonNull(demaValue)
                ? originalData[currentIndex].getClose().subtract(demaValue)
                : null;
    }

    private BigDecimal[] calculateEMAPriceDeviationErrors(BigDecimal[] priceDeviationErrors) {
        BigDecimal[] emaPriceDeviationErrors = calculateEMAPriceDeviationErrorsValues(priceDeviationErrors);
        BigDecimal[] result = new BigDecimal[originalData.length];
        System.arraycopy(emaPriceDeviationErrors, 0, result, period * 2 - 2, emaPriceDeviationErrors.length);
        return result;
    }

    private BigDecimal[] calculateEMAPriceDeviationErrorsValues(BigDecimal[] priceDeviationErrors) {
        return IndicatorResultExtractor.extractIndicatorValues(calculateEMA(priceDeviationErrors));
    }

    private MAResult[] calculateEMA(BigDecimal[] priceDeviationErrors) {
        return MovingAverageFactory.create(buildEMARequest(priceDeviationErrors)).getResult();
    }

    private IndicatorRequest buildEMARequest(BigDecimal[] priceDeviationErrors) {
        return MARequest.builder()
                .originalData(FakeTicksCreator.createWithCloseOnly(priceDeviationErrors))
                .indicatorType(EXPONENTIAL_MOVING_AVERAGE)
                .period(period)
                .alphaCoefficient(alphaCoefficient)
                .priceType(CLOSE)
                .build();
    }

    private BigDecimal[] calculateTEMAValues(BigDecimal[] demaValues, BigDecimal[] emaPriceDeviationErrors) {
        return IntStream.range(0, originalData.length)
                .mapToObj(idx -> calculateTEMAValue(demaValues[idx], emaPriceDeviationErrors[idx]))
                .toArray(BigDecimal[]::new);
    }

    private BigDecimal calculateTEMAValue(BigDecimal demaValue, BigDecimal emaPriceDeviationError) {
        return nonNull(demaValue) && nonNull(emaPriceDeviationError)
                ? demaValue.add(emaPriceDeviationError)
                : null;
    }

    private void buildTripleExponentialMovingAverageResult(BigDecimal[] temaValues) {
        result = IntStream.range(0, originalData.length)
                .mapToObj(idx -> new MAResult(originalData[idx].getTickTime(), temaValues[idx]))
                .toArray(MAResult[]::new);
    }

}
