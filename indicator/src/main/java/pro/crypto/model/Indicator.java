package pro.crypto.model;

import pro.crypto.exception.WrongIncomingParametersException;
import pro.crypto.helper.IndicatorTypeChecker;
import pro.crypto.model.tick.PriceType;
import pro.crypto.model.tick.Tick;

import static java.lang.String.format;
import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;

public interface Indicator<T extends IndicatorResult> {

    IndicatorType getType();

    void calculate();

    T[] getResult();

    default void checkOriginalData(Tick... originalData) {
        if (isNull(originalData)) {
            throw new WrongIncomingParametersException(format("Incoming tick data is null {indicator: {%s}}", getType().toString()));
        }

        if (originalData.length == 0) {
            throw new WrongIncomingParametersException(format("Incoming tick data size should be > 0 {indicator: {%s}, size: {%d}}", getType().toString(), originalData.length));
        }
    }

    default void checkOriginalDataSize(Tick[] originalData, int period) {
        if (period >= originalData.length) {
            throw new WrongIncomingParametersException(format("Period should be less than tick data size {indicator: {%s}, period: {%d}, size: {%d}}",
                    getType().toString(), period, originalData.length));
        }
    }

    default void checkPeriod(int period) {
        if (period <= 0) {
            throw new WrongIncomingParametersException(format("Period should be more than 0 {indicator: {%s}, period: {%d}}",
                    getType().toString(), period));
        }
    }

    default void checkPriceType(PriceType priceType) {
        if (isNull(priceType)) {
            throw new WrongIncomingParametersException(format("Incoming price type is null {indicator: {%s}}", getType().toString()));
        }
    }

    default void checkMovingAverageType(IndicatorType movingAverageType) {
        if (nonNull(movingAverageType) && !IndicatorTypeChecker.isMovingAverageType(movingAverageType)) {
            throw new WrongIncomingParametersException(format("Incoming original indicator type is not a moving average {indicator: {%s}}, movingAverageType: {%s}",
                    getType().toString(), movingAverageType.toString()));
        }
    }

}
