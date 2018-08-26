package pro.crypto.analyzer.ac;

import lombok.Value;
import pro.crypto.model.Signal;
import pro.crypto.model.result.SignalResult;

import java.time.LocalDateTime;

@Value
public class ACAnalyzeResult implements SignalResult {

    private LocalDateTime time;

    private Signal signal;

}
