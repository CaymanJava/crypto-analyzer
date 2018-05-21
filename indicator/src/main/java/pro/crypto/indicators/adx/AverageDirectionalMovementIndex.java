package pro.crypto.indicators.adx;

import pro.crypto.helper.*;
import pro.crypto.indicators.ma.MovingAverageFactory;
import pro.crypto.model.Indicator;
import pro.crypto.model.IndicatorType;
import pro.crypto.model.request.ADXRequest;
import pro.crypto.model.request.MARequest;
import pro.crypto.model.result.ADXResult;
import pro.crypto.model.tick.Tick;

import java.math.BigDecimal;

import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;
import static pro.crypto.model.IndicatorType.AVERAGE_DIRECTIONAL_MOVEMENT_INDEX;
import static pro.crypto.model.IndicatorType.EXPONENTIAL_MOVING_AVERAGE;
import static pro.crypto.model.tick.PriceType.CLOSE;

public class AverageDirectionalMovementIndex implements Indicator<ADXResult> {

    private final Tick[] originalData;
    private final int period;

    private ADXResult[] result;

    public AverageDirectionalMovementIndex(ADXRequest request) {
        this.originalData = request.getOriginalData();
        this.period = request.getPeriod();
        checkIncomingData();
    }

    @Override
    public IndicatorType getType() {
        return AVERAGE_DIRECTIONAL_MOVEMENT_INDEX;
    }

    @Override
    public void calculate() {
        result = new ADXResult[originalData.length];
        BigDecimalTuple[] directionalIndicators = calculateDirectionalIndicators();
        BigDecimal[] averageDirectionalIndexes = calculateAverageDirectionalIndexes(directionalIndicators);
        buildAverageDirectionalMovementIndex(directionalIndicators, averageDirectionalIndexes);
    }

    @Override
    public ADXResult[] getResult() {
        if (isNull(result)) {
            calculate();
        }
        return result;
    }

    private void checkIncomingData() {
        checkOriginalData(originalData);
        checkOriginalDataSize(originalData, period * 2);
        checkPeriod(period);
    }

    private BigDecimalTuple[] calculateDirectionalIndicators() {
        BigDecimal[] upMovementValues = calculateUpMovement();
        BigDecimal[] downMovementValues = calculateDownMovement();
        return calculateDirectionalIndicatorsValues(upMovementValues, downMovementValues);
    }

    private BigDecimal[] calculateUpMovement() {
        BigDecimal[] upMovementValues = new BigDecimal[originalData.length];
        upMovementValues[0] = BigDecimal.ZERO;
        for (int currentIndex = 1; currentIndex < upMovementValues.length; currentIndex++) {
            upMovementValues[currentIndex] = originalData[currentIndex].getHigh().subtract(originalData[currentIndex - 1].getHigh());
        }
        return upMovementValues;
    }

    private BigDecimal[] calculateDownMovement() {
        BigDecimal[] downMovementValues = new BigDecimal[originalData.length];
        downMovementValues[0] = BigDecimal.ZERO;
        for (int currentIndex = 1; currentIndex < downMovementValues.length; currentIndex++) {
            downMovementValues[currentIndex] = originalData[currentIndex - 1].getLow().subtract(originalData[currentIndex].getLow());
        }
        return downMovementValues;
    }

    private BigDecimalTuple[] calculateDirectionalIndicatorsValues(BigDecimal[] upMovementValues, BigDecimal[] downMovementValues) {
        BigDecimal[] positiveDirectionalMovements = calculatePositiveDirectionalMovements(upMovementValues, downMovementValues);
        BigDecimal[] negativeDirectionalMovements = calculateNegativeDirectionalMovements(upMovementValues, downMovementValues);
        BigDecimal[] trueRanges = TrueRangeCalculator.calculate(originalData);
        BigDecimal[] positiveDirectionalIndicators = calculateDirectionalIndicators(positiveDirectionalMovements, trueRanges);
        BigDecimal[] negativeDirectionalIndicators = calculateDirectionalIndicators(negativeDirectionalMovements, trueRanges);
        return buildDirectionalIndicators(positiveDirectionalIndicators, negativeDirectionalIndicators);
    }

    private BigDecimal[] calculatePositiveDirectionalMovements(BigDecimal[] upMovementValues, BigDecimal[] downMovementValues) {
        BigDecimal[] positiveDirectionalMovements = new BigDecimal[originalData.length];
        for (int currentIndex = 0; currentIndex < positiveDirectionalMovements.length; currentIndex++) {
            positiveDirectionalMovements[currentIndex] =
                    calculatePositiveDirectionalMovement(upMovementValues[currentIndex], downMovementValues[currentIndex]);
        }
        return positiveDirectionalMovements;
    }

    private BigDecimal calculatePositiveDirectionalMovement(BigDecimal upMovementValue, BigDecimal downMovementValue) {
        return upMovementValue.compareTo(downMovementValue) > 0 && upMovementValue.compareTo(BigDecimal.ZERO) > 0
                ? upMovementValue
                : BigDecimal.ZERO;
    }

