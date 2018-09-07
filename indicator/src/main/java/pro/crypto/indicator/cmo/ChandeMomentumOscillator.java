package pro.crypto.indicator.cmo;

import pro.crypto.helper.FakeTicksCreator;
import pro.crypto.helper.IndicatorResultExtractor;
import pro.crypto.helper.MathHelper;
import pro.crypto.helper.PriceDifferencesCalculator;
import pro.crypto.indicator.ma.MARequest;
import pro.crypto.indicator.ma.MovingAverageFactory;
import pro.crypto.model.*;
import pro.crypto.model.tick.Tick;

import java.math.BigDecimal;
import java.util.stream.IntStream;

import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;
import static pro.crypto.model.IndicatorType.CHANDE_MOMENTUM_OSCILLATOR;
import static pro.crypto.model.tick.PriceType.CLOSE;

public class ChandeMomentumOscillator implements Indicator<CMOResult> {

    private final Tick[] originalData;
    private final int period;
    private final int signalLinePeriod;
    private final IndicatorType movingAverageType;

    private CMOResult[] result;

    public ChandeMomentumOscillator(IndicatorRequest creationRequest) {
        CMORequest request = (CMORequest) creationRequest;
        this.originalData = request.getOriginalData();
        this.period = request.getPeriod();
        this.signalLinePeriod = request.getSignalLinePeriod();
        this.movingAverageType = request.getMovingAverageType();
        checkIncomingData();
    }

    @Override
    public IndicatorType getType() {
        return CHANDE_MOMENTUM_OSCILLATOR;
    }

    @Override
    public void calculate() {
        result = new CMOResult[originalData.length];
        BigDecimalTuple[] priceDifferences = PriceDifferencesCalculator.calculatePriceDifference(originalData, CLOSE);
        BigDecimalTuple[] priceDifferenceSumValues = PriceDifferencesCalculator.calculatePriceDifferencesSum(priceDifferences, period);
        BigDecimal[] cmoValues = calculateChandeMomentumOscillatorResult(priceDifferenceSumValues);
        BigDecimal[] signalLineValues = calculateSignalLineValues(cmoValues);
        buildChandeMomentumOscillatorResult(cmoValues, signalLineValues);
    }

    @Override
    public CMOResult[] getResult() {
        if (isNull(result)) {
            calculate();
        }
        return result;
    }

    private void checkIncomingData() {
        checkOriginalData(originalData);
        checkOriginalDataSize(originalData, period + signalLinePeriod);
        checkMovingAverageType(movingAverageType);
        checkPeriod(period);
        checkPeriod(signalLinePeriod);
    }

    private BigDecimal[] calculateChandeMomentumOscillatorResult(BigDecimalTuple[] priceDifferenceSumValues) {
        return IntStream.range(0, result.length)
                .mapToObj(idx -> calculateChandeMomentumOscillator(priceDifferenceSumValues[idx]))
                .toArray(BigDecimal[]::new);
    }

    private BigDecimal calculateChandeMomentumOscillator(BigDecimalTuple priceDifferenceSum) {
        return nonNull(priceDifferenceSum)
                ? calculateChandeMomentumOscillatorValues(priceDifferenceSum)
                : null;
    }

    // 100 * (∑ positive movement - ∑ negative movement) / (∑ positive movement + ∑ negative movement)
    private BigDecimal calculateChandeMomentumOscillatorValues(BigDecimalTuple priceDifferenceSum) {
        return MathHelper.divide(new BigDecimal(100)
                        .multiply(priceDifferenceSum.getLeft().subtract(priceDifferenceSum.getRight())),
                priceDifferenceSum.getLeft().add(priceDifferenceSum.getRight())
        );
    }

    private BigDecimal[] calculateSignalLineValues(BigDecimal[] cmoValues) {
        BigDecimal[] movingAverageValues = IndicatorResultExtractor.extract(calculateMovingAverage(cmoValues));
        BigDecimal[] result = new BigDecimal[originalData.length];
        System.arraycopy(movingAverageValues, 0, result, period - 1, movingAverageValues.length);
        return result;
    }

    private SimpleIndicatorResult[] calculateMovingAverage(BigDecimal[] cmoValues) {
        return MovingAverageFactory.create(buildMARequest(cmoValues)).getResult();
    }

    private IndicatorRequest buildMARequest(BigDecimal[] cmoValues) {
        return MARequest.builder()
                .originalData(FakeTicksCreator.createWithCloseOnly(cmoValues))
                .priceType(CLOSE)
                .period(signalLinePeriod)
                .indicatorType(movingAverageType)
                .build();
    }

    private void buildChandeMomentumOscillatorResult(BigDecimal[] cmoValues, BigDecimal[] signalLineValues) {
        result = IntStream.range(0, originalData.length)
                .mapToObj(idx -> new CMOResult(originalData[idx].getTickTime(), cmoValues[idx], signalLineValues[idx]))
                .toArray(CMOResult[]::new);
    }

}
