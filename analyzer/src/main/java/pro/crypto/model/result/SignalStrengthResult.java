package pro.crypto.model.result;

import pro.crypto.model.SignalStrength;

public interface SignalStrengthResult extends AnalyzerResult {

    SignalStrength getSignalStrength();

}
