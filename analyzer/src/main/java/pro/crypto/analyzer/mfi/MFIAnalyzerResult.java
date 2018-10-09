package pro.crypto.analyzer.mfi;

import lombok.Value;
import pro.crypto.model.IndicatorVolumeCorrelation;
import pro.crypto.model.result.IndicatorVolumeCorrelationResult;

import java.time.LocalDateTime;

@Value
public class MFIAnalyzerResult implements IndicatorVolumeCorrelationResult {

    private LocalDateTime time;

    private IndicatorVolumeCorrelation correlation;

}
