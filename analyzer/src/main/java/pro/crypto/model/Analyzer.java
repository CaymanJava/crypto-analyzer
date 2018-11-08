package pro.crypto.model;

import pro.crypto.model.result.AnalyzerResult;

import static java.util.Objects.nonNull;
import static pro.crypto.model.Signal.NEUTRAL;

public interface Analyzer<T extends AnalyzerResult> {

    void analyze();

    T[] getResult();

    default SignalStrength toSignalStrength(Signal signal, Strength strength) {
        return nonNull(signal) && signal != NEUTRAL
                ? new SignalStrength(signal, strength)
                : null;
    }

}
