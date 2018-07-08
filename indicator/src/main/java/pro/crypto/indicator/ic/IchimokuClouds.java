package pro.crypto.indicator.ic;

import pro.crypto.exception.WrongIncomingParametersException;
import pro.crypto.helper.MathHelper;
import pro.crypto.helper.PriceExtractor;
import pro.crypto.model.Indicator;
import pro.crypto.model.IndicatorRequest;
import pro.crypto.model.IndicatorType;
import pro.crypto.model.tick.Tick;

import java.math.BigDecimal;
import java.util.stream.IntStream;

import static java.lang.String.format;
import static java.util.Arrays.copyOfRange;
import static java.util.Objects.isNull;
import static pro.crypto.model.IndicatorType.ICHIMOKU_CLOUDS;
import static pro.crypto.model.tick.PriceType.*;

public class IchimokuClouds implements Indicator<ICResult> {

    private final Tick[] originalData;
    private final int conversionLinePeriod;
    private final int baseLinePeriod;
    private final int leadingSpanPeriod;
    private final int displaced;

    private ICResult[] result;

    public IchimokuClouds(IndicatorRequest creationRequest) {
        ICRequest request = (ICRequest) creationRequest;
        this.originalData = request.getOriginalData();
        this.conversionLinePeriod = request.getConversionLinePeriod();
        this.baseLinePeriod = request.getBaseLinePeriod();
        this.leadingSpanPeriod = request.getLeadingSpanPeriod();
        this.displaced = request.getDisplaced();
        checkIncomingData();
    }

    @Override
    public IndicatorType getType() {
        return ICHIMOKU_CLOUDS;
    }

    @Override
    public void calculate() {
        result = new ICResult[originalData.length];
        BigDecimal[] conversionLine = calculateConversionLine();
        BigDecimal[] baseLine = calculateBaseLine();
        BigDecimal[] leadingSpanA = calculateLeadingSpanA(conversionLine, baseLine);
        BigDecimal[] leadingSpanB = calculateLeadingSpanB();
        BigDecimal[] laggingSpan = calculateLaggingSpan();
        buildIchimokuClouds(conversionLine, baseLine, leadingSpanA, leadingSpanB, laggingSpan);
    }

    @Override
    public ICResult[] getResult() {
        if (isNull(result)) {
            calculate();
        }
        return result;
    }

    private void checkIncomingData() {
        checkOriginalData(originalData);
        checkPeriods();
        checkOriginalDataSize(originalData, baseLinePeriod + leadingSpanPeriod);
        checkDisplaced(displaced);
    }

    private void checkPeriods() {
        checkPeriod(conversionLinePeriod);
        checkPeriod(baseLinePeriod);
        checkPeriod(leadingSpanPeriod);
        checkPeriodLength();
    }

    private void checkPeriodLength() {
        if (conversionLinePeriod >= baseLinePeriod) {
            throw new WrongIncomingParametersException(format("Conversion Line Period should be less than Base Line Period " +
                            "{indicator: {%s}, conversionLinePeriod: {%d}, baseLinePeriod: {%d}}",
                    getType().toString(), conversionLinePeriod, baseLinePeriod));
        }

        if (baseLinePeriod >= leadingSpanPeriod) {
            throw new WrongIncomingParametersException(format("Base Line Period should be less than Leading Span Period " +
                            "{indicator: {%s}, baseLinePeriod: {%d}, leadingSpanPeriod: {%d}}",
                    getType().toString(), baseLinePeriod, leadingSpanPeriod));
        }
    }

    private BigDecimal[] calculateConversionLine() {
        return calculateAverageBetweenMaxMin(conversionLinePeriod);
    }

    private BigDecimal[] calculateBaseLine() {
        return calculateAverageBetweenMaxMin(baseLinePeriod);
    }

    private BigDecimal[] calculateLeadingSpanA(BigDecimal[] conversionLine, BigDecimal[] baseLine) {
        return IntStream.range(0, originalData.length)
                .mapToObj(idx -> calculateLeadingSpanA(conversionLine, baseLine, idx))
                .toArray(BigDecimal[]::new);
    }

    private BigDecimal calculateLeadingSpanA(BigDecimal[] conversionLine, BigDecimal[] baseLine, int currentIndex) {
        return currentIndex >= baseLinePeriod + displaced - 1
                ? calculateLeadingSpanAValue(conversionLine, baseLine, currentIndex)
                : null;
    }

    private BigDecimal calculateLeadingSpanAValue(BigDecimal[] conversionLine, BigDecimal[] baseLine, int currentIndex) {
        return MathHelper.average(
                conversionLine[currentIndex - displaced],
                baseLine[currentIndex - displaced]);
    }

    private BigDecimal[] calculateLeadingSpanB() {
        BigDecimal[] notShiftedData = calculateAverageBetweenMaxMin(leadingSpanPeriod);
        BigDecimal[] leadingSpanB = new BigDecimal[originalData.length];
        System.arraycopy(notShiftedData, leadingSpanPeriod - 1,
                leadingSpanB, displaced + leadingSpanPeriod - 1,
                leadingSpanB.length - (displaced + leadingSpanPeriod - 1));
        return leadingSpanB;
    }

    private BigDecimal[] calculateAverageBetweenMaxMin(int period) {
        BigDecimal[] highValues = PriceExtractor.extractValuesByType(originalData, HIGH);
        BigDecimal[] lowValues = PriceExtractor.extractValuesByType(originalData, LOW);
        return IntStream.range(0, originalData.length)
                .mapToObj(idx -> calculateAverage(period, highValues, lowValues, idx))
                .toArray(BigDecimal[]::new);
    }

    private BigDecimal calculateAverage(int period, BigDecimal[] highValues, BigDecimal[] lowValues, int currentIndex) {
        return currentIndex >= period - 1
                ? calculateAverageValue(period, highValues, lowValues, currentIndex)
                : null;
    }

    private BigDecimal calculateAverageValue(int period, BigDecimal[] highValues, BigDecimal[] lowValues, int currentIndex) {
        return MathHelper.average(
                MathHelper.max(copyOfRange(highValues, currentIndex - period + 1, currentIndex + 1)),
                MathHelper.min(copyOfRange(lowValues, currentIndex - period + 1, currentIndex + 1)));
    }

    private BigDecimal[] calculateLaggingSpan() {
        BigDecimal[] laggingSpan = new BigDecimal[originalData.length];
        System.arraycopy(PriceExtractor.extractValuesByType(originalData, CLOSE), displaced, laggingSpan, 0, laggingSpan.length - displaced);
        return laggingSpan;
    }

    private void buildIchimokuClouds(BigDecimal[] conversionLine, BigDecimal[] baseLine,
                                     BigDecimal[] leadingSpanA, BigDecimal[] leadingSpanB,
                                     BigDecimal[] laggingSpan) {
        IntStream.range(0, result.length)
                .forEach(idx -> result[idx] = new ICResult(originalData[idx].getTickTime(), conversionLine[idx],
                        baseLine[idx], leadingSpanA[idx], leadingSpanB[idx], laggingSpan[idx]));
    }

}
