package pro.crypto.analyzer.dpo;

import lombok.Value;
import pro.crypto.model.analyzer.Signal;
import pro.crypto.model.result.SignalResult;

import java.time.LocalDateTime;

@Value
public class DPOAnalyzerResult implements SignalResult {

    private LocalDateTime time;

    private Signal signal;

}
