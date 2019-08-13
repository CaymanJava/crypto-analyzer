package pro.crypto.analyzer.pvt;

import lombok.Value;
import pro.crypto.model.analyzer.SignalStrength;
import pro.crypto.model.result.SignalStrengthResult;

import java.time.LocalDateTime;

@Value
public class PVTAnalyzerResult implements SignalStrengthResult {

    private LocalDateTime time;

    private SignalStrength signalStrength;

}
