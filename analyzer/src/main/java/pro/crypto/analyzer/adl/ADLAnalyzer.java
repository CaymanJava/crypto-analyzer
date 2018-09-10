package pro.crypto.analyzer.adl;

import pro.crypto.analyzer.helper.DefaultDivergenceAnalyzer;
import pro.crypto.helper.IndicatorResultExtractor;
import pro.crypto.indicator.adl.ADLResult;
import pro.crypto.model.Analyzer;
import pro.crypto.model.AnalyzerRequest;
import pro.crypto.model.Signal;
import pro.crypto.model.result.AnalyzerResult;
import pro.crypto.model.tick.Tick;

import java.util.stream.IntStream;

import static java.util.Objects.isNull;
import static pro.crypto.model.Signal.NEUTRAL;

public class ADLAnalyzer implements Analyzer<ADLAnalyzerResult> {

    private final Tick[] originalData;
    private final ADLResult[] indicatorResults;

    private ADLAnalyzerResult[] result;

    public ADLAnalyzer(AnalyzerRequest request) {
        this.originalData = request.getOriginalData();
        this.indicatorResults = (ADLResult[]) request.getIndicatorResults();
    }

    @Override
    public void analyze() {
        Signal[] signals = findDivergenceSignals();
        buildADLAnalyzerResults(signals);
    }

    @Override
    public ADLAnalyzerResult[] getResult() {
        if (isNull(result)) {
            analyze();
        }
        return result;
    }

    private Signal[] findDivergenceSignals() {
        return new DefaultDivergenceAnalyzer().analyze(originalData, IndicatorResultExtractor.extract(indicatorResults));
    }

    private void buildADLAnalyzerResults(Signal[] signals) {
        result = IntStream.range(0, originalData.length)
                .mapToObj(idx -> buildADLAnalyzerResult(idx, signals[idx]))
                .toArray(ADLAnalyzerResult[]::new);
    }

    private AnalyzerResult buildADLAnalyzerResult(int currentIndex, Signal signal) {
        return new ADLAnalyzerResult(originalData[currentIndex].getTickTime(), isNull(signal) ? NEUTRAL : signal);
    }

}
