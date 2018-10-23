package pro.crypto.analyzer.stc;

import lombok.Value;
import pro.crypto.model.Signal;
import pro.crypto.model.result.SignalResult;

import java.time.LocalDateTime;

@Value
public class STCAnalyzerResult implements SignalResult {

    private LocalDateTime time;

    private Signal signal;

}
