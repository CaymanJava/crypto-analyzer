package pro.crypto.model.result;

import pro.crypto.model.analyzer.Signal;

public interface SignalResult extends AnalyzerResult {

    Signal getSignal();

}
