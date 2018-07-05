package pro.crypto.indicator.rsi;

import lombok.Value;
import pro.crypto.model.SimpleIndicatorResult;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Value
public class RSIResult implements SimpleIndicatorResult{

    private LocalDateTime time;

    private BigDecimal indicatorValue;

}
