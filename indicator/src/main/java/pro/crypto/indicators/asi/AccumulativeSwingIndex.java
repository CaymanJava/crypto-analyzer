package pro.crypto.indicators.asi;

import pro.crypto.exception.WrongIncomingParametersException;
import pro.crypto.helper.MathHelper;
import pro.crypto.model.Indicator;
import pro.crypto.model.IndicatorType;
import pro.crypto.model.request.ASIRequest;
import pro.crypto.model.result.ASIResult;
import pro.crypto.model.tick.Tick;

import java.math.BigDecimal;

import static java.lang.String.format;
import static java.util.Locale.ENGLISH;
import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;
import static pro.crypto.model.IndicatorType.ACCUMULATIVE_SWING_INDEX;

public class AccumulativeSwingIndex implements Indicator<ASIResult> {

    private final Tick[] originalData;
    private final double limitMoveValue;

    private ASIResult[] result;

    public AccumulativeSwingIndex(ASIRequest request) {
        this.originalData = request.getOriginalData();
        this.limitMoveValue = request.getLimitMoveValue();
        checkIncomingData();
    }

    @Override
    public IndicatorType getType() {
        return ACCUMULATIVE_SWING_INDEX;
    }

    @Override
    public void calculate() {
        result = new ASIResult[originalData.length];
        BigDecimal[] swingIndexes = calculateSwingIndexes();
        calculateAccumulativeSwingIndexResult(swingIndexes);
    }

    @Override
    public ASIResult[] getResult() {
        if (isNull(result)) {
            calculate();
        }
        return result;
    }

    private void checkIncomingData() {
        checkOriginalData(originalData);
        checkLimitMoveValue();
    }

    private void checkLimitMoveValue() {
        if (limitMoveValue <= 0) {
            throw new WrongIncomingParametersException(format(ENGLISH, "Limit move value should be more than 0 {indicator: {%s}, shift: {%.2f}}",
                    getType().toString(), limitMoveValue));
        }
    }

    private BigDecimal[] calculateSwingIndexes() {
        BigDecimal[] kCoefficients = calculateKCoefficients();
        BigDecimal[] rCoefficients = calculateRCoefficients();
        return calculateSwingIndexes(kCoefficients, rCoefficients);
    }

    private BigDecimal[] calculateKCoefficients() {
        BigDecimal[] kCoefficients = new BigDecimal[originalData.length];
        for (int currentIndex = 1; currentIndex < kCoefficients.length; currentIndex++) {
            kCoefficients[currentIndex] = calculateKCoefficient(currentIndex);
        }
        return kCoefficients;
    }

    private BigDecimal calculateKCoefficient(int currentIndex) {
        return MathHelper.max(calculateFirstParameter(currentIndex), calculateSecondParameter(currentIndex));
    }

    private BigDecimal[] calculateRCoefficients() {
        BigDecimal[] rCoefficients = new BigDecimal[originalData.length];
        for (int currentIndex = 1; currentIndex < rCoefficients.length; currentIndex++) {
            rCoefficients[currentIndex] = calculateRCoefficient(currentIndex);
        }
        return rCoefficients;
    }

    private BigDecimal calculateRCoefficient(int currentIndex) {
        BigDecimal firstParameter = calculateFirstParameter(currentIndex);
        BigDecimal secondParameter = calculateSecondParameter(currentIndex);
        BigDecimal thirdParameter = calculateThirdParameter(currentIndex);
        BigDecimal fourthParameter = calculateFourthParameter(currentIndex);
        return calculateRCoefficient(firstParameter, secondParameter, thirdParameter, fourthParameter);
    }

    private BigDecimal calculateRCoefficient(BigDecimal firstParameter, BigDecimal secondParameter,
                                             BigDecimal thirdParameter, BigDecimal fourthParameter) {
        if (firstParameter.compareTo(secondParameter) > 0) {
            if (firstParameter.compareTo(thirdParameter) > 0) {
                return calculateRForFirstParameter(firstParameter, secondParameter, fourthParameter);
            }
        } else if (secondParameter.compareTo(thirdParameter) > 0) {
            return calculateRForSecondParameter(firstParameter, secondParameter, fourthParameter);
        }
        return calculateRForThirdParameter(thirdParameter, fourthParameter);
    }

    // |H(i) - C(i-1)|
    private BigDecimal calculateFirstParameter(int currentIndex) {
        return originalData[currentIndex].getHigh().subtract(originalData[currentIndex - 1].getClose()).abs();
    }

