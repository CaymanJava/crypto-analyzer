package pro.crypto.indicator.rsi;

import pro.crypto.exception.WrongIncomingParametersException;
import pro.crypto.helper.MathHelper;
import pro.crypto.model.Indicator;
import pro.crypto.model.indicator.IndicatorType;
import pro.crypto.model.tick.Tick;
import pro.crypto.request.IndicatorRequest;

import java.math.BigDecimal;
import java.util.stream.IntStream;

import static java.lang.String.format;
import static java.math.BigDecimal.ONE;
import static java.math.BigDecimal.ZERO;
import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;
import static pro.crypto.model.indicator.IndicatorType.LAGUERRE_RELATIVE_STRENGTH_INDEX;

public class LaguerreRelativeStrengthIndex implements Indicator<RSIResult> {

    private final Tick[] originalData;
    private final double gamma;

    private BigDecimal[] zeroLagValues;
    private BigDecimal[] firstLagValues;
    private BigDecimal[] secondLagValues;
    private BigDecimal[] thirdLagValues;
    private BigDecimal[] closesUpValues;
    private BigDecimal[] closesDownValues;
    private RSIResult[] result;

    public LaguerreRelativeStrengthIndex(IndicatorRequest indicatorRequest) {
        LRSIRequest request = (LRSIRequest) indicatorRequest;
        this.originalData = request.getOriginalData();
        this.gamma = request.getGamma();
        checkIncomingData();
    }

    @Override
    public IndicatorType getType() {
        return LAGUERRE_RELATIVE_STRENGTH_INDEX;
    }

    @Override
    public void calculate() {
        initLagValues();
        initUpDownValues();
        calculateLagValues();
        calculateClosesUpDownValues();
        calculateLaguerreRelativeStrengthIndexResult();
    }

    @Override
    public RSIResult[] getResult() {
        if (isNull(result)) {
            calculate();
        }
        return result;
    }

    private void checkIncomingData() {
        checkOriginalData(originalData);
        checkGamma(gamma);
    }

    private void checkGamma(double gamma) {
        if (gamma <= 0 || gamma >= 1) {
            throw new WrongIncomingParametersException(format("Parameter gamma should be between 0 and 1 {indicator: {%s}}", getType().toString()));
        }
    }

    private void initLagValues() {
        zeroLagValues = initLagArray();
        firstLagValues = initLagArray();
        secondLagValues = initLagArray();
        thirdLagValues = initLagArray();
    }

    private BigDecimal[] initLagArray() {
        BigDecimal[] lagArray = new BigDecimal[originalData.length];
        lagArray[0] = ZERO;
        return lagArray;
    }


    private void initUpDownValues() {
        closesUpValues = new BigDecimal[originalData.length];
        closesDownValues = new BigDecimal[originalData.length];
    }

    private void calculateLagValues() {
        IntStream.range(1, originalData.length)
                .forEach(this::calculateLagValues);
    }

    private void calculateLagValues(int currentIndex) {
        zeroLagValues[currentIndex] = calculateZeroLagValue(currentIndex);
        firstLagValues[currentIndex] = calculateFirstLagValue(currentIndex);
        secondLagValues[currentIndex] = calculateSecondLagValue(currentIndex);
        thirdLagValues[currentIndex] = calculateThirdLagValue(currentIndex);
    }

    // L0[i] = (1 - gamma) * Close[i] + gamma * L0[i - 1]
    private BigDecimal calculateZeroLagValue(int currentIndex) {
        return ONE.subtract(new BigDecimal(gamma))
                .multiply(originalData[currentIndex].getClose())
                .add(new BigDecimal(gamma).multiply(zeroLagValues[currentIndex - 1]));
    }

    // L1[i] = - gamma * L0[i] + L0[i - 1] + gamma * L1[i - 1]
    private BigDecimal calculateFirstLagValue(int currentIndex) {
        return new BigDecimal(gamma).negate().multiply(zeroLagValues[currentIndex])
                .add(zeroLagValues[currentIndex - 1])
                .add(new BigDecimal(gamma).multiply(firstLagValues[currentIndex - 1]));
    }

    // L2[i] = - gamma * L1[i] + L1[i - 1] + gamma * L2[i - 1]
    private BigDecimal calculateSecondLagValue(int currentIndex) {
        return new BigDecimal(gamma).negate().multiply(firstLagValues[currentIndex])
                .add(firstLagValues[currentIndex - 1])
                .add(new BigDecimal(gamma).multiply(secondLagValues[currentIndex - 1]));
    }

