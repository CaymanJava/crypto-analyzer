package pro.crypto.indicators.uo;

import pro.crypto.exception.WrongIncomingParametersException;
import pro.crypto.helper.MathHelper;
import pro.crypto.helper.TrueRangeCalculator;
import pro.crypto.model.Indicator;
import pro.crypto.model.IndicatorType;
import pro.crypto.model.request.UORequest;
import pro.crypto.model.result.UOResult;
import pro.crypto.model.tick.Tick;

import java.math.BigDecimal;
import java.util.Arrays;

import static java.lang.String.format;
import static java.util.Objects.isNull;
import static pro.crypto.model.IndicatorType.ULTIMATE_OSCILLATOR;

public class UltimateOscillator implements Indicator<UOResult> {

    private final Tick[] originalData;
    private final int shortPeriod;
    private final int middlePeriod;
    private final int longPeriod;

    private UOResult[] result;

    public UltimateOscillator(UORequest request) {
        this.originalData = request.getOriginalData();
        this.shortPeriod = request.getShortPeriod();
        this.middlePeriod = request.getMiddlePeriod();
        this.longPeriod = request.getLongPeriod();
        checkIncomingData();
    }

    @Override
    public IndicatorType getType() {
        return ULTIMATE_OSCILLATOR;
    }

    @Override
    public void calculate() {
        result = new UOResult[originalData.length];
        BigDecimal[] buyingPressureValues = calculateBuyingPressureValues();
        BigDecimal[] trueRangeValues = TrueRangeCalculator.calculate(originalData);
        calculateUltimateOscillatorValues(buyingPressureValues, trueRangeValues);
    }

    @Override
    public UOResult[] getResult() {
        if (isNull(result)) {
            calculate();
        }
        return result;
    }

    private void checkIncomingData() {
        checkOriginalData(originalData);
        checkPeriods();
    }

    private void checkPeriods() {
        checkPeriod(shortPeriod);
        checkPeriod(middlePeriod);
        checkPeriod(longPeriod);
        checkPeriodsLength();
        checkIncomingDataLength();
    }

    private void checkPeriodsLength() {
        if (shortPeriod > middlePeriod || middlePeriod > longPeriod) {
            throw new WrongIncomingParametersException(format("Incorrect period values " +
                            "{indicator: {%s}, shortPeriod: {%d}, middlePeriod: {%d}}, longPeriod: {%d}}",
                    getType().toString(), shortPeriod, middlePeriod, longPeriod));
        }
    }

    private void checkIncomingDataLength() {
        if (originalData.length <= longPeriod + 1) {
            throw new WrongIncomingParametersException(format("Incoming tick data is not enough " +
                            "{indicator: {%s}, tickLength: {%d}, fastPeriod: {%d}}",
                    getType().toString(), originalData.length, longPeriod));
        }
    }

    private BigDecimal[] calculateBuyingPressureValues() {
        BigDecimal[] buyingPressureValues = new BigDecimal[originalData.length];
        for (int currentIndex = 1; currentIndex < buyingPressureValues.length; currentIndex++) {
            buyingPressureValues[currentIndex] = calculateBuyingPressureValue(currentIndex);
        }
        return buyingPressureValues;
    }

    private BigDecimal calculateBuyingPressureValue(int currentIndex) {
        BigDecimal trueLength = calculateTrueLength(currentIndex);
        return originalData[currentIndex].getClose().subtract(trueLength);
    }

    private BigDecimal calculateTrueLength(int currentIndex) {
        return MathHelper.min(originalData[currentIndex].getLow(), originalData[currentIndex - 1].getClose());
    }

    private void calculateUltimateOscillatorValues(BigDecimal[] buyingPressureValues, BigDecimal[] trueRangeValues) {
        fillInInitialPositions();
        fillInRemainPosition(buyingPressureValues, trueRangeValues);
    }

    private void fillInInitialPositions() {
        for (int currentIndex = 0; currentIndex < longPeriod; currentIndex++) {
            result[currentIndex] = new UOResult(originalData[currentIndex].getTickTime(), null);
        }
    }

