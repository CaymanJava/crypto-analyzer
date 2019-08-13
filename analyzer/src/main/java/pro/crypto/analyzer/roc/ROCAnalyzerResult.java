package pro.crypto.analyzer.roc;

import lombok.Value;
import pro.crypto.model.analyzer.SignalStrength;
import pro.crypto.model.result.SignalStrengthResult;

import java.time.LocalDateTime;

@Value
public class ROCAnalyzerResult implements SignalStrengthResult {

    private LocalDateTime time;

    private SignalStrength signalStrength;

}
