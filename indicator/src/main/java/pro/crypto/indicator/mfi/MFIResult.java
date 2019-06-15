package pro.crypto.indicator.mfi;

import lombok.Value;
import pro.crypto.model.IndicatorVolumeCorrelation;
import pro.crypto.model.IndicatorVolumeCorrelationResult;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Value
public class MFIResult implements IndicatorVolumeCorrelationResult {

    private LocalDateTime time;

    private BigDecimal indicatorValue;

    private IndicatorVolumeCorrelation correlation;

}
