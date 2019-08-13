package pro.crypto.indicator.mfi;

import pro.crypto.helper.MathHelper;
import pro.crypto.model.Indicator;
import pro.crypto.model.indicator.IndicatorType;
import pro.crypto.model.indicator.IndicatorVolumeCorrelation;
import pro.crypto.model.tick.Tick;
import pro.crypto.request.IndicatorRequest;

import java.math.BigDecimal;
import java.util.stream.IntStream;

import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;
import static pro.crypto.model.indicator.IndicatorType.MARKET_FACILITATION_INDEX;
import static pro.crypto.model.indicator.IndicatorVolumeCorrelation.INDICATOR_DOWN_VOLUME_DOWN;
import static pro.crypto.model.indicator.IndicatorVolumeCorrelation.INDICATOR_DOWN_VOLUME_UP;
import static pro.crypto.model.indicator.IndicatorVolumeCorrelation.INDICATOR_UP_VOLUME_DOWN;
import static pro.crypto.model.indicator.IndicatorVolumeCorrelation.INDICATOR_UP_VOLUME_UP;

/**
 * This indicator is only for visual analysis. It doesn't have any analyzers
 */
public class MarketFacilitationIndex implements Indicator<MFIResult> {

    private final Tick[] originalData;

    private BigDecimal[] indicatorResults;
    private MFIResult[] result;

    public MarketFacilitationIndex(IndicatorRequest creationRequest) {
        this.originalData = creationRequest.getOriginalData();
        checkOriginalData(originalData);
    }

    @Override
    public IndicatorType getType() {
        return MARKET_FACILITATION_INDEX;
    }

    @Override
    public void calculate() {
        indicatorResults = calculateMarketFacilitationIndexValues();
        IndicatorVolumeCorrelation[] correlations = findIndicatorVolumeCorrelations();
        buildMarketFacilitationIndexResult(correlations);
    }

    @Override
    public MFIResult[] getResult() {
        if (isNull(result)) {
            calculate();
        }
        return result;
    }

    private BigDecimal[] calculateMarketFacilitationIndexValues() {
        return IntStream.range(0, originalData.length)
                .mapToObj(this::calculateMarketFacilitationIndex)
                .toArray(BigDecimal[]::new);
    }

    private BigDecimal calculateMarketFacilitationIndex(int currentIndex) {
        return calculateMarketFacilitationIndexValue(originalData[currentIndex]);
    }

    private BigDecimal calculateMarketFacilitationIndexValue(Tick tick) {
        return MathHelper.divide(tick.getHigh().subtract(tick.getLow()), tick.getBaseVolume());
    }

    private IndicatorVolumeCorrelation[] findIndicatorVolumeCorrelations() {
        return IntStream.range(0, indicatorResults.length)
                .mapToObj(this::findIndicatorVolumeCorrelation)
                .toArray(IndicatorVolumeCorrelation[]::new);
    }

    private IndicatorVolumeCorrelation findIndicatorVolumeCorrelation(int currentIndex) {
        return isPossibleDefineCorrelation(currentIndex)
                ? defineCorrelation(currentIndex)
                : null;
    }

    private boolean isPossibleDefineCorrelation(int currentIndex) {
        return currentIndex > 0
                && nonNull(indicatorResults[currentIndex - 1])
                && nonNull(indicatorResults[currentIndex])
                && nonNull(originalData[currentIndex - 1].getBaseVolume())
                && nonNull(originalData[currentIndex].getBaseVolume());
    }

    private IndicatorVolumeCorrelation defineCorrelation(int currentIndex) {
        if (isIndicatorIncreased(currentIndex) && isVolumeIncreased(currentIndex)) {
            return INDICATOR_UP_VOLUME_UP;
        }

        if (isIndicatorIncreased(currentIndex) && isVolumeDecreased(currentIndex)) {
            return INDICATOR_UP_VOLUME_DOWN;
        }

        if (isIndicatorDecreased(currentIndex) && isVolumeIncreased(currentIndex)) {
            return INDICATOR_DOWN_VOLUME_UP;
        }

        if (isIndicatorDecreased(currentIndex) && isVolumeDecreased(currentIndex)) {
            return INDICATOR_DOWN_VOLUME_DOWN;
        }

        return null;
    }

    private boolean isIndicatorIncreased(int currentIndex) {
        return indicatorResults[currentIndex].compareTo(indicatorResults[currentIndex - 1]) > 0;
    }

    private boolean isVolumeIncreased(int currentIndex) {
        return originalData[currentIndex].getBaseVolume().compareTo(originalData[currentIndex - 1].getBaseVolume()) > 0;
    }

    private boolean isVolumeDecreased(int currentIndex) {
        return originalData[currentIndex].getBaseVolume().compareTo(originalData[currentIndex - 1].getBaseVolume()) < 0;
    }

    private boolean isIndicatorDecreased(int currentIndex) {
        return indicatorResults[currentIndex].compareTo(indicatorResults[currentIndex - 1]) < 0;
    }

    private void buildMarketFacilitationIndexResult(IndicatorVolumeCorrelation[] correlations) {
        this.result = IntStream.range(0, originalData.length)
                .mapToObj(idx -> new MFIResult(originalData[idx].getTickTime(), indicatorResults[idx], correlations[idx]))
                .toArray(MFIResult[]::new);
    }

}
