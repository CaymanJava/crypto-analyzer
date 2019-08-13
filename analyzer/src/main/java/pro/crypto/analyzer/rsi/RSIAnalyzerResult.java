package pro.crypto.analyzer.rsi;

import lombok.Value;
import pro.crypto.model.analyzer.SecurityLevel;
import pro.crypto.model.analyzer.Signal;
import pro.crypto.model.result.SecurityLevelResult;
import pro.crypto.model.result.SignalResult;

import java.time.LocalDateTime;

@Value
public class RSIAnalyzerResult implements SignalResult, SecurityLevelResult {

    private LocalDateTime time;

    private Signal signal;

    private SecurityLevel securityLevel;

}
