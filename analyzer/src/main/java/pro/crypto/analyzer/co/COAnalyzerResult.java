package pro.crypto.analyzer.co;

import lombok.Value;
import pro.crypto.model.SignalStrength;
import pro.crypto.model.result.SignalStrengthResult;

import java.time.LocalDateTime;

@Value
public class COAnalyzerResult implements SignalStrengthResult {

    private LocalDateTime time;

    private SignalStrength signalStrength;

}
