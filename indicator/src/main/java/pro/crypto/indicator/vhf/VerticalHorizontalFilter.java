package pro.crypto.indicator.vhf;

import pro.crypto.helper.MathHelper;
import pro.crypto.helper.MinMaxFinder;
import pro.crypto.helper.PriceVolumeExtractor;
import pro.crypto.model.Indicator;
import pro.crypto.model.IndicatorRequest;
import pro.crypto.model.IndicatorType;
import pro.crypto.model.tick.Tick;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.stream.IntStream;

import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;
import static pro.crypto.model.IndicatorType.VERTICAL_HORIZONTAL_FILTER;
import static pro.crypto.model.tick.PriceType.CLOSE;

/**
 * Indicator is made for visual analyzing
 */
public class VerticalHorizontalFilter implements Indicator<VHFResult> {

    private final Tick[] originalData;
    private final int period;

    private VHFResult[] result;

    public VerticalHorizontalFilter(IndicatorRequest creationRequest) {
        VHFRequest request = (VHFRequest) creationRequest;
        this.originalData = request.getOriginalData();
        this.period = request.getPeriod();
        checkIncomingData();
    }

    @Override
    public IndicatorType getType() {
        return VERTICAL_HORIZONTAL_FILTER;
    }

    @Override
    public void calculate() {
        BigDecimal[] numerators = calculateNumerators();
        BigDecimal[] denominators = calculateDenominators();
        buildVerticalHorizontalFilterResult(numerators, denominators);
    }

    @Override
    public VHFResult[] getResult() {
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

    private BigDecimal[] calculateNumerators() {
        BigDecimal[] closePrices = PriceVolumeExtractor.extractPrices(originalData, CLOSE);
        BigDecimal[] maxCloseValues = MinMaxFinder.findMaxValues(closePrices, period);
        BigDecimal[] minCloseValues = MinMaxFinder.findMinValues(closePrices, period);
        return calculateNumerators(maxCloseValues, minCloseValues);
    }

    private BigDecimal[] calculateNumerators(BigDecimal[] maxCloseValues, BigDecimal[] minCloseValues) {
        return IntStream.range(0, originalData.length)
                .mapToObj(idx -> calculateNumerator(maxCloseValues[idx], minCloseValues[idx]))
                .toArray(BigDecimal[]::new);
    }

    private BigDecimal calculateNumerator(BigDecimal maxCloseValue, BigDecimal minCloseValue) {
        return nonNull(maxCloseValue) && nonNull(minCloseValue)
                ? maxCloseValue.subtract(minCloseValue)
                : null;
    }

    private BigDecimal[] calculateDenominators() {
        BigDecimal[] absoluteCloseDiff = calculateAbsoluteCloseDifferences();
        return calculateCloseDifferencesSum(absoluteCloseDiff);
    }

    private BigDecimal[] calculateAbsoluteCloseDifferences() {
        BigDecimal[] absoluteCloseDiffs = new BigDecimal[originalData.length];
        IntStream.range(1, absoluteCloseDiffs.length)
                .forEach(idx -> absoluteCloseDiffs[idx] = originalData[idx].getClose().subtract(originalData[idx - 1].getClose()).abs());
        return absoluteCloseDiffs;
    }

    private BigDecimal[] calculateCloseDifferencesSum(BigDecimal[] absoluteCloseDiffs) {
        BigDecimal[] sum = new BigDecimal[originalData.length];
        IntStream.range(period - 1, sum.length)
                .forEach(idx -> sum[idx] = calculateCloseDifferencesSum(absoluteCloseDiffs, idx));
        return sum;
    }

    private BigDecimal calculateCloseDifferencesSum(BigDecimal[] absoluteCloseDiffs, int currentIndex) {
        return MathHelper.sum(Arrays.copyOfRange(absoluteCloseDiffs, currentIndex - period + 2, currentIndex + 1));
    }

    private void buildVerticalHorizontalFilterResult(BigDecimal[] numerators, BigDecimal[] denominators) {
        result = IntStream.range(0, originalData.length)
                .mapToObj(idx -> new VHFResult(
                        originalData[idx].getTickTime(),
                        MathHelper.divide(numerators[idx], denominators[idx])
                ))
                .toArray(VHFResult[]::new);
    }

}
