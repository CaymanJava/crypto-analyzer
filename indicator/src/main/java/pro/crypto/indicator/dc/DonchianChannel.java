package pro.crypto.indicator.dc;

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
import static java.util.Objects.nonNull;
import static pro.crypto.model.IndicatorType.DONCHIAN_CHANNEL;
import static pro.crypto.model.tick.PriceType.HIGH;
import static pro.crypto.model.tick.PriceType.LOW;

public class DonchianChannel implements Indicator<DCResult> {

    private final Tick[] originalData;
    private final int highPeriod;
    private final int lowPeriod;

    private DCResult[] result;

    public DonchianChannel(IndicatorRequest creationRequest) {
        DCRequest request = (DCRequest) creationRequest;
        this.originalData = request.getOriginalData();
        this.highPeriod = request.getHighPeriod();
        this.lowPeriod = request.getLowPeriod();
        checkIncomingData();
    }

    @Override
    public IndicatorType getType() {
        return DONCHIAN_CHANNEL;
    }

    @Override
    public void calculate() {
        result = new DCResult[originalData.length];
        BigDecimal[] upperEnvelopes = calculateUpperEnvelopes();
        BigDecimal[] lowerEnvelopes = calculateLowerEnvelopes();
        BigDecimal[] middleEnvelopes = calculateMiddleEnvelopes(upperEnvelopes, lowerEnvelopes);
        buildDonchianResult(upperEnvelopes, lowerEnvelopes, middleEnvelopes);
    }

    @Override
    public DCResult[] getResult() {
        if (isNull(result)) {
            calculate();
        }
        return result;
    }

    private void checkIncomingData() {
        checkOriginalData(originalData);
        checkOriginalDataSize(originalData, lowPeriod);
        checkOriginalDataSize(originalData, highPeriod);
        checkPeriods();
    }

    private void checkPeriods() {
        checkPeriod(highPeriod);
        checkPeriod(lowPeriod);
    }

    private BigDecimal[] calculateUpperEnvelopes() {
        return MinMaxFinder.findMaxExcludingLast(PriceVolumeExtractor.extractPrices(originalData, HIGH), highPeriod);
    }

    private BigDecimal[] calculateLowerEnvelopes() {
        return MinMaxFinder.findMinExcludingLast(PriceVolumeExtractor.extractPrices(originalData, LOW), lowPeriod);
    }

    private BigDecimal[] calculateMiddleEnvelopes(BigDecimal[] upperEnvelopes, BigDecimal[] lowerEnvelopes) {
        return IntStream.range(0, originalData.length)
                .mapToObj(idx -> calculateMiddleEnvelope(upperEnvelopes[idx], lowerEnvelopes[idx]))
                .toArray(BigDecimal[]::new);
    }

    private BigDecimal calculateMiddleEnvelope(BigDecimal upperEnvelope, BigDecimal lowerEnvelope) {
        return nonNull(upperEnvelope) && nonNull(lowerEnvelope)
                ? MathHelper.average(upperEnvelope, lowerEnvelope)
                : null;
    }

    private void buildDonchianResult(BigDecimal[] upperEnvelopes, BigDecimal[] lowerEnvelopes, BigDecimal[] middleEnvelopes) {
        IntStream.range(0, result.length)
                .forEach(idx -> result[idx] = new DCResult(
                        originalData[idx].getTickTime(), middleEnvelopes[idx],
                        upperEnvelopes[idx], lowerEnvelopes[idx]));
    }

}
