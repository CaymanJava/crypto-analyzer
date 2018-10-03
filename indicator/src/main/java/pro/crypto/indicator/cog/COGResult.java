package pro.crypto.indicator.cog;

import lombok.Value;
import pro.crypto.model.SignalLineIndicatorResult;
import pro.crypto.model.SimpleIndicatorResult;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Value
public class COGResult implements SimpleIndicatorResult, SignalLineIndicatorResult {

    private LocalDateTime time;

    private BigDecimal indicatorValue;

    private BigDecimal signalLineValue;

}
