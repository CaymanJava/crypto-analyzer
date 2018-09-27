package pro.crypto.model;

import pro.crypto.helper.SignalMerger;
import pro.crypto.helper.SignalStrengthMerger;
import pro.crypto.model.result.AnalyzerResult;

import java.util.stream.IntStream;

import static java.util.Objects.nonNull;
import static pro.crypto.model.Signal.NEUTRAL;

public interface Analyzer<T extends AnalyzerResult> {

    void analyze();

    T[] getResult();

    default Signal removeFalsePositiveSignal(Signal signal, Signal falsePositive) {
        return signal != falsePositive ? signal : null;
    }

    default SignalStrength toSignalStrength(Signal signal, Strength strength) {
        return nonNull(signal) && signal != NEUTRAL
                ? new SignalStrength(signal, strength)
                : null;
    }

    default SignalStrength[] mergeSignalsStrength(SignalStrength[] firstSignals, SignalStrength[] secondSignals, SignalStrength[] thirdSignals) {
        SignalStrengthMerger signalStrengthMerger = new SignalStrengthMerger();
        return IntStream.range(0, firstSignals.length)
                .mapToObj(idx -> signalStrengthMerger.merge(firstSignals[idx], secondSignals[idx], thirdSignals[idx]))
                .toArray(SignalStrength[]::new);
    }

    default SignalStrength[] mergeSignalsStrength(SignalStrength[] firstSignals, SignalStrength[] secondSignals) {
        SignalStrengthMerger signalStrengthMerger = new SignalStrengthMerger();
        return IntStream.range(0, firstSignals.length)
                .mapToObj(idx -> signalStrengthMerger.merge(firstSignals[idx], secondSignals[idx]))
                .toArray(SignalStrength[]::new);
    }

    default Signal[] mergeSignals(Signal[] firstSignals, Signal[] secondSignals, Signal[] thirdSignals) {
        SignalMerger signalMerger = new SignalMerger();
        return IntStream.range(0, firstSignals.length)
                .mapToObj(idx -> signalMerger.merge(firstSignals[idx], secondSignals[idx], thirdSignals[idx]))
                .toArray(Signal[]::new);
    }

    default Signal[] mergeSignals(Signal[] firstSignals, Signal[] sellSignals) {
        SignalMerger signalMerger = new SignalMerger();
        return IntStream.range(0, firstSignals.length)
                .mapToObj(idx -> signalMerger.merge(firstSignals[idx], sellSignals[idx]))
                .toArray(Signal[]::new);
    }

}
