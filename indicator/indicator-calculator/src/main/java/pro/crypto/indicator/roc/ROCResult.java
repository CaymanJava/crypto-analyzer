package pro.crypto.indicator.roc;

import lombok.Value;
import pro.crypto.response.SimpleIndicatorResult;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Value
public class ROCResult implements SimpleIndicatorResult {

    private LocalDateTime time;

    private BigDecimal indicatorValue;

}
