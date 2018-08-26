package pro.crypto.analyzer.alligator;

import lombok.Builder;
import lombok.Value;
import pro.crypto.model.result.TrendPresenceResult;

import java.time.LocalDateTime;

@Value
@Builder
public class AlligatorAnalyzerResult implements TrendPresenceResult {

    private LocalDateTime time;

    private int awakePeriods;

    private boolean trend;

}
