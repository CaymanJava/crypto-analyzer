package pro.crypto.indicator.ro;

import pro.crypto.helper.MathHelper;
import pro.crypto.indicator.rma.RMARequest;
import pro.crypto.indicator.rma.RMAResult;
import pro.crypto.indicator.rma.RainbowMovingAverage;
import pro.crypto.model.Indicator;
import pro.crypto.model.IndicatorRequest;
import pro.crypto.model.IndicatorType;
import pro.crypto.model.tick.PriceType;
import pro.crypto.model.tick.Tick;

import java.math.BigDecimal;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import static java.lang.Math.max;
import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;
import static pro.crypto.helper.MinMaxFinder.*;
import static pro.crypto.helper.MinMaxFinder.findMaxValues;
import static pro.crypto.helper.PriceVolumeExtractor.extract;
import static pro.crypto.model.IndicatorType.RAINBOW_OSCILLATOR;

public class RainbowOscillator implements Indicator<ROResult> {

    private final Tick[] originalData;
    private final PriceType priceType;
    private final int period;
    private final int highLowLookBack;

    private RMAResult[] rmaResults;
    private BigDecimal[] maxPriceValues;
    private BigDecimal[] minPriceValues;
    private BigDecimal[] maxRmaValues;
    private BigDecimal[] minRmaValues;
    private BigDecimal[] avgRmaValues;
    private BigDecimal[] roValues;
    private BigDecimal[] upperEnvelopes;

    private ROResult[] result;

    public RainbowOscillator(IndicatorRequest creationRequest) {
        RORequest request = (RORequest) creationRequest;
        this.originalData = request.getOriginalData();
        this.priceType = request.getPriceType();
        this.period = request.getPeriod();
        this.highLowLookBack = request.getHighLowLookBack();
        checkIncomingData();
    }

    @Override
    public IndicatorType getType() {
        return RAINBOW_OSCILLATOR;
    }

    @Override
    public void calculate() {
        calculateRainbowMovingAverage();
        findMaxPriceValues();
        findMinPriceValues();
        findMaxRmaValues();
        findMinRmaValues();
        findAverageRmaValues();
        calculateRainbowOscillator();
        calculateUpperEnvelopes();
        buildRainbowOscillatorResult();
    }

    @Override
    public ROResult[] getResult() {
        if (isNull(result)) {
            calculate();
        }
        return result;
    }

    private void checkIncomingData() {
        checkOriginalData(originalData);
        checkOriginalDataSize(originalData, max(period * 10 - 10, highLowLookBack));
        checkPriceType(priceType);
        checkPeriod(period);
        checkPeriod(highLowLookBack);
    }

    private void calculateRainbowMovingAverage() {
        rmaResults = new RainbowMovingAverage(buildRMARequest()).getResult();
    }

    private IndicatorRequest buildRMARequest() {
        return RMARequest.builder()
                .originalData(originalData)
                .priceType(priceType)
                .period(period)
                .build();
    }

    private void findMaxPriceValues() {
        maxPriceValues = findMaxValues(extract(originalData, priceType), highLowLookBack);
    }

    private void findMinPriceValues() {
        minPriceValues = findMinValues(extract(originalData, priceType), highLowLookBack);
    }

    private void findMaxRmaValues() {
        maxRmaValues = Stream.of(rmaResults)
                .map(this::findMaxValue)
                .toArray(BigDecimal[]::new);
    }

    private BigDecimal findMaxValue(RMAResult rmaResult) {
        return isAllValuesPresent(rmaResult)
                ? MathHelper.max(rmaResult.getFirstMaValue(), rmaResult.getSecondMaValue(), rmaResult.getThirdMaValue(),
                rmaResult.getFourthMaValue(), rmaResult.getFifthMaValue(), rmaResult.getSixthMaValue(),
                rmaResult.getSeventhMaValue(), rmaResult.getEighthMaValue(), rmaResult.getNinthMaValue(),
                rmaResult.getTenthMaValue())
                : null;
    }

    private void findMinRmaValues() {
        minRmaValues = Stream.of(rmaResults)
                .map(this::findMinValue)
                .toArray(BigDecimal[]::new);
    }

