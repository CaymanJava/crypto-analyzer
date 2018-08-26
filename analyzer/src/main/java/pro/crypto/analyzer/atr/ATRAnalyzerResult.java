package pro.crypto.analyzer.atr;

import lombok.Value;
import pro.crypto.model.result.TrendPresenceResult;
import pro.crypto.model.result.StartTrendResult;

import java.time.LocalDateTime;

@Value
public class ATRAnalyzerResult implements TrendPresenceResult, StartTrendResult {

    private LocalDateTime time;

    private boolean startTrend;

    private boolean trend;

}
