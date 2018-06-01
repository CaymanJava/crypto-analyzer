package pro.crypto.indicators.dc;

import pro.crypto.helper.MathHelper;
import pro.crypto.helper.MinMaxFinder;
import pro.crypto.helper.PriceExtractor;
import pro.crypto.model.Indicator;
import pro.crypto.model.IndicatorType;
import pro.crypto.model.request.DCRequest;
import pro.crypto.model.result.DCResult;
import pro.crypto.model.tick.Tick;

import java.math.BigDecimal;

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

    public DonchianChannel(DCRequest request) {
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
        return MinMaxFinder.findMaxExcludedLast(PriceExtractor.extractValuesByType(originalData, HIGH), highPeriod);
    }

    private BigDecimal[] calculateLowerEnvelopes() {
        return MinMaxFinder.findMinExcludedLast(PriceExtractor.extractValuesByType(originalData, LOW), lowPeriod);
    }

    private BigDecimal[] calculateMiddleEnvelopes(BigDecimal[] upperEnvelopes, BigDecimal[] lowerEnvelopes) {
        BigDecimal[] middleEnvelopes = new BigDecimal[originalData.length];
        for (int currentIndex = 0; currentIndex < middleEnvelopes.length; currentIndex++) {
            middleEnvelopes[currentIndex] = calculateMiddleEnvelope(upperEnvelopes[currentIndex], lowerEnvelopes[currentIndex]);
        }
        return middleEnvelopes;
    }

    private BigDecimal calculateMiddleEnvelope(BigDecimal upperEnvelope, BigDecimal lowerEnvelope) {
        return nonNull(upperEnvelope) && nonNull(lowerEnvelope)
                ? MathHelper.average(upperEnvelope, lowerEnvelope)
                : null;
    }

    private void buildDonchianResult(BigDecimal[] upperEnvelopes, BigDecimal[] lowerEnvelopes, BigDecimal[] middleEnvelopes) {
        for (int currentIndex = 0; currentIndex < result.length; currentIndex++) {
            result[currentIndex] = new DCResult(
                    originalData[currentIndex].getTickTime(),
                    middleEnvelopes[currentIndex],
                    upperEnvelopes[currentIndex],
                    lowerEnvelopes[currentIndex]);
        }
    }

}
