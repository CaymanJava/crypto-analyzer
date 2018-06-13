package pro.crypto.model;

import pro.crypto.exception.WrongIncomingParametersException;
import pro.crypto.helper.IndicatorTypeChecker;
import pro.crypto.model.tick.PriceType;
import pro.crypto.model.tick.Tick;

import static java.lang.String.format;
import static java.util.Locale.ENGLISH;
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

    default void checkOriginalData(Tick originalData) {
        if (isNull(originalData)) {
            throw new WrongIncomingParametersException(format("Incoming tick data is null {indicator: {%s}}", getType().toString()));
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

    default void checkDisplaced(int displaced) {
        if (displaced <= 0) {
            throw new WrongIncomingParametersException(format("Displaced value should be more than 0 {indicator: {%s}, displaced: {%d}}",
                    getType().toString(), displaced));
        }
    }

    default void checkShift(double shift) {
        if (shift < 0) {
            throw new WrongIncomingParametersException(format(ENGLISH, "Shift should be more or equals 0 {indicator: {%s}, shift: {%.2f}}",
                    getType().toString(), shift));
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
