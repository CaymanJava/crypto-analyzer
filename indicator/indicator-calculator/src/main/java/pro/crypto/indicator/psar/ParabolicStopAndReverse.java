package pro.crypto.indicator.psar;

import pro.crypto.exception.WrongIncomingParametersException;
import pro.crypto.helper.MathHelper;
import pro.crypto.model.Indicator;
import pro.crypto.model.indicator.IndicatorType;
import pro.crypto.model.tick.Tick;
import pro.crypto.request.IndicatorRequest;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.stream.IntStream;

import static java.lang.String.format;
import static java.util.Objects.isNull;
import static pro.crypto.helper.MathHelper.scaleAndRound;
import static pro.crypto.model.indicator.IndicatorType.PARABOLIC_STOP_AND_REVERSE;

public class ParabolicStopAndReverse implements Indicator<PSARResult> {

    private final Tick[] originalData;
    private final BigDecimal minAccelerationFactor;
    private final BigDecimal maxAccelerationFactor;
    private final BigDecimal deltaAccelerationFactor;

    private Integer[] trendIndexes;
    private BigDecimal[] extremePoints;
    private BigDecimal[] accelerationFactors;
    private BigDecimal[] tentativeSARValues;
    private PSARResult[] result;

    public ParabolicStopAndReverse(IndicatorRequest creationRequest) {
        PSARRequest request = (PSARRequest) creationRequest;
        this.originalData = request.getOriginalData();
        this.minAccelerationFactor = new BigDecimal(request.getMinAccelerationFactor()).setScale(3, RoundingMode.HALF_UP);
        this.maxAccelerationFactor = new BigDecimal(request.getMaxAccelerationFactor()).setScale(3, RoundingMode.HALF_UP);
        this.deltaAccelerationFactor = new BigDecimal(0.02).setScale(2, RoundingMode.HALF_UP);
        checkIncomingData();
        initAuxiliaryArrays();
    }

    @Override
    public IndicatorType getType() {
        return PARABOLIC_STOP_AND_REVERSE;
    }

    @Override
    public void calculate() {
        result = new PSARResult[originalData.length];
        calculateParabolicStopAndReverse();
    }

    @Override
    public PSARResult[] getResult() {
        if (isNull(result)) {
            calculate();
        }
        return result;
    }

    private void checkIncomingData() {
        checkOriginalData(originalData);
        checkAccelerationFactors();
    }

    private void checkAccelerationFactors() {
        if (minAccelerationFactor.compareTo(BigDecimal.ZERO) <= 0) {
            throw new WrongIncomingParametersException(format("Min Acceleration Factor should be less more than zero" +
                            " {indicator: {%s}, minAccelerationFactor: {%s}}",
                    getType().toString(), minAccelerationFactor.toString()));
        }

        if (maxAccelerationFactor.compareTo(BigDecimal.ZERO) <= 0) {
            throw new WrongIncomingParametersException(format("Max Acceleration Factor should be less more than zero" +
                            " {indicator: {%s}, minAccelerationFactor: {%s}}",
                    getType().toString(), maxAccelerationFactor.toString()));
        }

        if (minAccelerationFactor.compareTo(maxAccelerationFactor) >= 0) {
            throw new WrongIncomingParametersException(format("Min Acceleration Factor should be less than Max Acceleration Factor" +
                            " {indicator: {%s}, minAccelerationFactor: {%s}, maxAccelerationFactor: {%s}}",
                    getType().toString(), minAccelerationFactor.toString(), maxAccelerationFactor.toString()));
        }
    }

    private void initAuxiliaryArrays() {
        trendIndexes = new Integer[originalData.length];
        extremePoints = new BigDecimal[originalData.length];
        accelerationFactors = new BigDecimal[originalData.length];
        tentativeSARValues = new BigDecimal[originalData.length];
    }

    private void calculateParabolicStopAndReverse() {
        fillInInitialValues();
        IntStream.range(2, result.length)
                .forEach(this::setPSARResult);
    }

    private void fillInInitialValues() {
        trendIndexes[1] = -1;
        extremePoints[1] = originalData[1].getLow();
        accelerationFactors[1] = minAccelerationFactor;
        result[0] = new PSARResult(originalData[0].getTickTime(), null);
        result[1] = new PSARResult(originalData[1].getTickTime(), originalData[0].getHigh());
    }

    private void setPSARResult(int currentIndex) {
        result[currentIndex] = calculateParabolicStopAndReverseValue(currentIndex);
    }

    private PSARResult calculateParabolicStopAndReverseValue(int currentIndex) {
        BigDecimal temporarySAR = calculateTemporarySAR(currentIndex);
        calculateTentativeSAR(temporarySAR, currentIndex);
        calculateTrendIndex(currentIndex);
        calculateExtremePoint(currentIndex);
        calculateAccelerationFactor(currentIndex);
        return new PSARResult(originalData[currentIndex].getTickTime(), calculateParabolicSAR(currentIndex));
    }

