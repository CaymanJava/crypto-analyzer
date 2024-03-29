package pro.crypto.analyzer.ac;

import pro.crypto.indicator.ac.ACResult;
import pro.crypto.model.Analyzer;
import pro.crypto.model.AnalyzerRequest;
import pro.crypto.model.analyzer.Signal;

import java.math.BigDecimal;
import java.util.stream.IntStream;

import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;
import static pro.crypto.model.analyzer.Signal.BUY;
import static pro.crypto.model.analyzer.Signal.NEUTRAL;
import static pro.crypto.model.analyzer.Signal.SELL;

public class ACAnalyzer implements Analyzer<ACAnalyzerResult> {

    private final ACResult[] indicatorResults;

    private ACAnalyzerResult[] result;

    public ACAnalyzer(AnalyzerRequest request) {
        this.indicatorResults = (ACResult[]) request.getIndicatorResults();
    }

    @Override
    public void analyze() {
        Signal[] signals = recognizeSignals();
        buildACResult(signals);
    }

    @Override
    public ACAnalyzerResult[] getResult() {
        if (isNull(result)) {
            analyze();
        }
        return result;
    }

    private Signal[] recognizeSignals() {
        return IntStream.range(0, indicatorResults.length)
                .mapToObj(this::recognizeSignal)
                .toArray(Signal[]::new);
    }

    private Signal recognizeSignal(int currentIndex) {
        return currentIndex != 0 && nonNull(indicatorResults[currentIndex].getIndicatorValue()) && nonNull(indicatorResults[currentIndex].getIncreased())
                ? recognizeGeneralCaseSignal(currentIndex)
                : NEUTRAL;
    }

    private Signal recognizeGeneralCaseSignal(int currentIndex) {
        return indicatorResults[currentIndex].getIndicatorValue().compareTo(BigDecimal.ZERO) > 0
                ? recognizePositiveCaseSignal(currentIndex)
                : recognizeNegativeCaseSignal(currentIndex);
    }

    private Signal recognizePositiveCaseSignal(int currentIndex) {
        if (isPossibleToRecognizeTrendSignal(currentIndex)) {
            if (indicatorGrowsTwoPeriods(currentIndex)) {
                return BUY;
            }
        }
        if (isPossibleToRecognizeAgainstTrendSignal(currentIndex)) {
            if (indicatorFallsThreePeriods(currentIndex)) {
                return SELL;
            }
        }
        return NEUTRAL;
    }

    private Signal recognizeNegativeCaseSignal(int currentIndex) {
        if (isPossibleToRecognizeTrendSignal(currentIndex)) {
            if (indicatorFallsTwoPeriods(currentIndex)) {
                return SELL;
            }
        }
        if (isPossibleToRecognizeAgainstTrendSignal(currentIndex)) {
            if (indicatorGrowsThreePeriods(currentIndex)) {
                return BUY;
            }
        }
        return NEUTRAL;
    }

    private boolean isPossibleToRecognizeTrendSignal(int currentIndex) {
        return currentIndex - 1 >= 0
                && nonNull(indicatorResults[currentIndex - 1].getIndicatorValue())
                && nonNull(indicatorResults[currentIndex - 1].getIncreased());
    }

    private boolean isPossibleToRecognizeAgainstTrendSignal(int currentIndex) {
        return currentIndex - 2 >= 0
                && nonNull(indicatorResults[currentIndex - 2].getIndicatorValue())
                && nonNull(indicatorResults[currentIndex - 2].getIncreased());
    }

    private boolean indicatorGrowsTwoPeriods(int currentIndex) {
        return indicatorResults[currentIndex].getIncreased() && indicatorResults[currentIndex - 1].getIncreased();
    }

    private boolean indicatorFallsThreePeriods(int currentIndex) {
        return !indicatorResults[currentIndex].getIncreased()
                && !indicatorResults[currentIndex - 1].getIncreased()
                && !indicatorResults[currentIndex - 2].getIncreased();
    }

    private boolean indicatorFallsTwoPeriods(int currentIndex) {
        return !indicatorResults[currentIndex].getIncreased()
                && !indicatorResults[currentIndex - 1].getIncreased();
    }

    private boolean indicatorGrowsThreePeriods(int currentIndex) {
        return indicatorResults[currentIndex].getIncreased()
                && indicatorResults[currentIndex - 1].getIncreased()
                && indicatorResults[currentIndex - 2].getIncreased();
    }

    private void buildACResult(Signal[] signals) {
        result = IntStream.range(0, signals.length)
                .mapToObj(idx -> new ACAnalyzerResult(indicatorResults[idx].getTime(), signals[idx]))
                .toArray(ACAnalyzerResult[]::new);
    }

}
