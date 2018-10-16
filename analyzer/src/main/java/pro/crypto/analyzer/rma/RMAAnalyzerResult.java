package pro.crypto.analyzer.rma;

import lombok.Value;
import pro.crypto.model.SignalStrength;
import pro.crypto.model.Trend;
import pro.crypto.model.result.SignalStrengthResult;
import pro.crypto.model.result.TrendResult;

import java.time.LocalDateTime;

@Value
public class RMAAnalyzerResult implements SignalStrengthResult, TrendResult {

    private LocalDateTime time;

    private SignalStrength signalStrength;

    private Trend trend;

}