    // |L(i) - C(i-1)|
    private BigDecimal calculateSecondParameter(int currentIndex) {
        return originalData[currentIndex].getLow().subtract(originalData[currentIndex - 1].getClose()).abs();
    }

    // |H(i) - L(i)|
    private BigDecimal calculateThirdParameter(int currentIndex) {
        return originalData[currentIndex].getHigh().subtract(originalData[currentIndex].getLow()).abs();
    }

    // |C(i - 1) - O(i - 1)|
    private BigDecimal calculateFourthParameter(int currentIndex) {
        return originalData[currentIndex - 1].getClose().subtract(originalData[currentIndex - 1].getOpen()).abs();
    }

    // |H(i) - C(i-1)| - 0.5 * |L(i) - C(i-1)| + 0.25 * |C(i - 1) - O(i - 1)|
    private BigDecimal calculateRForFirstParameter(BigDecimal firstParameter, BigDecimal secondParameter, BigDecimal fourthParameter) {
        return firstParameter
                .subtract(new BigDecimal(0.5).multiply(secondParameter))
                .add(new BigDecimal(0.25).multiply(fourthParameter));
    }

    // |L(i) - C(i-1)| - 0.5 * |H(i) - C(i-1)| + 0.25 * |C(i - 1) - O(i - 1)|
    private BigDecimal calculateRForSecondParameter(BigDecimal firstParameter, BigDecimal secondParameter, BigDecimal fourthParameter) {
        return secondParameter
                .subtract(new BigDecimal(0.5).multiply(firstParameter))
                .add(new BigDecimal(0.25).multiply(fourthParameter));
    }

    // |H(i) - L(i)| + 0.25 * |C(i - 1) - O(i - 1)|
    private BigDecimal calculateRForThirdParameter(BigDecimal thirdParameter, BigDecimal fourthParameter) {
        return thirdParameter
                .add(new BigDecimal(0.25).multiply(fourthParameter));
    }

    private BigDecimal[] calculateSwingIndexes(BigDecimal[] kCoefficients, BigDecimal[] rCoefficients) {
        BigDecimal[] swingIndexes = new BigDecimal[originalData.length];
        for (int currentIndex = 1; currentIndex < swingIndexes.length; currentIndex++) {
            swingIndexes[currentIndex] = calculateSwingIndex(currentIndex,
                    kCoefficients[currentIndex], rCoefficients[currentIndex]);
        }
        return swingIndexes;
    }

    // 50 * ( (C(i) - C(i-1) + 0.5 * (C(i) - O(i)) + 0.25 * (C(i-1) - O(i-1)) / R) * (K / L)
    private BigDecimal calculateSwingIndex(int currentIndex, BigDecimal kCoefficient, BigDecimal rCoefficient) {
        BigDecimal quotient = MathHelper.divide(calculateDivisible(currentIndex), rCoefficient);
        return nonNull(quotient)
                ? MathHelper.divide(quotient.multiply(kCoefficient), new BigDecimal(limitMoveValue))
                : null;
    }

    private BigDecimal calculateDivisible(int currentIndex) {
        return new BigDecimal(50)
                        .multiply(originalData[currentIndex].getClose().subtract(originalData[currentIndex - 1].getClose())
                                .add(new BigDecimal(0.5).multiply(originalData[currentIndex].getClose().subtract(originalData[currentIndex].getOpen())))
                                .add(new BigDecimal(0.25).multiply(originalData[currentIndex - 1].getClose().subtract(originalData[currentIndex - 1].getOpen()))));
    }

    private void calculateAccumulativeSwingIndexResult(BigDecimal[] swingIndexes) {
        fillInInitialValues(swingIndexes[1]);
        for (int currentIndex = 2; currentIndex < result.length; currentIndex++) {
            result[currentIndex] = new ASIResult(
                    originalData[currentIndex].getTickTime(),
                    calculateAccumulativeSwingIndex(swingIndexes[currentIndex], currentIndex));
        }
    }

    private BigDecimal calculateAccumulativeSwingIndex(BigDecimal swingIndex, int currentIndex) {
        return MathHelper.scaleAndRound(swingIndex.add(result[currentIndex - 1].getIndicatorValue()));
    }

    private void fillInInitialValues(BigDecimal firstSwingIndex) {
        result[0] = new ASIResult(originalData[0].getTickTime(), null);
        result[1] = new ASIResult(originalData[1].getTickTime(), firstSwingIndex);
    }

}
