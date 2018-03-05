package pro.crypto.moving.average;

import pro.crypto.model.Indicator;
import pro.crypto.exception.UnknownTypeException;
import pro.crypto.exception.WrongIncomingParametersException;
import pro.crypto.model.PriceType;
import pro.crypto.model.result.MovingAverageResult;
import pro.crypto.model.tick.Tick;

import java.math.BigDecimal;

import static java.lang.String.format;
import static java.util.Objects.isNull;

public abstract class MovingAverage implements Indicator {

    protected MovingAverageResult[] result;
    protected PriceType priceType;

    public MovingAverageResult[] getResult() {
        if (isNull(result)) {
            calculate();
        }
        return result;
    }

    void checkIncomingData(Tick[] originalData, int period, PriceType priceType) {
        checkOriginalData(originalData);
        checkPeriod(originalData, period);
        checkPriceType(priceType);
    }

    void countSimpleAverage(int fromIndex, int currentIndex, int period, Tick[] originalData) {
        BigDecimal simpleAverage = countAndGetSimpleAverage(fromIndex, currentIndex, originalData, period);
        result[currentIndex] = buildMovingAverageResult(currentIndex, period, originalData, simpleAverage);
    }

    void fillStartPositions(Tick[] originalData, int period) {
        for (int currentIndex = 0; currentIndex < period - 1; currentIndex++) {
            result[currentIndex] = new MovingAverageResult(
                    originalData[currentIndex].getTickTime(),
                    scaleAndRoundValue(extractPriceByType(originalData[currentIndex])),
                    null);
        }
    }

    void initResultArray(int length) {
        result = new MovingAverageResult[length];
    }

    BigDecimal countAndGetSimpleAverage(int fromIndex, int toIndex, Tick[] originalData, int period) {
        BigDecimal periodSum = new BigDecimal(0);
        for (int currentIndex = fromIndex; currentIndex <= toIndex; currentIndex++) {
            periodSum = periodSum.add(extractPriceByType(originalData[currentIndex]));
        }
        return scaleAndRoundValue(periodSum.divide(new BigDecimal(period), BigDecimal.ROUND_HALF_UP));
    }

    BigDecimal scaleAndRoundValue(BigDecimal value) {
        return value.setScale(10, BigDecimal.ROUND_HALF_UP);
    }

    BigDecimal extractPriceByType(Tick tick) {
        switch (this.priceType) {
            case HIGH:
                return tick.getHigh();
            case LOW:
                return tick.getLow();
            case OPEN:
                return tick.getOpen();
            case CLOSE:
                return tick.getClose();
            default:
                throw new UnknownTypeException("Unknown price type exception");
        }
    }

    private MovingAverageResult buildMovingAverageResult(int currentIndex, int period, Tick[] originalData, BigDecimal simpleAverage) {
        return new MovingAverageResult(
                originalData[currentIndex].getTickTime(),
                scaleAndRoundValue(extractPriceByType(originalData[currentIndex])),
                simpleAverage);
    }

    private void checkPeriod(Tick[] originalData, int period) {
        if (period >= originalData.length) {
            throw new WrongIncomingParametersException(format("Period should be less than tick data size {indicator: {%s}, period: {%d}, size: {%d}}",
                    getType().toString(), period, originalData.length));
        }

        if (period <= 0) {
            throw new WrongIncomingParametersException(format("Period should be more than 0 {indicator: {%s}, period: {%d}}",
                    getType().toString(), period));
        }
    }

    private void checkOriginalData(Tick[] originalData) {
        if (isNull(originalData)) {
            throw new WrongIncomingParametersException(format("Incoming tick data is null {indicator: {%s}}", getType().toString()));
        }

        if (originalData.length <= 0) {
            throw new WrongIncomingParametersException(format("Incoming tick data size should be > 0 {indicator: {%s}, size: {%d}}", getType().toString(), originalData.length));
        }
    }

    private void checkPriceType(PriceType priceType) {
        if (isNull(priceType)) {
            throw new WrongIncomingParametersException(format("Incoming price type is null {indicator: {%s}}", getType().toString()));
        }
    }

}
