package pro.crypto.indicators.hlb;

import pro.crypto.helper.IndicatorResultExtractor;
import pro.crypto.helper.MathHelper;
import pro.crypto.indicators.ma.MovingAverageFactory;
import pro.crypto.model.Indicator;
import pro.crypto.model.IndicatorType;
import pro.crypto.model.request.HLBRequest;
import pro.crypto.model.request.MARequest;
import pro.crypto.model.result.HLBResult;
import pro.crypto.model.result.MAResult;
import pro.crypto.model.tick.PriceType;
import pro.crypto.model.tick.Tick;

import java.math.BigDecimal;
import java.util.stream.Stream;

import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;
import static pro.crypto.model.IndicatorType.HIGH_LOW_BANDS;
import static pro.crypto.model.IndicatorType.TRIANGULAR_MOVING_AVERAGE;

public class HighLowBands implements Indicator<HLBResult> {

    private final Tick[] originalData;
    private final int period;
    private final PriceType priceType;
    private final double shiftPercentage;

    private HLBResult[] result;

    public HighLowBands(HLBRequest request) {
        this.originalData = request.getOriginalData();
        this.period = request.getPeriod();
        this.priceType = request.getPriceType();
        this.shiftPercentage = request.getShiftPercentage();
        checkIncomingData();
    }

    @Override
    public IndicatorType getType() {
        return HIGH_LOW_BANDS;
    }

    @Override
    public void calculate() {
        result = new HLBResult[originalData.length];
        BigDecimal[] basisValues = calculateBasisValues();
        BigDecimal[] upperEnvelopeValues = calculateUpperEnvelopeValues(basisValues);
        BigDecimal[] lowerEnvelopeValues = calculateLowerEnvelopeValues(basisValues);
        buildHighLowBandsResult(basisValues, upperEnvelopeValues, lowerEnvelopeValues);
    }

    @Override
    public HLBResult[] getResult() {
        if (isNull(result)) {
            calculate();
        }
        return result;
    }

    private void checkIncomingData() {
        checkOriginalData(originalData);
        checkOriginalDataSize(originalData, period);
        checkPeriod(period);
        checkShift(shiftPercentage);
        checkPriceType(priceType);
    }

    private BigDecimal[] calculateBasisValues() {
        return IndicatorResultExtractor.extract(calculateTriangularMovingAverage());
    }

    private MAResult[] calculateTriangularMovingAverage() {
        return MovingAverageFactory.create(buildMARequest()).getResult();
    }

    private MARequest buildMARequest() {
        return MARequest.builder()
                .originalData(originalData)
                .priceType(priceType)
                .period(period)
                .indicatorType(TRIANGULAR_MOVING_AVERAGE)
                .build();
    }

    private BigDecimal[] calculateUpperEnvelopeValues(BigDecimal[] basisValues) {
        return Stream.of(basisValues)
                .map(this::calculateUpperEnvelope)
                .toArray(BigDecimal[]::new);
    }

    private BigDecimal calculateUpperEnvelope(BigDecimal basis) {
        return nonNull(basis)
                ? calculateUpperEnvelopeValue(basis)
                : null;
    }

    private BigDecimal calculateUpperEnvelopeValue(BigDecimal basis) {
        return MathHelper.scaleAndRound(basis.add(
                calculatePercentage(basis)));
    }

    private BigDecimal[] calculateLowerEnvelopeValues(BigDecimal[] basisValues) {
        return Stream.of(basisValues)
                .map(this::calculateLowerEnvelope)
                .toArray(BigDecimal[]::new);
    }

    private BigDecimal calculateLowerEnvelope(BigDecimal basis) {
        return nonNull(basis)
                ? calculateLowerEnvelopeValue(basis)
                : null;
    }

    private BigDecimal calculateLowerEnvelopeValue(BigDecimal basis) {
        return MathHelper.scaleAndRound(basis.subtract(calculatePercentage(basis)));
    }

    private BigDecimal calculatePercentage(BigDecimal basis) {
        return divideByHundred(basis).multiply(new BigDecimal(shiftPercentage));
    }

    private BigDecimal divideByHundred(BigDecimal basis) {
        return MathHelper.divide(basis, new BigDecimal(100));
    }

    private void buildHighLowBandsResult(BigDecimal[] basisValues, BigDecimal[] upperEnvelopeValues, BigDecimal[] lowerEnvelopeValues) {
        for (int currentIndex = 0; currentIndex < result.length; currentIndex++) {
            result[currentIndex] = new HLBResult(
                    originalData[currentIndex].getTickTime(), basisValues[currentIndex],
                    upperEnvelopeValues[currentIndex], lowerEnvelopeValues[currentIndex]
            );
        }
    }

}
