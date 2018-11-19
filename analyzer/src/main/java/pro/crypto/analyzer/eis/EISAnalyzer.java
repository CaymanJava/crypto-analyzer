package pro.crypto.analyzer.eis;

import pro.crypto.indicator.eis.BarColor;
import pro.crypto.indicator.eis.EISResult;
import pro.crypto.model.Analyzer;
import pro.crypto.model.AnalyzerRequest;
import pro.crypto.model.Signal;

import java.util.stream.IntStream;

import static java.util.Objects.isNull;
import static pro.crypto.model.Signal.BUY;
import static pro.crypto.model.Signal.NEUTRAL;
import static pro.crypto.model.Signal.SELL;

public class EISAnalyzer implements Analyzer<EISAnalyzerResult> {

    private final EISResult[] indicatorResults;

    private EISAnalyzerResult[] result;

    public EISAnalyzer(AnalyzerRequest request) {
        this.indicatorResults = (EISResult[]) request.getIndicatorResults();
    }

    @Override
    public void analyze() {
        result = IntStream.range(0, indicatorResults.length)
                .mapToObj(this::fromIndicatorResult)
                .toArray(EISAnalyzerResult[]::new);
    }

    @Override
    public EISAnalyzerResult[] getResult() {
        if (isNull(result)) {
            analyze();
        }
        return result;
    }

    private EISAnalyzerResult fromIndicatorResult(int currentIndex) {
        return new EISAnalyzerResult(indicatorResults[currentIndex].getTime(),
                barColorToSignal(indicatorResults[currentIndex].getBarColor()));
    }

    private Signal barColorToSignal(BarColor barColor) {
        if (isNull(barColor)) {
            return NEUTRAL;
        }

        switch (barColor) {
            case GREEN:
                return BUY;
            case RED:
                return SELL;
            default:
                return NEUTRAL;
        }
    }

}
