package pro.crypto.analyzer.trix;

import lombok.Value;
import pro.crypto.model.SignalStrength;
import pro.crypto.model.result.SignalStrengthResult;

import java.time.LocalDateTime;

@Value
public class TRIXAnalyzerResult implements SignalStrengthResult {

    private LocalDateTime time;

    private SignalStrength signalStrength;

}
