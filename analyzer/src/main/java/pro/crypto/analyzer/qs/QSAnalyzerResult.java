package pro.crypto.analyzer.qs;

import lombok.Value;
import pro.crypto.model.analyzer.SignalStrength;
import pro.crypto.model.result.SignalStrengthResult;

import java.time.LocalDateTime;

@Value
public class QSAnalyzerResult implements SignalStrengthResult {

    private LocalDateTime time;

    private SignalStrength signalStrength;

}
