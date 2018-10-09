package pro.crypto.analyzer.mfi;

import pro.crypto.indicator.mfi.MFIResult;
import pro.crypto.model.Analyzer;
import pro.crypto.model.AnalyzerRequest;
import pro.crypto.model.IndicatorVolumeCorrelation;
import pro.crypto.model.tick.Tick;

import java.util.stream.IntStream;

import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;
import static pro.crypto.model.IndicatorVolumeCorrelation.*;

public class MFIAnalyzer implements Analyzer<MFIAnalyzerResult> {

    private final Tick[] originalData;
    private final MFIResult[] indicatorResults;

    private MFIAnalyzerResult[] result;

    public MFIAnalyzer(AnalyzerRequest request) {
        this.originalData = request.getOriginalData();
        this.indicatorResults = (MFIResult[]) request.getIndicatorResults();
    }

    @Override
    public void analyze() {
        IndicatorVolumeCorrelation[] correlations = findIndicatorVolumeCorrelations();
        buildMFIAnalyzerResult(correlations);
    }

    @Override
    public MFIAnalyzerResult[] getResult() {
        if (isNull(result)) {
            analyze();
        }
        return result;
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
                && nonNull(indicatorResults[currentIndex - 1].getIndicatorValue())
                && nonNull(indicatorResults[currentIndex].getIndicatorValue())
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
        return indicatorResults[currentIndex].getIndicatorValue().compareTo(indicatorResults[currentIndex - 1].getIndicatorValue()) > 0;
    }

    private boolean isVolumeIncreased(int currentIndex) {
        return originalData[currentIndex].getBaseVolume().compareTo(originalData[currentIndex - 1].getBaseVolume()) > 0;
    }

    private boolean isVolumeDecreased(int currentIndex) {
        return originalData[currentIndex].getBaseVolume().compareTo(originalData[currentIndex - 1].getBaseVolume()) < 0;
    }

    private boolean isIndicatorDecreased(int currentIndex) {
        return indicatorResults[currentIndex].getIndicatorValue().compareTo(indicatorResults[currentIndex - 1].getIndicatorValue()) < 0;
    }

    private void buildMFIAnalyzerResult(IndicatorVolumeCorrelation[] correlations) {
        result = IntStream.range(0, indicatorResults.length)
                .mapToObj(idx -> new MFIAnalyzerResult(indicatorResults[idx].getTime(), correlations[idx]))
                .toArray(MFIAnalyzerResult[]::new);
    }

}
