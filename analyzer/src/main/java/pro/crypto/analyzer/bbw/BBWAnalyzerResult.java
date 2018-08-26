package pro.crypto.analyzer.bbw;

import lombok.Value;
import pro.crypto.model.result.StartTrendResult;
import pro.crypto.model.result.TrendPresenceResult;

import java.time.LocalDateTime;

@Value
public class BBWAnalyzerResult implements TrendPresenceResult, StartTrendResult {

    private LocalDateTime time;

    private boolean startTrend;

    private boolean trend;

}
