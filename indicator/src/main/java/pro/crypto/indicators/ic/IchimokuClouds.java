package pro.crypto.indicators.ic;

import pro.crypto.exception.WrongIncomingParametersException;
import pro.crypto.helper.MathHelper;
import pro.crypto.helper.PriceExtractor;
import pro.crypto.model.Indicator;
import pro.crypto.model.IndicatorType;
import pro.crypto.model.request.ICRequest;
import pro.crypto.model.result.ICResult;
import pro.crypto.model.tick.Tick;

import java.math.BigDecimal;

import static java.lang.String.format;
import static java.util.Arrays.copyOfRange;
import static java.util.Objects.isNull;
import static pro.crypto.model.IndicatorType.ICHIMOKU_CLOUDS;

public class IchimokuClouds implements Indicator<ICResult> {

    private final Tick[] originalData;
    private final int conversionLinePeriod;
    private final int baseLinePeriod;
    private final int leadingSpanPeriod;
    private final int displaced;

    private ICResult[] result;

    public IchimokuClouds(ICRequest request) {
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
        BigDecimal[] leadingSpanA = new BigDecimal[originalData.length];
        for (int currentIndex = baseLinePeriod + displaced - 1; currentIndex < leadingSpanA.length; currentIndex++) {
            leadingSpanA[currentIndex] = MathHelper.average(
                    conversionLine[currentIndex - displaced],
                    baseLine[currentIndex - displaced]);
        }
        return leadingSpanA;
    }

    private BigDecimal[] calculateLeadingSpanB() {
        BigDecimal[] leadingSpanB = new BigDecimal[originalData.length];
        BigDecimal[] notShiftedData = calculateAverageBetweenMaxMin(leadingSpanPeriod);
        System.arraycopy(notShiftedData, leadingSpanPeriod - 1,
                leadingSpanB, displaced + leadingSpanPeriod - 1,
                leadingSpanB.length - (displaced + leadingSpanPeriod - 1));
        return leadingSpanB;
    }

    private BigDecimal[] calculateAverageBetweenMaxMin(int period) {
        BigDecimal[] conversionLine = new BigDecimal[originalData.length];
        BigDecimal[] highValues = PriceExtractor.extractHighValues(originalData);
        BigDecimal[] lowValues = PriceExtractor.extractLowValues(originalData);
        for (int currentIndex = period - 1; currentIndex < conversionLine.length; currentIndex++) {
            conversionLine[currentIndex] = MathHelper.average(
                    MathHelper.max(copyOfRange(highValues, currentIndex - period + 1, currentIndex + 1)),
                    MathHelper.min(copyOfRange(lowValues, currentIndex - period + 1, currentIndex + 1))
            );
        }
        return conversionLine;
    }

    private BigDecimal[] calculateLaggingSpan() {
        BigDecimal[] laggingSpan = new BigDecimal[originalData.length];
        System.arraycopy(PriceExtractor.extractCloseValues(originalData), displaced, laggingSpan, 0, laggingSpan.length - displaced);
        return laggingSpan;
    }

    private void buildIchimokuClouds(BigDecimal[] conversionLine, BigDecimal[] baseLine,
                                     BigDecimal[] leadingSpanA, BigDecimal[] leadingSpanB,
                                     BigDecimal[] laggingSpan) {
        for (int currentIndex = 0; currentIndex < result.length; currentIndex++) {
            result[currentIndex] = new ICResult(
                    originalData[currentIndex].getTickTime(),
                    conversionLine[currentIndex],
                    baseLine[currentIndex],
                    leadingSpanA[currentIndex],
                    leadingSpanB[currentIndex],
                    laggingSpan[currentIndex]
            );
        }
    }

}
