package pro.crypto.indicator.ma;

import pro.crypto.exception.WrongIncomingParametersException;
import pro.crypto.model.indicator.IndicatorType;
import pro.crypto.model.indicator.Shift;
import pro.crypto.model.tick.PriceType;
import pro.crypto.model.tick.Tick;

import java.math.BigDecimal;
import java.util.stream.IntStream;

import static java.lang.String.format;
import static java.util.Objects.isNull;
import static pro.crypto.model.indicator.IndicatorType.DISPLACED_MOVING_AVERAGE;
import static pro.crypto.model.indicator.IndicatorType.SIMPLE_MOVING_AVERAGE;

public class DisplacedMovingAverage extends MovingAverage {

    private final IndicatorType originalIndicatorType;
    private final Tick[] originalData;
    private final int period;
    private final BigDecimal alphaCoefficient;
    private final Shift shift;

    DisplacedMovingAverage(IndicatorType originalIndicatorType, PriceType priceType,
                           Tick[] originalData, int period,
                           BigDecimal alphaCoefficient, Shift shift) {
        checkData(originalIndicatorType, priceType, originalData, period, shift);
        this.originalIndicatorType = isNull(originalIndicatorType) ? SIMPLE_MOVING_AVERAGE : originalIndicatorType;
        this.priceType = priceType;
        this.originalData = originalData;
        this.period = period;
        this.alphaCoefficient = alphaCoefficient;
        this.shift = shift;
    }

    @Override
    public IndicatorType getType() {
        return DISPLACED_MOVING_AVERAGE;
    }

    @Override
    public void calculate() {
        initResultArray(originalData.length);
        MAResult[] originalResult = MovingAverageFactory.create(buildMovingAverageRequest())
                .getResult();
        shiftResult(originalResult);
    }

    private void checkData(IndicatorType originalIndicatorType, PriceType priceType, Tick[] originalData, int period, Shift shift) {
        checkShiftData(shift);
        checkMovingAverageType(originalIndicatorType);
        checkPeriod(period);
        checkIncomingData(originalData, period, priceType);
    }

    private void checkShiftData(Shift shift) {
        if (isNull(shift)) {
            throw new WrongIncomingParametersException(format("Incoming shift data is null {indicator: {%s}}", getType().toString()));
        }

        if (isNull(shift.getType())) {
            throw new WrongIncomingParametersException(format("Incoming shift type is null {indicator: {%s}}", getType().toString()));
        }

        if (shift.getValue() <= 0) {
            throw new WrongIncomingParametersException(format("Incoming shift value should be more than zero {indicator: {%s}, shiftValue: {%d}}",
                    getType().toString(), shift.getValue()));
        }
    }

    private MARequest buildMovingAverageRequest() {
        return MARequest.builder()
                .indicatorType(originalIndicatorType)
                .originalData(originalData)
                .priceType(priceType)
                .period(period)
                .alphaCoefficient(alphaCoefficient)
                .build();
    }

    private void shiftResult(MAResult[] intermediateResult) {
        switch (shift.getType()) {
            case LEFT:
                fillInLeftShift(intermediateResult);
                break;
            case RIGHT:
            default:
                fillInRightShift(intermediateResult);
                break;
        }
    }

    private void fillInRightShift(MAResult[] originalResult) {
        fillInTime(originalResult);
        fillInRightShiftedValues(originalResult);
    }

    private void fillInLeftShift(MAResult[] originalResult) {
        fillInTime(originalResult);
        fillInLeftShiftedValues(originalResult);
    }

    private void fillInTime(MAResult[] originalResult) {
        IntStream.range(0, originalResult.length)
                .forEach(idx -> result[idx] = new MAResult(originalResult[idx].getTime(), null));
    }

    private void fillInRightShiftedValues(MAResult[] originalResult) {
        IntStream.range(shift.getValue(), result.length)
                .filter(idx -> originalResult.length > idx)
                .forEach(idx -> result[idx].setIndicatorValue(originalResult[idx - shift.getValue()].getIndicatorValue()));
    }

    private void fillInLeftShiftedValues(MAResult[] originalResult) {
        IntStream.range(0, result.length)
                .filter(idx -> originalResult.length > idx + shift.getValue())
                .forEach(idx -> result[idx].setIndicatorValue(originalResult[idx + shift.getValue()].getIndicatorValue()));
    }

}
