package pro.crypto.analyzer.ce;

import lombok.Value;
import pro.crypto.model.Signal;
import pro.crypto.model.result.SignalResult;

import java.time.LocalDateTime;

@Value
public class CEAnalyzerResult implements SignalResult {

    private LocalDateTime time;

    private Signal signal;

}
