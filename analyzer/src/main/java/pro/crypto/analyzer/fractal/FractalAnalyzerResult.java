package pro.crypto.analyzer.fractal;

import lombok.AllArgsConstructor;
import lombok.Data;
import pro.crypto.model.analyzer.Signal;
import pro.crypto.model.result.SignalResult;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
public class FractalAnalyzerResult implements SignalResult {

    private LocalDateTime time;

    private Signal signal;

}
