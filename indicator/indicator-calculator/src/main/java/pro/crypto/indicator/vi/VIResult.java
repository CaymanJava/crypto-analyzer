package pro.crypto.indicator.vi;

import lombok.Value;
import pro.crypto.response.SignalLineIndicatorResult;
import pro.crypto.response.SimpleIndicatorResult;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Value
public class VIResult implements SimpleIndicatorResult, SignalLineIndicatorResult {

    private LocalDateTime time;

    private BigDecimal indicatorValue;

    private BigDecimal signalLineValue;

}