    private BigDecimal calculateTemporarySAR(int currentIndex) {
        return scaleAndRound(extremePoints[currentIndex - 1].subtract(result[currentIndex - 1].getIndicatorValue())
                .multiply(accelerationFactors[currentIndex - 1])
                .add(result[currentIndex - 1].getIndicatorValue()));
    }

    private void calculateTentativeSAR(BigDecimal temporarySAR, int currentIndex) {
        tentativeSARValues[currentIndex] = trendIndexes[currentIndex - 1] < 0
                ? calculateNegativeTentativeSAR(temporarySAR, currentIndex)
                : calculatePositiveTentativeSAR(temporarySAR, currentIndex);
    }

    private BigDecimal calculatePositiveTentativeSAR(BigDecimal temporarySAR, int currentIndex) {
        return MathHelper.min(
                temporarySAR,
                originalData[currentIndex - 1].getLow(),
                originalData[currentIndex - 2].getLow());
    }

    private BigDecimal calculateNegativeTentativeSAR(BigDecimal temporarySAR, int currentIndex) {
        return MathHelper.max(
                temporarySAR,
                originalData[currentIndex - 1].getHigh(),
                originalData[currentIndex - 2].getHigh());
    }

    private void calculateTrendIndex(int currentIndex) {
        if (isDowntrend(currentIndex - 1)) {
            calculateDowntrendIndex(currentIndex);
        } else {
            calculateUptrendIndex(currentIndex);
        }
    }

    private void calculateDowntrendIndex(int currentIndex) {
        if (isDowntrendEnded(currentIndex)) {
            trendIndexes[currentIndex] = 1;
        } else {
            trendIndexes[currentIndex] = trendIndexes[currentIndex - 1] - 1;
        }
    }

    private boolean isDowntrendEnded(int index) {
        return tentativeSARValues[index].compareTo(originalData[index].getHigh()) < 0;
    }

    private void calculateUptrendIndex(int currentIndex) {
        if (isUptrendEnded(currentIndex)) {
            trendIndexes[currentIndex] = -1;
        } else {
            trendIndexes[currentIndex] = trendIndexes[currentIndex - 1] + 1;
        }
    }

    private boolean isUptrendEnded(int index) {
        return tentativeSARValues[index].compareTo(originalData[index].getLow()) > 0;
    }

    private void calculateExtremePoint(int currentIndex) {
        if (isDowntrend(currentIndex)) {
            calculateExtremePointForDowntrend(currentIndex);
        } else {
            calculateExtremePointForUptrend(currentIndex);
        }
    }

    private boolean isDowntrend(int index) {
        return trendIndexes[index] < 0;
    }

    private void calculateExtremePointForDowntrend(int currentIndex) {
        if (isDowntrendJustStarted(currentIndex)) {
            extremePoints[currentIndex] = originalData[currentIndex].getLow();
        } else {
            extremePoints[currentIndex] = MathHelper.min(originalData[currentIndex].getLow(), extremePoints[currentIndex - 1]);
        }
    }

    private void calculateExtremePointForUptrend(int currentIndex) {
        if (isUptrendJustStarted(currentIndex)) {
            extremePoints[currentIndex] = originalData[currentIndex].getHigh();
        } else {
            extremePoints[currentIndex] = MathHelper.max(originalData[currentIndex].getHigh(), extremePoints[currentIndex - 1]);
        }
    }

    private void calculateAccelerationFactor(int currentIndex) {
        if (isTrendJustStarted(currentIndex)) {
            accelerationFactors[currentIndex] = minAccelerationFactor;
        } else {
            calculateAccelerationFactorValue(currentIndex);
        }
    }

    private void calculateAccelerationFactorValue(int currentIndex) {
        if (isExtremePointChanged(currentIndex)) {
            accelerationFactors[currentIndex] = MathHelper.min(accelerationFactors[currentIndex - 1].add(deltaAccelerationFactor), maxAccelerationFactor);
        } else {
            accelerationFactors[currentIndex] = accelerationFactors[currentIndex - 1];
        }
    }

    private boolean isExtremePointChanged(int currentIndex) {
        return extremePoints[currentIndex].compareTo(extremePoints[currentIndex - 1]) != 0;
    }

    private boolean isTrendJustStarted(int index) {
        return Math.abs(trendIndexes[index]) == 1;
    }

    private BigDecimal calculateParabolicSAR(int currentIndex) {
        if (isDowntrendJustStarted(currentIndex)) {
            return calculateDowntrendParabolic(currentIndex);
        }
        if (isUptrendJustStarted(currentIndex)) {
            return calculateUpParabolic(currentIndex);
        }
        return scaleAndRound(tentativeSARValues[currentIndex]);
    }

    private boolean isDowntrendJustStarted(int index) {
        return trendIndexes[index] == -1;
    }

    private BigDecimal calculateDowntrendParabolic(int currentIndex) {
        return MathHelper.max(extremePoints[currentIndex - 1], originalData[currentIndex].getHigh());
    }

    private boolean isUptrendJustStarted(int index) {
        return trendIndexes[index] == 1;
    }

    private BigDecimal calculateUpParabolic(int currentIndex) {
        return MathHelper.min(extremePoints[currentIndex - 1], originalData[currentIndex].getLow());
    }

}
