package pro.crypto.indicators.uo;

import pro.crypto.exception.WrongIncomingParametersException;
import pro.crypto.helper.MathHelper;
import pro.crypto.model.Indicator;
import pro.crypto.model.IndicatorType;
import pro.crypto.model.request.UORequest;
import pro.crypto.model.result.UOResult;
import pro.crypto.model.tick.Tick;

import java.math.BigDecimal;

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
        BigDecimal[] buyingPressureValues = countBuyingPressureValues();
        BigDecimal[] trueRangeValues = countTrueRangeValues();
        countUltimateOscillatorValues(buyingPressureValues, trueRangeValues);
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

    private BigDecimal[] countBuyingPressureValues() {
        BigDecimal[] buyingPressureValues = new BigDecimal[originalData.length];
        for (int currentIndex = 1; currentIndex < buyingPressureValues.length; currentIndex++) {
            buyingPressureValues[currentIndex] = countBuyingPressureValue(currentIndex);
        }
        return buyingPressureValues;
    }

    private BigDecimal countBuyingPressureValue(int currentIndex) {
        BigDecimal trueLength = countTrueLength(currentIndex);
        return originalData[currentIndex].getClose().subtract(trueLength);
    }

    private BigDecimal countTrueLength(int currentIndex) {
        return MathHelper.min(originalData[currentIndex].getLow(), originalData[currentIndex - 1].getClose());
    }

    private BigDecimal[] countTrueRangeValues() {
        BigDecimal[] trueRangeValues = new BigDecimal[originalData.length];
        for (int currentIndex = 1; currentIndex < trueRangeValues.length; currentIndex++) {
            trueRangeValues[currentIndex] = countTrueRange(currentIndex);
        }
        return trueRangeValues;
    }

    private BigDecimal countTrueRange(int currentIndex) {
        BigDecimal firstRange = originalData[currentIndex].getHigh().subtract(originalData[currentIndex].getLow());
        BigDecimal secondRange = originalData[currentIndex].getHigh().subtract(originalData[currentIndex - 1].getClose());
        BigDecimal thirdRange = originalData[currentIndex].getClose().subtract(originalData[currentIndex].getLow());
        return MathHelper.max(MathHelper.max(firstRange, secondRange), thirdRange);
    }

    private void countUltimateOscillatorValues(BigDecimal[] buyingPressureValues, BigDecimal[] trueRangeValues) {
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
            result[currentPeriod] = countUltimateOscillator(buyingPressureValues, trueRangeValues, currentPeriod);
        }
    }

    private UOResult countUltimateOscillator(BigDecimal[] buyingPressureValues, BigDecimal[] trueRangeValues, int currentPeriod) {
        BigDecimal shortQuotient = countShortQuotient(buyingPressureValues, trueRangeValues, currentPeriod);
        BigDecimal middleQuotient = countMiddleQuotient(buyingPressureValues, trueRangeValues, currentPeriod);
        BigDecimal longQuotient = countLongQuotient(buyingPressureValues, trueRangeValues, currentPeriod);
        if (isNull(shortQuotient) || isNull(middleQuotient) || isNull(longQuotient)) {
            return new UOResult(originalData[currentPeriod].getTickTime(), null);
        }
        return countUltimateOscillatorValue(shortQuotient, middleQuotient, longQuotient, currentPeriod);
    }

    private BigDecimal countShortQuotient(BigDecimal[] buyingPressureValues, BigDecimal[] trueRangeValues, int currentPeriod) {
        BigDecimal shortBuyingPressureSum = countValuesSum(buyingPressureValues, currentPeriod, shortPeriod);
        BigDecimal shortTrueRangeSum = countValuesSum(trueRangeValues, currentPeriod, shortPeriod);
        return MathHelper.divide(shortBuyingPressureSum, shortTrueRangeSum);
    }

    private BigDecimal countMiddleQuotient(BigDecimal[] buyingPressureValues, BigDecimal[] trueRangeValues, int currentPeriod) {
        BigDecimal shortBuyingPressureSum = countValuesSum(buyingPressureValues, currentPeriod, middlePeriod);
        BigDecimal shortTrueRangeSum = countValuesSum(trueRangeValues, currentPeriod, middlePeriod);
        return MathHelper.divide(shortBuyingPressureSum, shortTrueRangeSum);
    }

    private BigDecimal countLongQuotient(BigDecimal[] buyingPressureValues, BigDecimal[] trueRangeValues, int currentPeriod) {
        BigDecimal shortBuyingPressureSum = countValuesSum(buyingPressureValues, currentPeriod, longPeriod);
        BigDecimal shortTrueRangeSum = countValuesSum(trueRangeValues, currentPeriod, longPeriod);
        return MathHelper.divide(shortBuyingPressureSum, shortTrueRangeSum);
    }

    private BigDecimal countValuesSum(BigDecimal[] values, int currentIndex, int period) {
        BigDecimal sum = BigDecimal.ZERO;
        for (int i = currentIndex - period + 1; i <= currentIndex; i++) {
            sum = sum.add(values[i]);
        }
        return sum;
    }

    private UOResult countUltimateOscillatorValue(BigDecimal shortQuotient, BigDecimal middleQuotient, BigDecimal longQuotient, int currentPeriod) {
        BigDecimal rawValue = countUltimateOscillatorRawValue(shortQuotient, middleQuotient, longQuotient);
        return isNull(rawValue)
                ? new UOResult(originalData[currentPeriod].getTickTime(), null)
                : new UOResult(originalData[currentPeriod].getTickTime(), rawValue.multiply(new BigDecimal(100)));
    }

    private BigDecimal countUltimateOscillatorRawValue(BigDecimal shortQuotient, BigDecimal middleQuotient, BigDecimal longQuotient) {
        final int shortPeriodWeightCoefficient = 4;
        final int middlePeriodWeightCoefficient = 2;
        final int longPeriodWeightCoefficient = 1;
        return MathHelper.divide(
                shortQuotient.multiply(new BigDecimal(shortPeriodWeightCoefficient))
                        .add(middleQuotient.multiply(new BigDecimal(middlePeriodWeightCoefficient)))
                        .add(longQuotient.multiply(new BigDecimal(longPeriodWeightCoefficient))),
                new BigDecimal(shortPeriodWeightCoefficient + middlePeriodWeightCoefficient + longPeriodWeightCoefficient)
        );
    }

}
