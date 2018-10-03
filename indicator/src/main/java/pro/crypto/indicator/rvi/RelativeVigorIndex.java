package pro.crypto.indicator.rvi;

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
import java.util.function.Function;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import static java.util.Objects.isNull;
import static pro.crypto.model.IndicatorType.RELATIVE_VIGOR_INDEX;
import static pro.crypto.model.IndicatorType.TRIANGULAR_MOVING_AVERAGE;
import static pro.crypto.model.tick.PriceType.CLOSE;

public class RelativeVigorIndex implements Indicator<RVIResult> {

    private final static int TRIANGULAR_MOVING_AVERAGE_PERIOD = 4;

    private final Tick[] originalData;
    private final int period;

    private RVIResult[] result;

    public RelativeVigorIndex(IndicatorRequest creationRequest) {
        RVIRequest request = (RVIRequest) creationRequest;
        this.originalData = request.getOriginalData();
        this.period = request.getPeriod();
        checkIncomingData();
    }

    @Override
    public IndicatorType getType() {
        return RELATIVE_VIGOR_INDEX;
    }

    @Override
    public void calculate() {
        BigDecimal[] numeratorValues = calculateNumeratorValues();
        BigDecimal[] denominatorValues = calculateDenominatorValues();
        BigDecimal[] relativeVigorValues = calculateRelativeVigorValues(numeratorValues, denominatorValues);
        BigDecimal[] signalLineValues = calculateSignalLineValues(relativeVigorValues);
        buildRelativeVigorIndexResult(relativeVigorValues, signalLineValues);
    }

    @Override
    public RVIResult[] getResult() {
        if (isNull(result)) {
            calculate();
        }
        return result;
    }

    private void checkIncomingData() {
        checkOriginalData(originalData);
        checkOriginalDataSize(originalData, period + 6);
        checkPeriod(period);
    }

    private BigDecimal[] calculateNumeratorValues() {
        return calculateTriangularMovingAverageValues(calculatePriceChanges(t -> t.getClose().subtract(t.getOpen())));
    }

    private BigDecimal[] calculateDenominatorValues() {
        return calculateTriangularMovingAverageValues(calculatePriceChanges(t -> t.getHigh().subtract(t.getLow())));
    }

    private BigDecimal[] calculatePriceChanges(Function<Tick, BigDecimal> subtractFunction) {
        return Stream.of(originalData)
                .map(subtractFunction)
                .toArray(BigDecimal[]::new);
    }

    private BigDecimal[] calculateRelativeVigorValues(BigDecimal[] numeratorValues, BigDecimal[] denominatorValues) {
        return IntStream.range(0, originalData.length)
                .mapToObj(idx -> calculateRelativeVigor(idx, numeratorValues, denominatorValues))
                .toArray(BigDecimal[]::new);
    }

    private BigDecimal calculateRelativeVigor(int currentIndex, BigDecimal[] numeratorValues, BigDecimal[] denominatorValues) {
        return currentIndex >= period + TRIANGULAR_MOVING_AVERAGE_PERIOD - 2
                ? calculateRelativeVigorValue(currentIndex, numeratorValues, denominatorValues)
                : null;
    }

    private BigDecimal calculateRelativeVigorValue(int currentIndex, BigDecimal[] numeratorValues, BigDecimal[] denominatorValues) {
        return MathHelper.divide(calculateSum(currentIndex, numeratorValues), calculateSum(currentIndex, denominatorValues));
    }

    private BigDecimal calculateSum(int currentIndex, BigDecimal[] numeratorValues) {
        return MathHelper.sum(Arrays.copyOfRange(numeratorValues, currentIndex - period + 1, currentIndex + 1));
    }

    private BigDecimal[] calculateSignalLineValues(BigDecimal[] relativeVigorValues) {
        BigDecimal[] signalLineValues = calculateTriangularMovingAverageValues(relativeVigorValues);
        BigDecimal[] result = new BigDecimal[originalData.length];
        System.arraycopy(signalLineValues, 0, result, period + TRIANGULAR_MOVING_AVERAGE_PERIOD - 2, signalLineValues.length);
        return result;
    }

    private BigDecimal[] calculateTriangularMovingAverageValues(BigDecimal[] values) {
        return IndicatorResultExtractor.extractIndicatorValue(calculateTriangularMovingAverage(values));
    }

    private SimpleIndicatorResult[] calculateTriangularMovingAverage(BigDecimal[] values) {
        return MovingAverageFactory.create(buildTMARequest(values)).getResult();
    }

    private IndicatorRequest buildTMARequest(BigDecimal[] values) {
        return MARequest.builder()
                .originalData(FakeTicksCreator.createWithCloseOnly(values))
                .period(TRIANGULAR_MOVING_AVERAGE_PERIOD)
                .indicatorType(TRIANGULAR_MOVING_AVERAGE)
                .priceType(CLOSE)
                .build();
    }

    private void buildRelativeVigorIndexResult(BigDecimal[] relativeVigorValues, BigDecimal[] signalLineValues) {
        result = IntStream.range(0, originalData.length)
                .mapToObj(idx -> new RVIResult(originalData[idx].getTickTime(), relativeVigorValues[idx], signalLineValues[idx]))
                .toArray(RVIResult[]::new);
    }

}
