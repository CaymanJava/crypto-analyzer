package pro.crypto.analyzer.cmf;

import lombok.Value;
import pro.crypto.model.analyzer.Signal;
import pro.crypto.model.analyzer.Trend;
import pro.crypto.model.result.SignalResult;
import pro.crypto.model.result.TrendResult;

import java.time.LocalDateTime;

@Value
public class CMFAnalyzerResult implements TrendResult, SignalResult {

    private LocalDateTime time;

    private Signal signal;

    private Trend trend;

}
