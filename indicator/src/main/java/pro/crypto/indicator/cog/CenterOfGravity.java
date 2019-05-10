package pro.crypto.indicator.cog;

import pro.crypto.helper.FakeTicksCreator;
import pro.crypto.helper.IndicatorResultExtractor;
import pro.crypto.helper.MathHelper;
import pro.crypto.indicator.ma.MARequest;
import pro.crypto.indicator.ma.MovingAverageFactory;
import pro.crypto.model.BigDecimalTuple;
import pro.crypto.model.Indicator;
import pro.crypto.model.IndicatorRequest;
import pro.crypto.model.IndicatorType;
import pro.crypto.model.SimpleIndicatorResult;
import pro.crypto.model.tick.PriceType;
import pro.crypto.model.tick.Tick;

import java.math.BigDecimal;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.IntStream;

import static java.util.Objects.isNull;
import static pro.crypto.model.IndicatorType.CENTER_OF_GRAVITY;
import static pro.crypto.model.tick.PriceType.CLOSE;

public class CenterOfGravity implements Indicator<COGResult> {

    private final Tick[] originalData;
    private final PriceType priceType;
    private final IndicatorType movingAverageType;
    private final int period;
    private final int signalLinePeriod;

    private COGResult[] result;

    public CenterOfGravity(IndicatorRequest creationRequest) {
        COGRequest request = (COGRequest) creationRequest;
        this.originalData = request.getOriginalData();
        this.priceType = request.getPriceType();
        this.movingAverageType = request.getMovingAverageType();
        this.period = request.getPeriod();
        this.signalLinePeriod = request.getSignalLinePeriod();
        checkIncomingData();
    }

    @Override
    public IndicatorType getType() {
        return CENTER_OF_GRAVITY;
    }

    @Override
    public void calculate() {
        result = new COGResult[originalData.length];
        BigDecimal[] centerOfGravityValues = calculateCenterOfGravityValues();
        BigDecimal[] signalLineValues = calculateSignalLineValues(centerOfGravityValues);
        buildCenterOfGravityResult(centerOfGravityValues, signalLineValues);
    }

    @Override
    public COGResult[] getResult() {
        if (isNull(result)) {
            calculate();
        }
        return result;
    }

    private void checkIncomingData() {
        checkOriginalData(originalData);
        checkOriginalDataSize(originalData, period + signalLinePeriod);
        checkPeriod(period);
        checkPeriod(signalLinePeriod);
        checkMovingAverageType(movingAverageType);
        checkPriceType(priceType);
    }

    private BigDecimal[] calculateCenterOfGravityValues() {
        return IntStream.range(0, originalData.length)
                .mapToObj(this::calculateCenterOfGravity)
                .toArray(BigDecimal[]::new);
    }

    private BigDecimal calculateCenterOfGravity(int outsideIndex) {
        return outsideIndex >= period - 1
                ? calculateCenterOfGravityValue(outsideIndex)
                : null;
    }

    private BigDecimal calculateCenterOfGravityValue(int outsideIndex) {
        final AtomicInteger coefficient = new AtomicInteger(0);
        BigDecimalTuple divisibleDivisor = IntStream.rangeClosed(outsideIndex - period + 1, outsideIndex)
                .mapToObj(idx -> calculateDivisibleDivisor(coefficient, idx))
                .reduce(BigDecimalTuple.zero(), BigDecimalTuple::add);
        return MathHelper.divide(divisibleDivisor.getLeft(), divisibleDivisor.getRight());
    }

    private BigDecimalTuple calculateDivisibleDivisor(AtomicInteger coefficient, int currentIndex) {
        BigDecimal divisible = calculateDivisible(coefficient.getAndIncrement(), currentIndex);
        BigDecimal divisor = calculateDivisor(currentIndex);
        return new BigDecimalTuple(divisible, divisor);
    }

    private BigDecimal calculateDivisible(int coefficient, int currentIndex) {
        return new BigDecimal(coefficient + 1).multiply(originalData[currentIndex].getPriceByType(priceType));
    }

    private BigDecimal calculateDivisor(int currentIndex) {
        return originalData[currentIndex].getPriceByType(priceType);
    }

    private BigDecimal[] calculateSignalLineValues(BigDecimal[] centerOfGravityValues) {
        BigDecimal[] signalLineValues = new BigDecimal[centerOfGravityValues.length];
        BigDecimal[] movingAverageValues = IndicatorResultExtractor.extractIndicatorValues(calculateMovingAverage(centerOfGravityValues));
        System.arraycopy(movingAverageValues, 0, signalLineValues, period - 1, movingAverageValues.length);
        return signalLineValues;
    }

    private SimpleIndicatorResult[] calculateMovingAverage(BigDecimal[] centerOfGravityValues) {
        return MovingAverageFactory.create(buildMARequest(centerOfGravityValues)).getResult();
    }

    private IndicatorRequest buildMARequest(BigDecimal[] centerOfGravityValues) {
        return MARequest.builder()
                .originalData(FakeTicksCreator.createWithCloseOnly(centerOfGravityValues))
                .period(signalLinePeriod)
                .indicatorType(movingAverageType)
                .priceType(CLOSE)
                .build();
    }

    private void buildCenterOfGravityResult(BigDecimal[] centerOfGravityValues, BigDecimal[] signalLineValues) {
        IntStream.range(0, result.length)
                .forEach(idx -> result[idx] = new COGResult(
                        originalData[idx].getTickTime(),
                        centerOfGravityValues[idx],
                        signalLineValues[idx]
                ));
    }

}
