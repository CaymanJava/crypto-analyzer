package pro.crypto.indicator.atr;

import pro.crypto.helper.FakeTicksCreator;
import pro.crypto.helper.IndicatorResultExtractor;
import pro.crypto.helper.MathHelper;
import pro.crypto.helper.TrueRangeCalculator;
import pro.crypto.indicator.ma.MARequest;
import pro.crypto.indicator.ma.MovingAverageFactory;
import pro.crypto.model.Indicator;
import pro.crypto.model.indicator.IndicatorType;
import pro.crypto.model.tick.Tick;
import pro.crypto.request.IndicatorRequest;
import pro.crypto.response.SimpleIndicatorResult;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.stream.IntStream;

import static java.util.Objects.isNull;
import static pro.crypto.model.indicator.IndicatorType.AVERAGE_TRUE_RANGE;
import static pro.crypto.model.indicator.IndicatorType.SIMPLE_MOVING_AVERAGE;
import static pro.crypto.model.tick.PriceType.CLOSE;

public class AverageTrueRange implements Indicator<ATRResult> {

    private final Tick[] originalData;
    private final int period;
    private final IndicatorType movingAverageType;
    private final int movingAveragePeriod;

    private ATRResult[] result;

    private BigDecimal[] indicatorValues;
    private BigDecimal[] signalLineValues;

    public AverageTrueRange(IndicatorRequest creationRequest) {
        ATRRequest request = (ATRRequest) creationRequest;
        this.originalData = request.getOriginalData();
        this.period = request.getPeriod();
        this.movingAverageType = isNull(request.getMovingAverageType()) ? SIMPLE_MOVING_AVERAGE : request.getMovingAverageType();
        this.movingAveragePeriod = request.getMovingAveragePeriod() <= 0 ? request.getPeriod() : request.getMovingAveragePeriod();
        checkIncomingData();
    }

    @Override
    public IndicatorType getType() {
        return AVERAGE_TRUE_RANGE;
    }

    @Override
    public void calculate() {
        result = new ATRResult[originalData.length];
        BigDecimal[] trueRangeValues = TrueRangeCalculator.calculate(originalData);
        calculateAverageTrueRangeValues(trueRangeValues);
        calculateSignalLineValues();
        buildAverageTrueRangeResult();
    }


    @Override
    public ATRResult[] getResult() {
        if (isNull(result)) {
            calculate();
        }
        return result;
    }

    private void checkIncomingData() {
        checkOriginalData(originalData);
        checkOriginalDataSize(originalData, period + movingAveragePeriod);
        checkPeriod(period);
        checkPeriod(movingAveragePeriod);
    }

    private void calculateAverageTrueRangeValues(BigDecimal[] trueRangeValues) {
        indicatorValues = new BigDecimal[originalData.length];
        IntStream.range(0, originalData.length)
                .forEach(idx -> indicatorValues[idx] = calculateAverageTrueRangeValue(trueRangeValues, idx));
    }

    private BigDecimal calculateAverageTrueRangeValue(BigDecimal[] trueRangeValues, int currentIndex) {
        if (currentIndex < period - 1) {
            return null;
        }

        if (currentIndex == period - 1) {
            return calculateFirstValue(trueRangeValues);
        }

        return calculateAverageTrueRange(trueRangeValues[currentIndex], currentIndex);
    }

    private BigDecimal calculateFirstValue(BigDecimal[] trueRangeValues) {
        return MathHelper.average(Arrays.copyOfRange(trueRangeValues, 0, period));
    }

    private BigDecimal calculateAverageTrueRange(BigDecimal trueRangeValue, int currentIndex) {
        return MathHelper.divide(
                indicatorValues[currentIndex - 1]
                        .multiply(new BigDecimal(period - 1))
                        .add(trueRangeValue),
                new BigDecimal(period));
    }

    private void calculateSignalLineValues() {
        signalLineValues = new BigDecimal[originalData.length];
        BigDecimal[] movingAverageValues = calculateMovingAverage();
        System.arraycopy(movingAverageValues, 0, signalLineValues, period - 1, movingAverageValues.length);
    }

    private BigDecimal[] calculateMovingAverage() {
        return IndicatorResultExtractor.extractIndicatorValues(calculateMovingAverageValues());
    }

    private SimpleIndicatorResult[] calculateMovingAverageValues() {
        return MovingAverageFactory.create(buildMARequest()).getResult();
    }

    private IndicatorRequest buildMARequest() {
        return MARequest.builder()
                .originalData(FakeTicksCreator.createWithCloseOnly(indicatorValues))
                .indicatorType(movingAverageType)
                .period(movingAveragePeriod)
                .priceType(CLOSE)
                .build();
    }

    private void buildAverageTrueRangeResult() {
        result = IntStream.range(0, originalData.length)
                .mapToObj(idx -> new ATRResult(originalData[idx].getTickTime(), indicatorValues[idx], signalLineValues[idx]))
                .toArray(ATRResult[]::new);
    }

}
