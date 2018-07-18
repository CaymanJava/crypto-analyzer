package pro.crypto.indicator.tmf;

import lombok.Value;
import pro.crypto.model.SimpleIndicatorResult;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Value
public class TMFResult implements SimpleIndicatorResult {

    private LocalDateTime time;

    private BigDecimal indicatorValue;

    private BigDecimal signalLineValue;

}
