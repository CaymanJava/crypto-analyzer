package pro.crypto.analyzer.adl;

import lombok.Value;
import pro.crypto.model.Signal;
import pro.crypto.model.result.SignalResult;

import java.time.LocalDateTime;

@Value
public class ADLAnalyzerResult implements SignalResult {

    private LocalDateTime time;

    private Signal signal;

}
