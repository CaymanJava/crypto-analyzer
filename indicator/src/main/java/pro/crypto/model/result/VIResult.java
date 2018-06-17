package pro.crypto.model.result;

import lombok.Value;
import pro.crypto.model.SimpleIndicatorResult;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Value
public class VIResult implements SimpleIndicatorResult {

    private LocalDateTime time;

    private BigDecimal indicatorValue;

    private BigDecimal movingAverageValue;

}
