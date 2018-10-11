package pro.crypto.analyzer.mi;

import lombok.Value;
import pro.crypto.model.result.TrendReverseResult;

import java.time.LocalDateTime;

@Value
public class MIAnalyzerResult implements TrendReverseResult {

    private LocalDateTime time;

    private boolean trendReverse;

}