    private BigDecimal[] calculateNegativeDirectionalMovements(BigDecimal[] upMovementValues, BigDecimal[] downMovementValues) {
        BigDecimal[] negativeDirectionalMovements = new BigDecimal[originalData.length];
        for (int currentIndex = 0; currentIndex < negativeDirectionalMovements.length; currentIndex++) {
            negativeDirectionalMovements[currentIndex] =
                    calculateNegativeDirectionalMovement(upMovementValues[currentIndex], downMovementValues[currentIndex]);
        }
        return negativeDirectionalMovements;
    }

    private BigDecimal calculateNegativeDirectionalMovement(BigDecimal upMovementValue, BigDecimal downMovementValue) {
        return downMovementValue.compareTo(upMovementValue) > 0 && downMovementValue.compareTo(BigDecimal.ZERO) > 0
                ? downMovementValue
                : BigDecimal.ZERO;
    }

    private BigDecimal[] calculateDirectionalIndicators(BigDecimal[] positiveDirectionalMovements, BigDecimal[] trueRanges) {
        BigDecimal[] ratios = calculateRatios(positiveDirectionalMovements, trueRanges);
        return IndicatorResultExtractor.extract(MovingAverageFactory.create(buildMARequest(ratios)).getResult());
    }

    private BigDecimal[] calculateRatios(BigDecimal[] directionalMovements, BigDecimal[] trueRanges) {
        BigDecimal[] ratios = new BigDecimal[originalData.length];
        for (int i = 0; i < ratios.length; i++) {
            ratios[i] = MathHelper.divide(directionalMovements[i], trueRanges[i]);
        }
        return ratios;
    }

    private BigDecimalTuple[] buildDirectionalIndicators(BigDecimal[] positiveDirectionalIndicators, BigDecimal[] negativeDirectionalIndicators) {
        BigDecimalTuple[] directionalIndicators = new BigDecimalTuple[originalData.length];
        for (int i = 0; i < directionalIndicators.length; i++) {
            directionalIndicators[i] = new BigDecimalTuple(
                    multiplyByOneHundred(positiveDirectionalIndicators[i]),
                    multiplyByOneHundred(negativeDirectionalIndicators[i])
            );
        }
        return directionalIndicators;
    }

    private BigDecimal[] calculateAverageDirectionalIndexes(BigDecimalTuple[] directionalIndicators) {
        BigDecimal[] directionalMovementIndexValues = calculateDirectionalMovementIndexValues(directionalIndicators);
        return IndicatorResultExtractor.extract(MovingAverageFactory.create(buildMARequest(directionalMovementIndexValues)).getResult());
    }

    private BigDecimal[] calculateDirectionalMovementIndexValues(BigDecimalTuple[] directionalIndicators) {
        BigDecimal[] directionalMovementIndexValues = new BigDecimal[originalData.length];
        for (int currentIndex = 0; currentIndex < directionalMovementIndexValues.length; currentIndex++) {
            directionalMovementIndexValues[currentIndex] = calculateDirectionalMovementIndex(directionalIndicators[currentIndex]);
        }
        return directionalMovementIndexValues;
    }

    private BigDecimal calculateDirectionalMovementIndex(BigDecimalTuple directionalIndicator) {
        return nonNull(directionalIndicator.getLeft()) && nonNull(directionalIndicator.getRight())
                ? calculateDirectionalMovementIndexValue(directionalIndicator.getLeft(), directionalIndicator.getRight())
                : null;
    }

    // DXI = (|+DI - -DI| / (+DI + -DI)) * 100
    private BigDecimal calculateDirectionalMovementIndexValue(BigDecimal positiveDirectionalIndex, BigDecimal negativeDirectionalIndex) {
        BigDecimal directMovementCoefficient = MathHelper.divide(
                positiveDirectionalIndex.subtract(negativeDirectionalIndex).abs(),
                positiveDirectionalIndex.add(negativeDirectionalIndex));
        return multiplyByOneHundred(directMovementCoefficient);
    }

    private BigDecimal multiplyByOneHundred(BigDecimal directionalIndicator) {
        return nonNull(directionalIndicator)
                ? directionalIndicator.multiply(new BigDecimal(100))
                : null;
    }

    private MARequest buildMARequest(BigDecimal[] ratios) {
        return MARequest.builder()
                .originalData(FakeTicksCreator.createWithCloseOnly(ratios))
                .indicatorType(EXPONENTIAL_MOVING_AVERAGE)
                .period(period)
                .priceType(CLOSE)
                .build();
    }

    private void buildAverageDirectionalMovementIndex(BigDecimalTuple[] directionalIndicators, BigDecimal[] averageDirectionalIndexes) {
        for (int currentIndex = 0; currentIndex < result.length; currentIndex++) {
            result[currentIndex] = new ADXResult(
                    originalData[currentIndex].getTickTime(),
                    directionalIndicators[currentIndex].getLeft(),
                    directionalIndicators[currentIndex].getRight(),
                    nonNull(directionalIndicators[currentIndex].getLeft()) && nonNull(directionalIndicators[currentIndex].getRight())
                            ? averageDirectionalIndexes[currentIndex - period + 1]
                            : null
            );
        }
    }

}
