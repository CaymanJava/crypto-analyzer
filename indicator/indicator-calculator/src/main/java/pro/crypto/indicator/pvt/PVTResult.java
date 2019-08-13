package pro.crypto.indicator.pvt;

import lombok.Value;
import pro.crypto.response.SignalLineIndicatorResult;
import pro.crypto.response.SimpleIndicatorResult;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Value
public class PVTResult implements SimpleIndicatorResult, SignalLineIndicatorResult {

    private LocalDateTime time;

    private BigDecimal indicatorValue;

    private BigDecimal signalLineValue;

}
