package pro.crypto.indicator.adx;

import pro.crypto.helper.FakeTicksCreator;
import pro.crypto.helper.IndicatorResultExtractor;
import pro.crypto.helper.MathHelper;
import pro.crypto.helper.TrueRangeCalculator;
import pro.crypto.indicator.ma.MARequest;
import pro.crypto.indicator.ma.MovingAverageFactory;
import pro.crypto.model.BigDecimalTuple;
import pro.crypto.model.Indicator;
import pro.crypto.model.IndicatorRequest;
import pro.crypto.model.IndicatorType;
import pro.crypto.model.tick.Tick;

import java.math.BigDecimal;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.stream.IntStream;

import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;
import static pro.crypto.model.IndicatorType.AVERAGE_DIRECTIONAL_MOVEMENT_INDEX;
import static pro.crypto.model.IndicatorType.EXPONENTIAL_MOVING_AVERAGE;
import static pro.crypto.model.tick.PriceType.CLOSE;

public class AverageDirectionalMovementIndex implements Indicator<ADXResult> {

    private final Tick[] originalData;
    private final int period;

    private ADXResult[] result;

    public AverageDirectionalMovementIndex(IndicatorRequest creationRequest) {
        ADXRequest request = (ADXRequest) creationRequest;
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
        return calculateMovement(this::calculateUpMovement);
    }

    private BigDecimal calculateUpMovement(int currentIndex) {
        if (currentIndex == 0) {
            return BigDecimal.ZERO;
        }
        return originalData[currentIndex].getHigh().subtract(originalData[currentIndex - 1].getHigh());
    }

    private BigDecimal[] calculateDownMovement() {
        return calculateMovement(this::calculateDownMovement);
    }

    private BigDecimal calculateDownMovement(int currentIndex) {
        if (currentIndex == 0) {
            return BigDecimal.ZERO;
        }
        return originalData[currentIndex - 1].getLow().subtract(originalData[currentIndex].getLow());
    }

    private BigDecimal[] calculateMovement(Function<Integer, BigDecimal> movementFunction) {
        return IntStream.range(0, originalData.length)
                .mapToObj(movementFunction::apply)
                .toArray(BigDecimal[]::new);
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
        return calculateDirectionalMovements(upMovementValues, downMovementValues, this::calculatePositiveDirectionalMovement);
    }

    private BigDecimal calculatePositiveDirectionalMovement(BigDecimal upMovementValue, BigDecimal downMovementValue) {
        return upMovementValue.compareTo(downMovementValue) > 0 && upMovementValue.compareTo(BigDecimal.ZERO) > 0
                ? upMovementValue
                : BigDecimal.ZERO;
    }

    private BigDecimal[] calculateNegativeDirectionalMovements(BigDecimal[] upMovementValues, BigDecimal[] downMovementValues) {
        return calculateDirectionalMovements(upMovementValues, downMovementValues, this::calculateNegativeDirectionalMovement);
    }

    private BigDecimal calculateNegativeDirectionalMovement(BigDecimal upMovementValue, BigDecimal downMovementValue) {
        return downMovementValue.compareTo(upMovementValue) > 0 && downMovementValue.compareTo(BigDecimal.ZERO) > 0
                ? downMovementValue
                : BigDecimal.ZERO;
    }

    private BigDecimal[] calculateDirectionalMovements(BigDecimal[] upMovementValues, BigDecimal[] downMovementValues,
                                                       BiFunction<BigDecimal, BigDecimal, BigDecimal> directMovementFunction) {
        return IntStream.range(0, originalData.length)
                .mapToObj(idx -> directMovementFunction.apply(upMovementValues[idx], downMovementValues[idx]))
                .toArray(BigDecimal[]::new);
    }

    private BigDecimal[] calculateDirectionalIndicators(BigDecimal[] positiveDirectionalMovements, BigDecimal[] trueRanges) {
        BigDecimal[] ratios = calculateRatios(positiveDirectionalMovements, trueRanges);
        return IndicatorResultExtractor.extractIndicatorValues(MovingAverageFactory.create(buildMARequest(ratios)).getResult());
    }

    private BigDecimal[] calculateRatios(BigDecimal[] directionalMovements, BigDecimal[] trueRanges) {
        return IntStream.range(0, originalData.length)
                .mapToObj(idx -> MathHelper.divide(directionalMovements[idx], trueRanges[idx]))
                .toArray(BigDecimal[]::new);
    }

    private BigDecimalTuple[] buildDirectionalIndicators(BigDecimal[] positiveDirectionalIndicators, BigDecimal[] negativeDirectionalIndicators) {
        return IntStream.range(0, originalData.length)
                .mapToObj(idx -> buildTuple(positiveDirectionalIndicators[idx], negativeDirectionalIndicators[idx]))
                .toArray(BigDecimalTuple[]::new);
    }

    private BigDecimalTuple buildTuple(BigDecimal positiveDirectionalIndicator, BigDecimal negativeDirectionalIndicator) {
        return new BigDecimalTuple(multiplyByOneHundred(positiveDirectionalIndicator), multiplyByOneHundred(negativeDirectionalIndicator));
    }

    private BigDecimal[] calculateAverageDirectionalIndexes(BigDecimalTuple[] directionalIndicators) {
        BigDecimal[] directionalMovementIndexValues = calculateDirectionalMovementIndexValues(directionalIndicators);
        return IndicatorResultExtractor.extractIndicatorValues(MovingAverageFactory.create(buildMARequest(directionalMovementIndexValues)).getResult());
    }

    private BigDecimal[] calculateDirectionalMovementIndexValues(BigDecimalTuple[] directionalIndicators) {
        return IntStream.range(0, originalData.length)
                .mapToObj(idx -> calculateDirectionalMovementIndex(directionalIndicators[idx]))
                .toArray(BigDecimal[]::new);
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

    private IndicatorRequest buildMARequest(BigDecimal[] ratios) {
        return MARequest.builder()
                .originalData(FakeTicksCreator.createWithCloseOnly(ratios))
                .indicatorType(EXPONENTIAL_MOVING_AVERAGE)
                .period(period)
                .priceType(CLOSE)
                .build();
    }

    private void buildAverageDirectionalMovementIndex(BigDecimalTuple[] directionalIndicators, BigDecimal[] averageDirectionalIndexes) {
        IntStream.range(0, result.length)
                .forEach(idx -> result[idx] = buildADXResult(directionalIndicators, averageDirectionalIndexes, idx));
    }

    private ADXResult buildADXResult(BigDecimalTuple[] directionalIndicators, BigDecimal[] averageDirectionalIndexes, int idx) {
        return new ADXResult(
                originalData[idx].getTickTime(),
                directionalIndicators[idx].getLeft(),
                directionalIndicators[idx].getRight(),
                extractAverageDirectionalIndex(directionalIndicators, averageDirectionalIndexes, idx));
    }

    private BigDecimal extractAverageDirectionalIndex(BigDecimalTuple[] directionalIndicators, BigDecimal[] averageDirectionalIndexes, int currentIndex) {
        return nonNull(directionalIndicators[currentIndex].getLeft()) && nonNull(directionalIndicators[currentIndex].getRight())
                ? averageDirectionalIndexes[currentIndex - period + 1]
                : null;
    }

}
