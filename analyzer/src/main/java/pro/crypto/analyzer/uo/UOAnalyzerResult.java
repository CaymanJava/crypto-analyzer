package pro.crypto.analyzer.uo;

import lombok.Value;
import pro.crypto.model.analyzer.SecurityLevel;
import pro.crypto.model.analyzer.SignalStrength;
import pro.crypto.model.result.SecurityLevelResult;
import pro.crypto.model.result.SignalStrengthResult;

import java.time.LocalDateTime;

@Value
public class UOAnalyzerResult implements SignalStrengthResult, SecurityLevelResult {

    private LocalDateTime time;

    private SignalStrength signalStrength;

    private SecurityLevel securityLevel;

}
