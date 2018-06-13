package pro.crypto.indicators.hv;

import pro.crypto.helper.FakeTicksCreator;
import pro.crypto.helper.IndicatorResultExtractor;
import pro.crypto.helper.MathHelper;
import pro.crypto.indicators.stdev.StandardDeviation;
import pro.crypto.model.Indicator;
import pro.crypto.model.IndicatorType;
import pro.crypto.model.request.HVRequest;
import pro.crypto.model.request.StDevRequest;
import pro.crypto.model.result.HVResult;
import pro.crypto.model.result.StDevResult;
import pro.crypto.model.tick.PriceType;
import pro.crypto.model.tick.Tick;

import java.math.BigDecimal;

import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;
import static pro.crypto.model.IndicatorType.HISTORICAL_VOLATILITY;
import static pro.crypto.model.IndicatorType.SIMPLE_MOVING_AVERAGE;
import static pro.crypto.model.tick.PriceType.CLOSE;

public class HistoricalVolatility implements Indicator<HVResult> {

    private final Tick[] originalData;
    private final int period;
    private final PriceType priceType;
    private final int daysPerYear;
    private final int standardDeviations;

    private HVResult[] result;

    public HistoricalVolatility(HVRequest request) {
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
        BigDecimal[] continuouslyCompoundedReturns = new BigDecimal[originalData.length];
        for (int currentIndex = standardDeviations; currentIndex < continuouslyCompoundedReturns.length; currentIndex++) {
            continuouslyCompoundedReturns[currentIndex] = calculateContinuouslyCompoundedReturn(currentIndex);
        }
        return continuouslyCompoundedReturns;
    }

    // ln(C(i) - C(i-stDev))
    private BigDecimal calculateContinuouslyCompoundedReturn(int index) {
        return MathHelper.ln(MathHelper.divide(originalData[index].getPriceByType(priceType), originalData[index - standardDeviations].getPriceByType(priceType)));
    }

    private BigDecimal[] calculateOneDayVolatilityValues(BigDecimal[] continuouslyCompoundedReturns) {
        BigDecimal[] standardDeviationValues = calculateStandardDeviationValues(continuouslyCompoundedReturns);
        BigDecimal[] result = new BigDecimal[originalData.length];
        System.arraycopy(standardDeviationValues, 0, result, standardDeviations, standardDeviationValues.length);
        return result;
    }

    private BigDecimal[] calculateStandardDeviationValues(BigDecimal[] continuouslyCompoundedReturns) {
        return IndicatorResultExtractor.extract(calculateStandardDeviation(continuouslyCompoundedReturns));
    }

    private StDevResult[] calculateStandardDeviation(BigDecimal[] continuouslyCompoundedReturns) {
        return new StandardDeviation(buildStDevRequest(continuouslyCompoundedReturns)).getResult();
    }

    private StDevRequest buildStDevRequest(BigDecimal[] continuouslyCompoundedReturns) {
        return StDevRequest.builder()
                .originalData(FakeTicksCreator.createWithCloseOnly(continuouslyCompoundedReturns))
                .period(period)
                .movingAverageType(SIMPLE_MOVING_AVERAGE)
                .priceType(CLOSE)
                .build();
    }

    private void calculateHistoricalVolatility(BigDecimal[] oneDayVolatilityValues) {
        BigDecimal annualizingFactor = calculateAnnualizingFactor();
        for (int currentIndex = 0; currentIndex < result.length; currentIndex++) {
            result[currentIndex] = new HVResult(originalData[currentIndex].getTickTime(), calculateHistoricalVolatility(oneDayVolatilityValues[currentIndex], annualizingFactor));
        }
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
