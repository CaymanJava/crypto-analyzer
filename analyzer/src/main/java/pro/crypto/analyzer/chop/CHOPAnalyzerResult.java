package pro.crypto.analyzer.chop;

import lombok.Value;
import pro.crypto.model.result.TrendPresenceResult;

import java.time.LocalDateTime;

@Value
public class CHOPAnalyzerResult implements TrendPresenceResult {

    private LocalDateTime time;

    private boolean trend;

}
