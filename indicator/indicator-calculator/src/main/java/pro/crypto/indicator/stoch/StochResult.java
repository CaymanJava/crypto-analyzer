package pro.crypto.indicator.stoch;

import lombok.Value;
import pro.crypto.response.IndicatorResult;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Value
public class StochResult implements IndicatorResult {

    private LocalDateTime time;

    private BigDecimal fastStochastic; // %K

    private BigDecimal slowStochastic; // %D

}
