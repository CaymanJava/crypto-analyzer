package pro.crypto.indicators.eri;

import pro.crypto.helper.IndicatorResultExtractor;
import pro.crypto.helper.MathHelper;
import pro.crypto.indicators.ma.MovingAverageFactory;
import pro.crypto.model.Indicator;
import pro.crypto.model.IndicatorType;
import pro.crypto.model.request.ERIRequest;
import pro.crypto.model.request.MARequest;
import pro.crypto.model.result.ERIResult;
import pro.crypto.model.result.MAResult;
import pro.crypto.model.tick.Tick;

import java.math.BigDecimal;
import java.util.stream.IntStream;

import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;
import static pro.crypto.model.IndicatorType.ELDER_RAY_INDEX;
import static pro.crypto.model.IndicatorType.EXPONENTIAL_MOVING_AVERAGE;
import static pro.crypto.model.tick.PriceType.CLOSE;

public class ElderRayIndex implements Indicator<ERIResult> {

    private final Tick[] originalData;
    private final int period;

    private ERIResult[] result;

    public ElderRayIndex(ERIRequest request) {
        this.originalData = request.getOriginalData();
        this.period = request.getPeriod();
        checkIncomingData();
    }

    @Override
    public IndicatorType getType() {
        return ELDER_RAY_INDEX;
    }

    @Override
    public void calculate() {
        result = new ERIResult[originalData.length];
        BigDecimal[] movingAverageValues = calculateMovingAverageValues();
        calculateElderRayIndicatorResult(movingAverageValues);
    }

    @Override
    public ERIResult[] getResult() {
        if (isNull(result)) {
            calculate();
        }
        return result;
    }

    private void checkIncomingData() {
        checkOriginalData(originalData);
        checkOriginalDataSize(originalData, period);
        checkPeriod(period);
    }

    private BigDecimal[] calculateMovingAverageValues() {
        return IndicatorResultExtractor.extract(calculateExponentialMovingAverage());
    }

    private MAResult[] calculateExponentialMovingAverage() {
        return MovingAverageFactory.create(buildMARequest()).getResult();
    }

    private MARequest buildMARequest() {
        return MARequest.builder()
                .originalData(originalData)
                .period(period)
                .priceType(CLOSE)
                .indicatorType(EXPONENTIAL_MOVING_AVERAGE)
                .build();
    }

    private void calculateElderRayIndicatorResult(BigDecimal[] movingAverageValues) {
        IntStream.range(0, result.length)
                .forEach(idx -> result[idx] = calculateElderRayIndicator(originalData[idx], movingAverageValues[idx]));
    }

    private ERIResult calculateElderRayIndicator(Tick tick, BigDecimal movingAverageValue) {
        return nonNull(movingAverageValue)
                ? buildElderRayIndicatorResult(tick, movingAverageValue)
                : new ERIResult(tick.getTickTime(), null, null);
    }

    private ERIResult buildElderRayIndicatorResult(Tick tick, BigDecimal movingAverageValue) {
        return new ERIResult(tick.getTickTime(), calculatePower(tick.getHigh(), movingAverageValue), calculatePower(tick.getLow(), movingAverageValue));
    }

    private BigDecimal calculatePower(BigDecimal high, BigDecimal movingAverageValue) {
        return MathHelper.scaleAndRound(high.subtract(movingAverageValue));
    }

}
