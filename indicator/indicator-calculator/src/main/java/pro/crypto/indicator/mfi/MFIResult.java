package pro.crypto.indicator.mfi;

import lombok.Value;
import pro.crypto.model.indicator.IndicatorVolumeCorrelation;
import pro.crypto.response.SimpleIndicatorResult;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Value
public class MFIResult implements SimpleIndicatorResult {

    private LocalDateTime time;

    private BigDecimal indicatorValue;

    private IndicatorVolumeCorrelation correlation;

}
