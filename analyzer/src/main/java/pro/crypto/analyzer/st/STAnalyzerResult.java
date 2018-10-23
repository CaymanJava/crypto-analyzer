package pro.crypto.analyzer.st;

import lombok.Value;
import pro.crypto.model.Signal;
import pro.crypto.model.Trend;
import pro.crypto.model.result.SignalResult;
import pro.crypto.model.result.TrendResult;

import java.time.LocalDateTime;

@Value
public class STAnalyzerResult implements SignalResult, TrendResult {

    private LocalDateTime time;

    private Signal signal;

    private Trend trend;

}
