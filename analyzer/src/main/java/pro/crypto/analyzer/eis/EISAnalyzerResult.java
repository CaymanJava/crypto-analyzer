package pro.crypto.analyzer.eis;

import lombok.Value;
import pro.crypto.model.Signal;
import pro.crypto.model.result.SignalResult;

import java.time.LocalDateTime;

@Value
public class EISAnalyzerResult implements SignalResult {

    private LocalDateTime time;

    private Signal signal;

}
