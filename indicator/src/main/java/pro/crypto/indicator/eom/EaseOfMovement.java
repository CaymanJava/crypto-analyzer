package pro.crypto.indicator.eom;

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
import java.util.stream.IntStream;
import java.util.stream.Stream;

import static java.util.Objects.isNull;
import static pro.crypto.model.IndicatorType.EASE_OF_MOVEMENT;
import static pro.crypto.model.tick.PriceType.CLOSE;

public class EaseOfMovement implements Indicator<EOMResult> {

    private final Tick[] originalData;
    private final IndicatorType movingAverageType;
    private final int movingAveragePeriod;

    private EOMResult[] result;

    public EaseOfMovement(IndicatorRequest creationRequest) {
        EOMRequest request = (EOMRequest) creationRequest;
        this.originalData = request.getOriginalData();
        this.movingAverageType = request.getMovingAverageType();
        this.movingAveragePeriod = request.getMovingAveragePeriod();
        checkIncomingData();
    }

    @Override
    public IndicatorType getType() {
        return EASE_OF_MOVEMENT;
    }

    @Override
    public void calculate() {
        result = new EOMResult[originalData.length];
        BigDecimal[] distanceMovedValues = calculateDistanceMovedValues();
        BigDecimal[] boxRatios = calculateBoxRatios();
        BigDecimal[] notSmoothedEOMValues = calculateNotSmoothedEaseOfMovementValues(distanceMovedValues, boxRatios);
        calculateEaseOfMovementResult(notSmoothedEOMValues);
    }

    @Override
    public EOMResult[] getResult() {
        if (isNull(result)) {
            calculate();
        }
        return result;
    }

    private void checkIncomingData() {
        checkOriginalData(originalData);
        checkOriginalDataSize(originalData, movingAveragePeriod);
        checkPeriod(movingAveragePeriod);
        checkMovingAverageType(movingAverageType);
    }

    private BigDecimal[] calculateDistanceMovedValues() {
        return IntStream.range(0, originalData.length)
                .mapToObj(this::calculateDistanceMoved)
                .toArray(BigDecimal[]::new);
    }

    private BigDecimal calculateDistanceMoved(int currentIndex) {
        return currentIndex > 0
                ? calculateDistanceMovedValue(currentIndex)
                : null;
    }

    // ((High(i) + Low(i)) / 2 - (High(i - 1) + Low(i - 1)) / 2)
    private BigDecimal calculateDistanceMovedValue(int currentIndex) {
        return MathHelper.average(originalData[currentIndex].getHigh(), originalData[currentIndex].getLow())
                .subtract(MathHelper.average(originalData[currentIndex - 1].getHigh(), originalData[currentIndex - 1].getLow()));
    }

    private BigDecimal[] calculateBoxRatios() {
        return Stream.of(originalData)
                .map(this::calculateBoxRatio)
                .toArray(BigDecimal[]::new);
    }

    // Volume(i) / High(i) - Low(i)
    private BigDecimal calculateBoxRatio(Tick tick) {
        return MathHelper.divide(tick.getBaseVolume(), tick.getHigh().subtract(tick.getLow()));
    }

    // DM / BR
    private BigDecimal[] calculateNotSmoothedEaseOfMovementValues(BigDecimal[] distanceMovedValues, BigDecimal[] boxRatios) {
        return IntStream.range(0, originalData.length)
                .mapToObj(idx -> MathHelper.divide(distanceMovedValues[idx], boxRatios[idx]))
                .toArray(BigDecimal[]::new);
    }

    private void calculateEaseOfMovementResult(BigDecimal[] notSmoothedEOMValues) {
        BigDecimal[] smoothedEOMValues = calculateMovingAverageValues(notSmoothedEOMValues);
        IntStream.range(0, result.length)
                .forEach(idx -> result[idx] = new EOMResult(originalData[idx].getTickTime(), smoothedEOMValues[idx]));
    }

    private BigDecimal[] calculateMovingAverageValues(BigDecimal[] notSmoothedEOMValues) {
        BigDecimal[] movingAverageValues = IndicatorResultExtractor.extractIndicatorValues(calculateMovingAverage(notSmoothedEOMValues));
        BigDecimal[] smoothedEOMValues = new BigDecimal[originalData.length];
        System.arraycopy(movingAverageValues, 0, smoothedEOMValues, 1, movingAverageValues.length);
        return smoothedEOMValues;
    }

    private SimpleIndicatorResult[] calculateMovingAverage(BigDecimal[] notSmoothedEOMValues) {
        return MovingAverageFactory.create(buildMARequest(notSmoothedEOMValues)).getResult();
    }

    private IndicatorRequest buildMARequest(BigDecimal[] notSmoothedEOMValues) {
        return MARequest.builder()
                .originalData(FakeTicksCreator.createWithCloseOnly(notSmoothedEOMValues))
                .period(movingAveragePeriod)
                .indicatorType(movingAverageType)
                .priceType(CLOSE)
                .build();
    }

}
