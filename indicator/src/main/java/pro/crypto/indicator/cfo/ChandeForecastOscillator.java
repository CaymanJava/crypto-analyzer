package pro.crypto.indicator.cfo;

import pro.crypto.helper.FakeTicksCreator;
import pro.crypto.helper.IndicatorResultExtractor;
import pro.crypto.helper.MathHelper;
import pro.crypto.indicator.lr.LRRequest;
import pro.crypto.indicator.lr.LinearRegression;
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
import static pro.crypto.model.IndicatorType.CHANDE_FORECAST_OSCILLATOR;
import static pro.crypto.model.tick.PriceType.CLOSE;

public class ChandeForecastOscillator implements Indicator<CFOResult> {

    private final Tick[] originalData;
    private final int period;
    private final int movingAveragePeriod;
    private final IndicatorType movingAverageType;
    private final PriceType priceType;

    private CFOResult[] result;

    public ChandeForecastOscillator(IndicatorRequest creationRequest) {
        CFORequest request = (CFORequest) creationRequest;
        this.originalData = request.getOriginalData();
        this.period = request.getPeriod();
        this.movingAveragePeriod = request.getMovingAveragePeriod();
        this.movingAverageType = request.getMovingAverageType();
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
        BigDecimal[] chandeForecastValues = calculateChandeForecastOscillator(linearRegressionValues);
        BigDecimal[] signalLineValues = calculateSignalLine(chandeForecastValues);
        buildChandeForecastOscillatorResult(chandeForecastValues, signalLineValues);
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
        checkOriginalDataSize(originalData, period + movingAveragePeriod);
        checkMovingAverageType(movingAverageType);
        checkPeriod(period);
        checkPeriod(movingAveragePeriod);
        checkPriceType(priceType);
    }

    private BigDecimal[] calculateLinearRegressionValues() {
        return IndicatorResultExtractor.extractIndicatorValues(calculateLinearRegression());
    }

    private SimpleIndicatorResult[] calculateLinearRegression() {
        return new LinearRegression(buildLRRequest()).getResult();
    }

    private IndicatorRequest buildLRRequest() {
        return LRRequest.builder()
                .originalData(originalData)
                .period(period)
                .priceType(priceType)
                .averageCalculation(true)
                .build();
    }

    private BigDecimal[] calculateChandeForecastOscillator(BigDecimal[] linearRegressionValues) {
        return IntStream.range(0, result.length)
                .mapToObj(idx -> calculateChandeForecastOscillator(linearRegressionValues[idx], originalData[idx].getPriceByType(priceType)))
                .toArray(BigDecimal[]::new);
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

    private BigDecimal[] calculateSignalLine(BigDecimal[] chandeForecastValues) {
        BigDecimal[] movingAverageValues = calculateMovingAverageValues(chandeForecastValues);
        BigDecimal[] result = new BigDecimal[originalData.length];
        System.arraycopy(movingAverageValues, 0, result, period - 1, movingAverageValues.length);
        return result;
    }

    private BigDecimal[] calculateMovingAverageValues(BigDecimal[] chandeForecastValues) {
        return IndicatorResultExtractor.extractIndicatorValues(calculateMovingAverage(chandeForecastValues));
    }

    private SimpleIndicatorResult[] calculateMovingAverage(BigDecimal[] chandeForecastValues) {
        return MovingAverageFactory.create(buildMARequest(chandeForecastValues)).getResult();
    }

    private IndicatorRequest buildMARequest(BigDecimal[] chandeForecastValues) {
        return MARequest.builder()
                .originalData(FakeTicksCreator.createWithCloseOnly(chandeForecastValues))
                .priceType(CLOSE)
                .indicatorType(movingAverageType)
                .period(movingAveragePeriod)
                .build();
    }

    private void buildChandeForecastOscillatorResult(BigDecimal[] chandeForecastValues, BigDecimal[] signalLineValues) {
        result = IntStream.range(0, originalData.length)
                .mapToObj(idx -> buildChandeForecastOscillatorResult(chandeForecastValues[idx], signalLineValues[idx], idx))
                .toArray(CFOResult[]::new);
    }

    private CFOResult buildChandeForecastOscillatorResult(BigDecimal chandeForecastValue, BigDecimal signalLineValue, int currentIndex) {
        return new CFOResult(originalData[currentIndex].getTickTime(), chandeForecastValue, signalLineValue);
    }

}
