package pro.crypto.analyzer.rsi;

import lombok.Value;
import pro.crypto.model.SecurityLevel;
import pro.crypto.model.Signal;
import pro.crypto.model.result.SecurityLevelResult;
import pro.crypto.model.result.SignalResult;

import java.time.LocalDateTime;

@Value
public class RSIAnalyzerResult implements SignalResult, SecurityLevelResult {

    private LocalDateTime time;

    private Signal signal;

    private SecurityLevel securityLevel;

}
