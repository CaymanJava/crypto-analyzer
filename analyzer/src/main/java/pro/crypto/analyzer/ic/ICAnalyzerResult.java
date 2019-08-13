package pro.crypto.analyzer.ic;

import lombok.Value;
import pro.crypto.model.analyzer.SignalStrength;
import pro.crypto.model.analyzer.TrendStrength;
import pro.crypto.model.result.SignalStrengthResult;
import pro.crypto.model.result.TrendStrengthResult;

import java.time.LocalDateTime;

@Value
public class ICAnalyzerResult implements SignalStrengthResult, TrendStrengthResult {

    private LocalDateTime time;

    private SignalStrength signalStrength;

    private TrendStrength trendStrength;

}
