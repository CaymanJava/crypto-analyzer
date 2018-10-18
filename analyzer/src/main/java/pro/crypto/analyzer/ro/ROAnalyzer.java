package pro.crypto.analyzer.ro;

import pro.crypto.indicator.ro.ROResult;
import pro.crypto.model.*;

import java.math.BigDecimal;
import java.util.stream.IntStream;

import static java.math.BigDecimal.ZERO;
import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;
import static pro.crypto.model.Signal.*;
import static pro.crypto.model.Strength.NORMAL;
import static pro.crypto.model.Strength.STRONG;
import static pro.crypto.model.Trend.*;

public class ROAnalyzer implements Analyzer<ROAnalyzerResult> {

    private final static BigDecimal MIN_UPTREND_ENVELOPE_LEVEL = new BigDecimal(30);
    private final static BigDecimal MAX_UPTREND_ENVELOPE_LEVEL = new BigDecimal(60);
    private final static BigDecimal MIN_DOWNTREND_ENVELOPE_LEVEL = new BigDecimal(-60);
    private final static BigDecimal MAX_DOWNTREND_ENVELOPE_LEVEL = new BigDecimal(-30);
    private final static BigDecimal BUY_ACCEPTABLE_ENVELOPE_LEVEL = new BigDecimal(38);
    private final static BigDecimal SELL_ACCEPTABLE_ENVELOPE_LEVEL = new BigDecimal(-38);

    private final ROResult[] indicatorResults;
    private Trend[] trends;

    private ROAnalyzerResult[] result;

    public ROAnalyzer(AnalyzerRequest request) {
        this.indicatorResults = (ROResult[]) request.getIndicatorResults();
    }

    @Override
    public void analyze() {
        defineTrends();
        SignalStrength[] signals = defineSignals();
        buildROAnalyzerResult(signals);
    }

    @Override
    public ROAnalyzerResult[] getResult() {
        if (isNull(result)) {
            analyze();
        }
        return result;
    }

    private SignalStrength[] defineSignals() {
        return IntStream.range(0, indicatorResults.length)
                .mapToObj(this::defineSignal)
                .toArray(SignalStrength[]::new);
    }

    private SignalStrength defineSignal(int currentIndex) {
        return isPossibleDefineSignal(currentIndex)
                ? findSignal(currentIndex)
                : new SignalStrength(NEUTRAL, Strength.UNDEFINED);
    }

    private boolean isPossibleDefineSignal(int currentIndex) {
        return currentIndex > 1
                && nonNull(indicatorResults[currentIndex - 2].getIndicatorValue())
                && nonNull(indicatorResults[currentIndex - 1].getIndicatorValue())
                && nonNull(indicatorResults[currentIndex].getIndicatorValue())
                && nonNull(indicatorResults[currentIndex].getUpperEnvelope());
    }

    private SignalStrength findSignal(int currentIndex) {
        if (isBuySignal(currentIndex)) {
            return new SignalStrength(BUY, defineBuyStrength(currentIndex));
        }

        if (isSellSignal(currentIndex)) {
            return new SignalStrength(SELL, defineSellStrength(currentIndex));
        }

        return new SignalStrength(NEUTRAL, Strength.UNDEFINED);
    }

    private boolean isBuySignal(int currentIndex) {
        return indicatorResults[currentIndex].getIndicatorValue().compareTo(ZERO) > 0
                && indicatorResults[currentIndex].getIndicatorValue().compareTo(indicatorResults[currentIndex - 1].getIndicatorValue()) > 0
                && indicatorResults[currentIndex].getIndicatorValue().compareTo(indicatorResults[currentIndex - 2].getIndicatorValue()) > 0
                && indicatorResults[currentIndex].getUpperEnvelope().compareTo(BUY_ACCEPTABLE_ENVELOPE_LEVEL) < 0;
    }

    private Strength defineBuyStrength(int currentIndex) {
        return trends[currentIndex] == UPTREND
                ? STRONG
                : NORMAL;
    }

    private boolean isSellSignal(int currentIndex) {
        return indicatorResults[currentIndex].getIndicatorValue().compareTo(ZERO) < 0
                && indicatorResults[currentIndex].getIndicatorValue().compareTo(indicatorResults[currentIndex - 1].getIndicatorValue()) < 0
                && indicatorResults[currentIndex].getIndicatorValue().compareTo(indicatorResults[currentIndex - 2].getIndicatorValue()) < 0
                && indicatorResults[currentIndex].getLowerEnvelope().compareTo(SELL_ACCEPTABLE_ENVELOPE_LEVEL) > 0;
    }

    private Strength defineSellStrength(int currentIndex) {
        return trends[currentIndex] == DOWNTREND
                ? STRONG
                : NORMAL;
    }

    private void defineTrends() {
        trends = IntStream.range(0, indicatorResults.length)
                .mapToObj(this::tryDefineTrend)
                .toArray(Trend[]::new);
    }

    private Trend tryDefineTrend(int currentIndex) {
        return isPossibleDefineTrend(currentIndex)
                ? defineTrend(currentIndex)
                : UNDEFINED;
    }

    private boolean isPossibleDefineTrend(int currentIndex) {
        return nonNull(indicatorResults[currentIndex].getIndicatorValue())
                && nonNull(indicatorResults[currentIndex].getUpperEnvelope());
    }

    private Trend defineTrend(int currentIndex) {
        if (isUptrend(currentIndex)) {
            return UPTREND;
        }

        if (isDowntrend(currentIndex)) {
            return DOWNTREND;
        }

        return CONSOLIDATION;
    }

    private boolean isUptrend(int currentIndex) {
        return indicatorResults[currentIndex].getIndicatorValue().compareTo(ZERO) > 0
                && indicatorResults[currentIndex].getUpperEnvelope().compareTo(MIN_UPTREND_ENVELOPE_LEVEL) > 0
                && indicatorResults[currentIndex].getUpperEnvelope().compareTo(MAX_UPTREND_ENVELOPE_LEVEL) < 0;
    }

    private boolean isDowntrend(int currentIndex) {
        return indicatorResults[currentIndex].getIndicatorValue().compareTo(ZERO) < 0
                && indicatorResults[currentIndex].getLowerEnvelope().compareTo(MIN_DOWNTREND_ENVELOPE_LEVEL) > 0
                && indicatorResults[currentIndex].getLowerEnvelope().compareTo(MAX_DOWNTREND_ENVELOPE_LEVEL) < 0;
    }

    private void buildROAnalyzerResult(SignalStrength[] signals) {
        result = IntStream.range(0, indicatorResults.length)
                .mapToObj(idx -> new ROAnalyzerResult(indicatorResults[idx].getTime(), signals[idx], trends[idx]))
                .toArray(ROAnalyzerResult[]::new);
    }

}