    private void fillInRemainPosition(BigDecimal[] buyingPressureValues, BigDecimal[] trueRangeValues) {
        for (int currentPeriod = longPeriod; currentPeriod < originalData.length; currentPeriod++) {
            result[currentPeriod] = calculateUltimateOscillator(buyingPressureValues, trueRangeValues, currentPeriod);
        }
    }

    private UOResult calculateUltimateOscillator(BigDecimal[] buyingPressureValues, BigDecimal[] trueRangeValues, int currentPeriod) {
        BigDecimal shortQuotient = calculateShortQuotient(buyingPressureValues, trueRangeValues, currentPeriod);
        BigDecimal middleQuotient = calculateMiddleQuotient(buyingPressureValues, trueRangeValues, currentPeriod);
        BigDecimal longQuotient = calculateLongQuotient(buyingPressureValues, trueRangeValues, currentPeriod);
        if (isNull(shortQuotient) || isNull(middleQuotient) || isNull(longQuotient)) {
            return new UOResult(originalData[currentPeriod].getTickTime(), null);
        }
        return calculateUltimateOscillatorValue(shortQuotient, middleQuotient, longQuotient, currentPeriod);
    }

    private BigDecimal calculateShortQuotient(BigDecimal[] buyingPressureValues, BigDecimal[] trueRangeValues, int currentPeriod) {
        BigDecimal shortBuyingPressureSum = calculateValuesSum(buyingPressureValues, currentPeriod, shortPeriod);
        BigDecimal shortTrueRangeSum = calculateValuesSum(trueRangeValues, currentPeriod, shortPeriod);
        return MathHelper.divide(shortBuyingPressureSum, shortTrueRangeSum);
    }

    private BigDecimal calculateMiddleQuotient(BigDecimal[] buyingPressureValues, BigDecimal[] trueRangeValues, int currentPeriod) {
        BigDecimal shortBuyingPressureSum = calculateValuesSum(buyingPressureValues, currentPeriod, middlePeriod);
        BigDecimal shortTrueRangeSum = calculateValuesSum(trueRangeValues, currentPeriod, middlePeriod);
        return MathHelper.divide(shortBuyingPressureSum, shortTrueRangeSum);
    }

    private BigDecimal calculateLongQuotient(BigDecimal[] buyingPressureValues, BigDecimal[] trueRangeValues, int currentPeriod) {
        BigDecimal shortBuyingPressureSum = calculateValuesSum(buyingPressureValues, currentPeriod, longPeriod);
        BigDecimal shortTrueRangeSum = calculateValuesSum(trueRangeValues, currentPeriod, longPeriod);
        return MathHelper.divide(shortBuyingPressureSum, shortTrueRangeSum);
    }

    private BigDecimal calculateValuesSum(BigDecimal[] values, int currentIndex, int period) {
        return MathHelper.sum(Arrays.copyOfRange(values, currentIndex - period + 1, currentIndex + 1));
    }

    private UOResult calculateUltimateOscillatorValue(BigDecimal shortQuotient, BigDecimal middleQuotient, BigDecimal longQuotient, int currentPeriod) {
        BigDecimal rawValue = calculateUltimateOscillatorRawValue(shortQuotient, middleQuotient, longQuotient);
        return isNull(rawValue)
                ? new UOResult(originalData[currentPeriod].getTickTime(), null)
                : new UOResult(originalData[currentPeriod].getTickTime(), rawValue.multiply(new BigDecimal(100)));
    }

    private BigDecimal calculateUltimateOscillatorRawValue(BigDecimal shortQuotient, BigDecimal middleQuotient, BigDecimal longQuotient) {
        final int shortPeriodWeightCoefficient = 4;
        final int middlePeriodWeightCoefficient = 2;
        final int longPeriodWeightCoefficient = 1;
        return MathHelper.divide(
                MathHelper.sum(
                        shortQuotient.multiply(new BigDecimal(shortPeriodWeightCoefficient)),
                        middleQuotient.multiply(new BigDecimal(middlePeriodWeightCoefficient)),
                        longQuotient.multiply(new BigDecimal(longPeriodWeightCoefficient))),
                new BigDecimal(shortPeriodWeightCoefficient + middlePeriodWeightCoefficient + longPeriodWeightCoefficient)
        );
    }

}
