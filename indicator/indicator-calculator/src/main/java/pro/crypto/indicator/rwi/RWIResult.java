package pro.crypto.indicator.rwi;

import lombok.Value;
import pro.crypto.response.IndicatorResult;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Value
public class RWIResult implements IndicatorResult {

    private LocalDateTime time;

    private BigDecimal highValue;

    private BigDecimal lowValue;

}