    private BigDecimal findMinValue(RMAResult rmaResult) {
        return isAllValuesPresent(rmaResult)
                ? MathHelper.min(rmaResult.getFirstMaValue(), rmaResult.getSecondMaValue(), rmaResult.getThirdMaValue(),
                rmaResult.getFourthMaValue(), rmaResult.getFifthMaValue(), rmaResult.getSixthMaValue(),
                rmaResult.getSeventhMaValue(), rmaResult.getEighthMaValue(), rmaResult.getNinthMaValue(),
                rmaResult.getTenthMaValue())
                : null;
    }

    private void findAverageRmaValues() {
        avgRmaValues = Stream.of(rmaResults)
                .map(this::findAverageValue)
                .toArray(BigDecimal[]::new);
    }

    private BigDecimal findAverageValue(RMAResult rmaResult) {
        return isAllValuesPresent(rmaResult)
                ? MathHelper.average(rmaResult.getFirstMaValue(), rmaResult.getSecondMaValue(), rmaResult.getThirdMaValue(),
                rmaResult.getFourthMaValue(), rmaResult.getFifthMaValue(), rmaResult.getSixthMaValue(),
                rmaResult.getSeventhMaValue(), rmaResult.getEighthMaValue(), rmaResult.getNinthMaValue(),
                rmaResult.getTenthMaValue())
                : null;
    }

    private boolean isAllValuesPresent(RMAResult rmaResult) {
        return nonNull(rmaResult.getFirstMaValue()) && nonNull(rmaResult.getSecondMaValue())
                && nonNull(rmaResult.getThirdMaValue()) && nonNull(rmaResult.getFourthMaValue())
                && nonNull(rmaResult.getFifthMaValue()) && nonNull(rmaResult.getSixthMaValue())
                && nonNull(rmaResult.getSeventhMaValue()) && nonNull(rmaResult.getEighthMaValue())
                && nonNull(rmaResult.getNinthMaValue()) && nonNull(rmaResult.getTenthMaValue());
    }

    private void calculateRainbowOscillator() {
        roValues = IntStream.range(0, originalData.length)
                .mapToObj(this::calculateRainbowOscillator)
                .toArray(BigDecimal[]::new);
    }


    private BigDecimal calculateRainbowOscillator(int currentIndex) {
        return nonNull(maxPriceValues[currentIndex]) && nonNull(minPriceValues[currentIndex]) && nonNull(avgRmaValues[currentIndex])
                ? calculateRainbowOscillatorValue(currentIndex)
                : null;
    }

    // 100 * (Price(i) - average RMA(i)) / (MaxPrice(i) - MinPrice(i))
    private BigDecimal calculateRainbowOscillatorValue(int currentIndex) {
        return MathHelper.divide(new BigDecimal(100)
                        .multiply(originalData[currentIndex].getPriceByType(priceType).subtract(avgRmaValues[currentIndex])),
                maxPriceValues[currentIndex].subtract(minPriceValues[currentIndex]));
    }

    private void calculateUpperEnvelopes() {
        upperEnvelopes = IntStream.range(0, originalData.length)
                .mapToObj(this::calculateUpperEnvelope)
                .toArray(BigDecimal[]::new);
    }

    private BigDecimal calculateUpperEnvelope(int currentIndex) {
        return nonNull(maxPriceValues[currentIndex]) && nonNull(minPriceValues[currentIndex])
                && nonNull(maxRmaValues[currentIndex]) && nonNull(minRmaValues[currentIndex])
                ? calculateUpperEnvelopeValue(currentIndex)
                : null;
    }

    // 100 * (MaxRMA(i) - MinRMA(i)) / (MaxPrice(i) - MinPrice(i))
    private BigDecimal calculateUpperEnvelopeValue(int currentIndex) {
        return MathHelper.divide(new BigDecimal(100)
                        .multiply(maxRmaValues[currentIndex].subtract(minRmaValues[currentIndex])),
                maxPriceValues[currentIndex].subtract(minPriceValues[currentIndex]));
    }

    private void buildRainbowOscillatorResult() {
        result = IntStream.range(0, originalData.length)
                .mapToObj(this::buildRainbowOscillator)
                .toArray(ROResult[]::new);
    }

    private ROResult buildRainbowOscillator(int currentIndex) {
        return new ROResult(originalData[currentIndex].getTickTime(), roValues[currentIndex],
                upperEnvelopes[currentIndex], calculateLowerEnvelope(upperEnvelopes[currentIndex]));
    }

    private BigDecimal calculateLowerEnvelope(BigDecimal upperEnvelope) {
        return nonNull(upperEnvelope)
                ? upperEnvelope.negate()
                : null;
    }

}