    // L3[i] = - gamma * L2[i] + L2[i - 1] + gamma * L3[i - 1]
    private BigDecimal calculateThirdLagValue(int currentIndex) {
        return new BigDecimal(gamma).negate().multiply(secondLagValues[currentIndex])
                .add(secondLagValues[currentIndex - 1])
                .add(new BigDecimal(gamma).multiply(thirdLagValues[currentIndex - 1]));
    }

    private void calculateClosesUpDownValues() {
        IntStream.range(0, originalData.length)
                .forEach(this::calculateClosesUpDownValues);
    }

    private void calculateClosesUpDownValues(int currentIndex) {
        if (allLagValuesEmpty(currentIndex)) {
            return;
        }

        computeFirstSmoothing(currentIndex);
        computeSecondSmoothing(currentIndex);
        computeThirdSmoothing(currentIndex);
    }

    private boolean allLagValuesEmpty(int currentIndex) {
        return isEmpty(zeroLagValues[currentIndex])
                && isEmpty(firstLagValues[currentIndex])
                && isEmpty(secondLagValues[currentIndex])
                && isEmpty(thirdLagValues[currentIndex]);
    }

    private boolean isEmpty(BigDecimal value) {
        return isNull(value) || value.compareTo(ZERO) == 0;
    }

    // L0 >= L1 ? CU = L0 - L1 : CD = L1 - L0
    private void computeFirstSmoothing(int currentIndex) {
        if (zeroLagValues[currentIndex].compareTo(firstLagValues[currentIndex]) >= 0) {
            closesUpValues[currentIndex] = zeroLagValues[currentIndex].subtract(firstLagValues[currentIndex]);
            closesDownValues[currentIndex] = ZERO;
        } else {
            closesUpValues[currentIndex] = ZERO;
            closesDownValues[currentIndex] = firstLagValues[currentIndex].subtract(zeroLagValues[currentIndex]);
        }
    }

    // L1 >= L2 ? CU = CU + L1 - L2 : CD = CD + L2 - L1
    private void computeSecondSmoothing(int currentIndex) {
        if (firstLagValues[currentIndex].compareTo(secondLagValues[currentIndex]) >= 0) {
            closesUpValues[currentIndex] = closesUpValues[currentIndex].add(firstLagValues[currentIndex]).subtract(secondLagValues[currentIndex]);
        } else {
            closesDownValues[currentIndex] = closesDownValues[currentIndex].add(secondLagValues[currentIndex]).subtract(firstLagValues[currentIndex]);
        }
    }

    // L2 >= L3 ? CU = CU + L2 - L3 : CD = CD + L3 - L2
    private void computeThirdSmoothing(int currentIndex) {
        if (secondLagValues[currentIndex].compareTo(thirdLagValues[currentIndex]) >= 0) {
            closesUpValues[currentIndex] = closesUpValues[currentIndex].add(secondLagValues[currentIndex]).subtract(thirdLagValues[currentIndex]);
        } else {
            closesDownValues[currentIndex] = closesDownValues[currentIndex].add(thirdLagValues[currentIndex]).subtract(secondLagValues[currentIndex]);
        }
    }

    private void calculateLaguerreRelativeStrengthIndexResult() {
        result = IntStream.range(0, originalData.length)
                .mapToObj(idx -> new RSIResult(
                        originalData[idx].getTickTime(),
                        calculateLaguerreRelativeStrengthIndex(idx)))
                .toArray(RSIResult[]::new);
    }

    private BigDecimal calculateLaguerreRelativeStrengthIndex(int currentIndex) {
        return isPossibleCalculateIndicator(currentIndex)
                ? calculateLaguerreRelativeStrengthIndexValue(currentIndex)
                : null;
    }

    private boolean isPossibleCalculateIndicator(int currentIndex) {
        return nonNull(closesUpValues[currentIndex]) && nonNull(closesDownValues[currentIndex]);
    }

    // LRSI = CU / (CU + CD)
    private BigDecimal calculateLaguerreRelativeStrengthIndexValue(int currentIndex) {
        return closesUpValues[currentIndex].add(closesDownValues[currentIndex]).compareTo(ZERO) != 0
                ? MathHelper.divide(closesUpValues[currentIndex], closesUpValues[currentIndex].add(closesDownValues[currentIndex]))
                : ZERO;
    }

}
