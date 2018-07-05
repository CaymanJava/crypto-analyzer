package pro.crypto.indicator.cfo;

import pro.crypto.helper.IndicatorResultExtractor;
import pro.crypto.helper.MathHelper;
import pro.crypto.indicator.lr.LinearRegression;
import pro.crypto.model.Indicator;
import pro.crypto.model.IndicatorType;
import pro.crypto.indicator.lr.LRRequest;
import pro.crypto.indicator.lr.LRResult;
import pro.crypto.model.tick.PriceType;
import pro.crypto.model.tick.Tick;

import java.math.BigDecimal;
import java.util.stream.IntStream;

import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;
import static pro.crypto.model.IndicatorType.CHANDE_FORECAST_OSCILLATOR;

public class ChandeForecastOscillator implements Indicator<CFOResult> {

    private final Tick[] originalData;
    private final int period;
    private final PriceType priceType;

    private CFOResult[] result;

    public ChandeForecastOscillator(CFORequest request) {
        this.originalData = request.getOriginalData();
        this.period = request.getPeriod();
        this.priceType = request.getPriceType();
        checkIncomingData();
    }

    @Override
    public IndicatorType getType() {
        return CHANDE_FORECAST_OSCILLATOR;
    }

    @Override
    public void calculate() {
        result = new CFOResult[originalData.length];
        BigDecimal[] linearRegressionValues = calculateLinearRegressionValues();
        calculateChandeForecastOscillatorResult(linearRegressionValues);
    }

    @Override
    public CFOResult[] getResult() {
        if (isNull(result)) {
            calculate();
        }
        return result;
    }

    private void checkIncomingData() {
        checkOriginalData(originalData);
        checkOriginalDataSize(originalData, period);
        checkPeriod(period);
        checkPriceType(priceType);
    }

    private BigDecimal[] calculateLinearRegressionValues() {
        return IndicatorResultExtractor.extract(calculateLinearRegression());
    }

    private LRResult[] calculateLinearRegression() {
        return new LinearRegression(buildLRRequest()).getResult();
    }

    private LRRequest buildLRRequest() {
        return LRRequest.builder()
                .originalData(originalData)
                .period(period)
                .priceType(priceType)
                .averageCalculation(true)
                .build();
    }

    private void calculateChandeForecastOscillatorResult(BigDecimal[] linearRegressionValues) {
        IntStream.range(0, result.length)
                .forEach(idx -> result[idx] = new CFOResult(originalData[idx].getTickTime(),
                        calculateChandeForecastOscillator(linearRegressionValues[idx], originalData[idx].getPriceByType(priceType))));
    }

    private BigDecimal calculateChandeForecastOscillator(BigDecimal linearRegression, BigDecimal price) {
        return nonNull(linearRegression)
                ? calculateChandeForecastOscillatorValue(linearRegression, price)
                : null;
    }

    // CFO =(PRICE(i) − LinearRegression) * 100 / PRICE(i)
    private BigDecimal calculateChandeForecastOscillatorValue(BigDecimal linearRegression, BigDecimal price) {
        return MathHelper.divide(price.subtract(linearRegression).multiply(new BigDecimal(100)), price);
    }

}
