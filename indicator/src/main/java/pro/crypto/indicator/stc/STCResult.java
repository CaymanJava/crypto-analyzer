package pro.crypto.indicator.stc;

import lombok.Value;
import pro.crypto.model.SimpleIndicatorResult;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Value
public class STCResult implements SimpleIndicatorResult {

    private LocalDateTime time;

    private BigDecimal indicatorValue;

}
