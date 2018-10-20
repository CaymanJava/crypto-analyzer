package pro.crypto.analyzer.ro;

import pro.crypto.indicator.ro.ROResult;
import pro.crypto.model.*;

import java.math.BigDecimal;
import java.util.stream.IntStream;

import static java.math.BigDecimal.ZERO;
import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;
import static java.util.Optional.ofNullable;
import static pro.crypto.model.Signal.*;
import static pro.crypto.model.Strength.NORMAL;
import static pro.crypto.model.Strength.STRONG;
import static pro.crypto.model.Trend.*;

public class ROAnalyzer implements Analyzer<ROAnalyzerResult> {

    private final ROResult[] indicatorResults;
    private final BigDecimal minUptrendEnvelopeLevel;
    private final BigDecimal maxUptrendEnvelopeLevel;
    private final BigDecimal acceptableSignalEnvelopeLevel;

    private Trend[] trends;
    private ROAnalyzerResult[] result;

    public ROAnalyzer(AnalyzerRequest analyzerRequest) {
        ROAnalyzerRequest request = (ROAnalyzerRequest) analyzerRequest;
        this.indicatorResults = (ROResult[]) request.getIndicatorResults();
        this.minUptrendEnvelopeLevel = extractMinUptrendLevel(request);
        this.maxUptrendEnvelopeLevel = extractMaxUptrendLevel(request);
        this.acceptableSignalEnvelopeLevel = extractAcceptableSignalLevel(request);
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

    private BigDecimal extractMinUptrendLevel(ROAnalyzerRequest request) {
        return ofNullable(request.getMinUptrendEnvelopeLevel())
                .map(BigDecimal::new)
                .orElse(new BigDecimal(30));
    }

    private BigDecimal extractMaxUptrendLevel(ROAnalyzerRequest request) {
        return ofNullable(request.getMaxUptrendEnvelopeLevel())
                .map(BigDecimal::new)
                .orElse(new BigDecimal(60));
    }

    private BigDecimal extractAcceptableSignalLevel(ROAnalyzerRequest request) {
        return ofNullable(request.getAcceptableSignalEnvelopeLevel())
                .map(BigDecimal::new)
                .orElse(new BigDecimal(38));
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
                && indicatorResults[currentIndex].getUpperEnvelope().compareTo(acceptableSignalEnvelopeLevel) < 0;
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
                && indicatorResults[currentIndex].getLowerEnvelope().compareTo(acceptableSignalEnvelopeLevel.negate()) > 0;
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
                && indicatorResults[currentIndex].getUpperEnvelope().compareTo(minUptrendEnvelopeLevel) > 0
                && indicatorResults[currentIndex].getUpperEnvelope().compareTo(maxUptrendEnvelopeLevel) < 0;
    }

    private boolean isDowntrend(int currentIndex) {
        return indicatorResults[currentIndex].getIndicatorValue().compareTo(ZERO) < 0
                && indicatorResults[currentIndex].getLowerEnvelope().compareTo(maxUptrendEnvelopeLevel.negate()) > 0
                && indicatorResults[currentIndex].getLowerEnvelope().compareTo(minUptrendEnvelopeLevel.negate()) < 0;
    }

    private void buildROAnalyzerResult(SignalStrength[] signals) {
        result = IntStream.range(0, indicatorResults.length)
                .mapToObj(idx -> new ROAnalyzerResult(indicatorResults[idx].getTime(), signals[idx], trends[idx]))
                .toArray(ROAnalyzerResult[]::new);
    }

}
