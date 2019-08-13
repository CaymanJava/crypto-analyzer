package pro.crypto.model.result;

import pro.crypto.model.analyzer.SignalStrength;

public interface SignalStrengthResult extends AnalyzerResult {

    SignalStrength getSignalStrength();

}
