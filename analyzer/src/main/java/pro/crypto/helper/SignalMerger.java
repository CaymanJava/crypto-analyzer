package pro.crypto.helper;

import pro.crypto.model.analyzer.Signal;

import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;
import static pro.crypto.model.analyzer.Signal.NEUTRAL;

public class SignalMerger {

    public Signal merge(Signal firstSignal, Signal secondSignal, Signal thirdSignal) {
        if (isSignalNeutral(firstSignal)) {
            return mergeTwoSignals(secondSignal, thirdSignal);
        }

        if (isSignalNeutral(secondSignal)) {
            return mergeTwoSignals(firstSignal, thirdSignal);
        }

        if (isSignalNeutral(thirdSignal)) {
            return mergeTwoSignals(firstSignal, secondSignal);
        }

        return mergeAllSignals(firstSignal, secondSignal, thirdSignal);
    }

    public Signal merge(Signal firstSignal, Signal secondSignal) {
        return mergeTwoSignals(firstSignal, secondSignal);
    }

    private boolean isSignalNeutral(Signal signal) {
        return isNull(signal) || signal == NEUTRAL;
    }

    private Signal mergeAllSignals(Signal firstSignal, Signal secondSignal, Signal thirdSignal) {
        Signal firstTwoSignals = mergeTwoSignals(firstSignal, secondSignal);
        return firstTwoSignals == NEUTRAL
                ? NEUTRAL
                : mergeTwoSignals(firstTwoSignals, thirdSignal);
    }

    private Signal mergeTwoSignals(Signal firstSignal, Signal secondSignal) {
        if (nonNull(firstSignal) && nonNull(secondSignal)) {
            return mergeSignals(firstSignal, secondSignal);
        }

        if (nonNull(secondSignal)) {
            return secondSignal;
        }

        if (nonNull(firstSignal)) {
            return firstSignal;
        }

        return NEUTRAL;
    }

    private Signal mergeSignals(Signal firstSignal, Signal secondSignal) {
        return firstSignal == secondSignal
                ? firstSignal
                : mergeDifferentSignals(firstSignal, secondSignal);
    }

    private Signal mergeDifferentSignals(Signal firstSignal, Signal secondSignal) {
        if (firstSignal == NEUTRAL) {
            return secondSignal;
        }

        if (secondSignal == NEUTRAL) {
            return firstSignal;
        }

        return NEUTRAL;
    }

}
