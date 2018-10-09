package pro.crypto.indicator.gapo;

import pro.crypto.helper.MathHelper;
import pro.crypto.helper.MinMaxFinder;
import pro.crypto.helper.PriceVolumeExtractor;
import pro.crypto.model.Indicator;
import pro.crypto.model.IndicatorRequest;
import pro.crypto.model.IndicatorType;
import pro.crypto.model.tick.Tick;

import java.math.BigDecimal;
import java.util.stream.IntStream;

import static java.util.Objects.isNull;
import static pro.crypto.model.IndicatorType.GOPALAKRISHNAN_RANGE_INDEX;
import static pro.crypto.model.tick.PriceType.HIGH;
import static pro.crypto.model.tick.PriceType.LOW;

/**
 * This indicator is only for recognizing gaps. It doesn't have any analyzers or strategies
 */
public class GopalakrishnanRangeIndex implements Indicator<GAPOResult> {

    private final Tick[] originalData;
    private final int period;

    private GAPOResult[] result;

    public GopalakrishnanRangeIndex(IndicatorRequest creationRequest) {
        GAPORequest request = (GAPORequest) creationRequest;
        this.originalData = request.getOriginalData();
        this.period = request.getPeriod();
        checkIncomingData();
    }

    @Override
    public IndicatorType getType() {
        return GOPALAKRISHNAN_RANGE_INDEX;
    }

    @Override
    public void calculate() {
        result = new GAPOResult[originalData.length];
        calculateGopalakrishnanRangeIndex();
    }

    @Override
    public GAPOResult[] getResult() {
        if (isNull(result)) {
            calculate();
        }
        return result;
    }

    private void checkIncomingData() {
        checkOriginalData(originalData);
        checkOriginalDataSize(originalData, period);
        checkPeriod(period);
    }

    private void calculateGopalakrishnanRangeIndex() {
        BigDecimal[] maxValues = findMaxValues();
        BigDecimal[] minValues = findMinValues();
        BigDecimal log10Period = calculateLog10Period();
        IntStream.range(0, result.length)
                .forEach(idx -> result[idx] = buildGopalakrishnanRangeIndexResult(maxValues, minValues, log10Period, idx));
    }

    private BigDecimal[] findMaxValues() {
        return MinMaxFinder.findMaxValues(PriceVolumeExtractor.extract(originalData, HIGH), period);
    }

    private BigDecimal[] findMinValues() {
        return MinMaxFinder.findMinValues(PriceVolumeExtractor.extract(originalData, LOW), period);
    }

    private BigDecimal calculateLog10Period() {
        return MathHelper.log(new BigDecimal(period), 10);
    }

    private GAPOResult buildGopalakrishnanRangeIndexResult(BigDecimal[] maxValues, BigDecimal[] minValues, BigDecimal log10Period, int currentIndex) {
        return currentIndex < period - 1
                ? buildEmptyResult(originalData[currentIndex])
                : new GAPOResult(
                originalData[currentIndex].getTickTime(),
                calculateGopalakrishnanRangeIndexValue(maxValues[currentIndex], minValues[currentIndex], log10Period));
    }

    private GAPOResult buildEmptyResult(Tick originalDatum) {
        return new GAPOResult(originalDatum.getTickTime(), null);
    }

    // Lg(MAX(High) - MIN(Low)) / Lg(period)
    private BigDecimal calculateGopalakrishnanRangeIndexValue(BigDecimal maxValue, BigDecimal minValue, BigDecimal log10Period) {
        return MathHelper.divide(MathHelper.log(maxValue.subtract(minValue), 10), log10Period);
    }

}
