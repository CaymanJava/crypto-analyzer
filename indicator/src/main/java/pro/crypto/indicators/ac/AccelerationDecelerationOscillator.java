package pro.crypto.indicators.ac;

import pro.crypto.helper.FakeTicksCreator;
import pro.crypto.helper.IncreasedQualifier;
import pro.crypto.helper.IndicatorResultExtractor;
import pro.crypto.helper.MathHelper;
import pro.crypto.indicators.ao.AwesomeOscillator;
import pro.crypto.indicators.ma.MovingAverageFactory;
import pro.crypto.model.Indicator;
import pro.crypto.model.IndicatorType;
import pro.crypto.model.request.ACRequest;
import pro.crypto.model.request.AORequest;
import pro.crypto.model.request.MARequest;
import pro.crypto.model.result.ACResult;
import pro.crypto.model.result.AOResult;
import pro.crypto.model.result.MAResult;
import pro.crypto.model.tick.Tick;

import java.math.BigDecimal;
import java.util.stream.IntStream;

import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;
import static pro.crypto.model.IndicatorType.ACCELERATION_DECELERATION_OSCILLATOR;
import static pro.crypto.model.IndicatorType.SIMPLE_MOVING_AVERAGE;
import static pro.crypto.model.tick.PriceType.CLOSE;

public class AccelerationDecelerationOscillator implements Indicator<ACResult> {

    private final Tick[] originalData;
    private final int slowPeriod;
    private final int fastPeriod;
    private final int smoothedPeriod;

    private ACResult[] result;

    public AccelerationDecelerationOscillator(ACRequest request) {
        this.originalData = request.getOriginalData();
        this.slowPeriod = request.getSlowPeriod();
        this.fastPeriod = request.getFastPeriod();
        this.smoothedPeriod = request.getSmoothedPeriod();
        checkIncomingData();
    }

    @Override
    public IndicatorType getType() {
        return ACCELERATION_DECELERATION_OSCILLATOR;
    }

    @Override
    public void calculate() {
        result = new ACResult[originalData.length];
        BigDecimal[] awesomeOscillatorValues = calculateAwesomeOscillatorValues();
        BigDecimal[] accelerationDecelerationValues = calculateAccelerationDecelerationOscillator(awesomeOscillatorValues);
        Boolean[] increasedFlags = IncreasedQualifier.define(accelerationDecelerationValues);
        buildAccelerationDecelerationResult(accelerationDecelerationValues, increasedFlags);
    }

    @Override
    public ACResult[] getResult() {
        if (isNull(result)) {
            calculate();
        }
        return result;
    }

    private void checkIncomingData() {
        checkOriginalData(originalData);
        checkOriginalDataSize(originalData, slowPeriod);
        checkOriginalDataSize(originalData, fastPeriod);
        checkOriginalDataSize(originalData, fastPeriod + smoothedPeriod);
        checkPeriod(slowPeriod);
        checkPeriod(fastPeriod);
        checkPeriod(smoothedPeriod);
    }

    private BigDecimal[] calculateAwesomeOscillatorValues() {
        return IndicatorResultExtractor.extract(calculateAwesomeOscillator());
    }

    private AOResult[] calculateAwesomeOscillator() {
        return new AwesomeOscillator(buildAORequest()).getResult();
    }

    private AORequest buildAORequest() {
        return AORequest.builder()
                .originalData(originalData)
                .slowPeriod(slowPeriod)
                .fastPeriod(fastPeriod)
                .build();
    }

    private BigDecimal[] calculateAccelerationDecelerationOscillator(BigDecimal[] awesomeOscillatorValues) {
        BigDecimal[] movingAverageValues = calculateMovingAverage(awesomeOscillatorValues);
        return calculateAccelerationDecelerationOscillator(awesomeOscillatorValues, movingAverageValues);
    }

    private BigDecimal[] calculateMovingAverage(BigDecimal[] awesomeOscillatorValues) {
        BigDecimal[] maValues = IndicatorResultExtractor.extract(calculateMovingAverageValues(awesomeOscillatorValues));
        BigDecimal[] result = new BigDecimal[awesomeOscillatorValues.length];
        System.arraycopy(maValues, 0, result, fastPeriod - 1, maValues.length);
        return result;
    }

    private MAResult[] calculateMovingAverageValues(BigDecimal[] awesomeOscillatorValues) {
        return MovingAverageFactory.create(buildMARequest(awesomeOscillatorValues))
                .getResult();
    }

    private MARequest buildMARequest(BigDecimal[] awesomeOscillatorValues) {
        return MARequest.builder()
                .originalData(FakeTicksCreator.createWithCloseOnly(awesomeOscillatorValues))
                .priceType(CLOSE)
                .indicatorType(SIMPLE_MOVING_AVERAGE)
                .period(smoothedPeriod)
                .build();
    }

    private BigDecimal[] calculateAccelerationDecelerationOscillator(BigDecimal[] awesomeOscillatorValues, BigDecimal[] movingAverageValues) {
        return IntStream.range(0, originalData.length)
                .mapToObj(idx -> calculateAccelerationDecelerationOscillator(awesomeOscillatorValues[idx], movingAverageValues[idx]))
                .toArray(BigDecimal[]::new);
    }

    private BigDecimal calculateAccelerationDecelerationOscillator(BigDecimal awesomeOscillatorValue, BigDecimal movingAverageValue) {
        return nonNull(awesomeOscillatorValue) && nonNull(movingAverageValue)
                ? calculateACValue(awesomeOscillatorValue, movingAverageValue)
                : null;
    }

    private BigDecimal calculateACValue(BigDecimal awesomeOscillatorValue, BigDecimal movingAverageValue) {
        return MathHelper.scaleAndRound(awesomeOscillatorValue.subtract(movingAverageValue));
    }

    private void buildAccelerationDecelerationResult(BigDecimal[] accelerationDecelerationValues, Boolean[] increasedFlags) {
        IntStream.range(0, result.length)
                .forEach(idx -> result[idx] = new ACResult(
                        originalData[idx].getTickTime(),
                        accelerationDecelerationValues[idx],
                        increasedFlags[idx]));
    }

}
