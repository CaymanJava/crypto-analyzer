package pro.crypto.indicator.hv;

import pro.crypto.helper.FakeTicksCreator;
import pro.crypto.helper.IndicatorResultExtractor;
import pro.crypto.helper.MathHelper;
import pro.crypto.indicator.stdev.StDevRequest;
import pro.crypto.indicator.stdev.StandardDeviation;
import pro.crypto.model.Indicator;
import pro.crypto.model.indicator.IndicatorType;
import pro.crypto.model.tick.PriceType;
import pro.crypto.model.tick.Tick;
import pro.crypto.request.IndicatorRequest;
import pro.crypto.response.SimpleIndicatorResult;

import java.math.BigDecimal;
import java.util.stream.IntStream;

import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;
import static pro.crypto.model.indicator.IndicatorType.HISTORICAL_VOLATILITY;
import static pro.crypto.model.indicator.IndicatorType.SIMPLE_MOVING_AVERAGE;
import static pro.crypto.model.tick.PriceType.CLOSE;

/**
 * This indicator is only for history volatility calculation. It doesn't have any analyzers or strategies
 */
public class HistoricalVolatility implements Indicator<HVResult> {

    private final Tick[] originalData;
    private final int period;
    private final PriceType priceType;
    private final int daysPerYear;
    private final int standardDeviations;

    private HVResult[] result;

    public HistoricalVolatility(IndicatorRequest creationRequest) {
        HVRequest request = (HVRequest) creationRequest;
        this.originalData = request.getOriginalData();
        this.period = request.getPeriod();
        this.priceType = request.getPriceType();
        this.daysPerYear = request.getDaysPerYear();
        this.standardDeviations = request.getStandardDeviations();
        checkIncomingData();
    }

    @Override
    public IndicatorType getType() {
        return HISTORICAL_VOLATILITY;
    }

    @Override
    public void calculate() {
        result = new HVResult[originalData.length];
        BigDecimal[] continuouslyCompoundedReturns = calculateContinuouslyCompoundedReturns();
        BigDecimal[] oneDayVolatilityValues = calculateOneDayVolatilityValues(continuouslyCompoundedReturns);
        calculateHistoricalVolatility(oneDayVolatilityValues);
    }

    @Override
    public HVResult[] getResult() {
        if (isNull(result)) {
            calculate();
        }
        return result;
    }

    private void checkIncomingData() {
        checkOriginalData(originalData);
        checkOriginalDataSize(originalData, period + standardDeviations);
        checkPeriod(period);
        checkPeriod(standardDeviations);
        checkPeriod(daysPerYear);
        checkPriceType(priceType);
    }

    private BigDecimal[] calculateContinuouslyCompoundedReturns() {
        return IntStream.range(0, originalData.length)
                .mapToObj(this::calculateContinuouslyCompoundedReturn)
                .toArray(BigDecimal[]::new);
    }

    // ln(C(i) - C(i-stDev))
    private BigDecimal calculateContinuouslyCompoundedReturn(int index) {
        return index >= standardDeviations
                ? calculateContinuouslyCompoundedReturnValue(index)
                : null;
    }

    // ln(C(i) - C(i-stDev))
    private BigDecimal calculateContinuouslyCompoundedReturnValue(int index) {
        return MathHelper.ln(MathHelper.divide(
                originalData[index].getPriceByType(priceType),
                originalData[index - standardDeviations].getPriceByType(priceType)));
    }

    private BigDecimal[] calculateOneDayVolatilityValues(BigDecimal[] continuouslyCompoundedReturns) {
        BigDecimal[] standardDeviationValues = calculateStandardDeviationValues(continuouslyCompoundedReturns);
        BigDecimal[] result = new BigDecimal[originalData.length];
        System.arraycopy(standardDeviationValues, 0, result, standardDeviations, standardDeviationValues.length);
        return result;
    }

    private BigDecimal[] calculateStandardDeviationValues(BigDecimal[] continuouslyCompoundedReturns) {
        return IndicatorResultExtractor.extractIndicatorValues(calculateStandardDeviation(continuouslyCompoundedReturns));
    }

    private SimpleIndicatorResult[] calculateStandardDeviation(BigDecimal[] continuouslyCompoundedReturns) {
        return new StandardDeviation(buildStDevRequest(continuouslyCompoundedReturns)).getResult();
    }

    private IndicatorRequest buildStDevRequest(BigDecimal[] continuouslyCompoundedReturns) {
        return StDevRequest.builder()
                .originalData(FakeTicksCreator.createWithCloseOnly(continuouslyCompoundedReturns))
                .period(period)
                .movingAverageType(SIMPLE_MOVING_AVERAGE)
                .priceType(CLOSE)
                .build();
    }

    private void calculateHistoricalVolatility(BigDecimal[] oneDayVolatilityValues) {
        BigDecimal annualizingFactor = calculateAnnualizingFactor();
        IntStream.range(0, result.length)
                .forEach(idx -> result[idx] = new HVResult(originalData[idx].getTickTime(),
                        calculateHistoricalVolatility(oneDayVolatilityValues[idx], annualizingFactor)
                ));
    }

    private BigDecimal calculateAnnualizingFactor() {
        return new BigDecimal(100).multiply(calculateSqrtFromDaysPerYear());
    }

    private BigDecimal calculateSqrtFromDaysPerYear() {
        return MathHelper.sqrt(new BigDecimal(daysPerYear));
    }

    private BigDecimal calculateHistoricalVolatility(BigDecimal oneDayVolatilityValue, BigDecimal annualizingFactor) {
        return nonNull(oneDayVolatilityValue)
                ? MathHelper.scaleAndRound(oneDayVolatilityValue.multiply(annualizingFactor))
                : null;
    }

}
