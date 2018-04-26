package pro.crypto.model.result;

import lombok.Value;
import pro.crypto.model.IndicatorResult;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Value
public class ROCResult implements IndicatorResult {

    private LocalDateTime time;

    private BigDecimal indicatorValue;

}
