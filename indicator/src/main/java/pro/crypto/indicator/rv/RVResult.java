package pro.crypto.indicator.rv;

import lombok.Value;
import pro.crypto.model.SimpleIndicatorResult;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Value
public class RVResult implements SimpleIndicatorResult {

    private LocalDateTime time;

    private BigDecimal indicatorValue;

}
