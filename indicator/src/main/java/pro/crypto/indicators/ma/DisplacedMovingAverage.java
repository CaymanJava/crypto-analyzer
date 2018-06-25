package pro.crypto.indicators.ma;

import pro.crypto.exception.WrongIncomingParametersException;
import pro.crypto.helper.IndicatorTypeChecker;
import pro.crypto.helper.TimeFrameShifter;
import pro.crypto.model.IndicatorType;
import pro.crypto.model.Shift;
import pro.crypto.model.request.MARequest;
import pro.crypto.model.result.MAResult;
import pro.crypto.model.tick.PriceType;
import pro.crypto.model.tick.Tick;

import java.math.BigDecimal;
import java.util.stream.IntStream;

import static java.lang.String.format;
import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;
import static pro.crypto.model.IndicatorType.DISPLACED_MOVING_AVERAGE;
import static pro.crypto.model.IndicatorType.SIMPLE_MOVING_AVERAGE;
import static pro.crypto.model.ShiftType.LEFT;
import static pro.crypto.model.ShiftType.RIGHT;

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
        initResultArray(originalData.length + shift.getValue());
        MAResult[] originalResult = MovingAverageFactory.create(buildMovingAverageRequest())
                .getResult();
        shiftResult(originalResult);
    }

    private void checkData(IndicatorType originalIndicatorType, PriceType priceType, Tick[] originalData, int period, Shift shift) {
        checkShiftData(shift);
        checkOriginalIndicatorType(originalIndicatorType);
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

        if (shift.getType() == LEFT) {
            throw new WrongIncomingParametersException(format("Left shift type is not available in moving average indicator {indicator: {%s}}", getType().toString()));
        }

        if (isNull(shift.getTimeFrame())) {
            throw new WrongIncomingParametersException(format("Incoming shift time frame is null {indicator: {%s}}", getType().toString()));
        }

        if (shift.getValue() <= 0) {
            throw new WrongIncomingParametersException(format("Incoming shift value should be more than zero {indicator: {%s}, shiftValue: {%d}}",
                    getType().toString(), shift.getValue()));
        }
    }

    private void checkOriginalIndicatorType(IndicatorType originalIndicatorType) {
        if (nonNull(originalIndicatorType) && !IndicatorTypeChecker.isMovingAverageType(originalIndicatorType)) {
            throw new WrongIncomingParametersException(format("Incoming original indicator type is not a moving average {indicator: {%s}}, movingAverageType: {%s}",
                    getType().toString(), originalIndicatorType.toString()));
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
        if (shift.getType() == RIGHT) {
            fillInRightShift(intermediateResult);
        }
    }

    private void fillInRightShift(MAResult[] originalResult) {
        fillInTime(originalResult);
        shiftTime(originalResult);
        fillInShiftedValues(originalResult);
    }

    private void fillInTime(MAResult[] originalResult) {
        IntStream.range(0, originalResult.length)
                .forEach(idx -> result[idx] = new MAResult(originalResult[idx].getTime(), null));
    }

    private void shiftTime(MAResult[] originalResult) {
        IntStream.rangeClosed(originalResult.length, result.length - 1)
                .distinct()
                .forEach(idx -> result[idx] = new MAResult(
                        new TimeFrameShifter(originalResult[idx - shift.getValue()].getTime(), shift).shiftTime(), null));
    }

    private void fillInShiftedValues(MAResult[] originalResult) {
        IntStream.range(shift.getValue(), result.length)
                .forEach(idx -> result[idx].setIndicatorValue(originalResult[idx - shift.getValue()].getIndicatorValue()));
    }

}
