package pro.crypto.analyzer.macd;

import lombok.Value;
import pro.crypto.model.analyzer.SignalStrength;
import pro.crypto.model.result.SignalStrengthResult;

import java.time.LocalDateTime;

@Value
public class MACDAnalyzerResult implements SignalStrengthResult {

    private LocalDateTime time;

    private SignalStrength signalStrength;

}
