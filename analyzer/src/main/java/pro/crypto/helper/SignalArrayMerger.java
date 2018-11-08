package pro.crypto.helper;

import pro.crypto.model.Signal;
import pro.crypto.model.SignalStrength;
import pro.crypto.model.Strength;

import java.util.stream.IntStream;

import static java.util.Objects.nonNull;
import static pro.crypto.model.Signal.NEUTRAL;

public class SignalArrayMerger {

    public static SignalStrength toSignalStrength(Signal signal, Strength strength) {
        return nonNull(signal) && signal != NEUTRAL
                ? new SignalStrength(signal, strength)
                : null;
    }

    public static SignalStrength[] mergeSignalsStrength(SignalStrength[] firstSignals, SignalStrength[] secondSignals, SignalStrength[] thirdSignals) {
        SignalStrengthMerger signalStrengthMerger = new SignalStrengthMerger();
        return IntStream.range(0, firstSignals.length)
                .mapToObj(idx -> signalStrengthMerger.merge(firstSignals[idx], secondSignals[idx], thirdSignals[idx]))
                .toArray(SignalStrength[]::new);
    }

    public static SignalStrength[] mergeSignalsStrength(SignalStrength[] firstSignals, SignalStrength[] secondSignals) {
        SignalStrengthMerger signalStrengthMerger = new SignalStrengthMerger();
        return IntStream.range(0, firstSignals.length)
                .mapToObj(idx -> signalStrengthMerger.merge(firstSignals[idx], secondSignals[idx]))
                .toArray(SignalStrength[]::new);
    }

    public static Signal[] mergeSignals(Signal[] firstSignals, Signal[] secondSignals, Signal[] thirdSignals) {
        SignalMerger signalMerger = new SignalMerger();
        return IntStream.range(0, firstSignals.length)
                .mapToObj(idx -> signalMerger.merge(firstSignals[idx], secondSignals[idx], thirdSignals[idx]))
                .toArray(Signal[]::new);
    }

    public static Signal[] mergeSignals(Signal[] firstSignals, Signal[] sellSignals) {
        SignalMerger signalMerger = new SignalMerger();
        return IntStream.range(0, firstSignals.length)
                .mapToObj(idx -> signalMerger.merge(firstSignals[idx], sellSignals[idx]))
                .toArray(Signal[]::new);
    }

}
