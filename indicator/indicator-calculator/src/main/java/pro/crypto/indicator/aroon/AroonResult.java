package pro.crypto.indicator.aroon;

import lombok.Value;
import pro.crypto.response.IndicatorResult;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Value
public class AroonResult implements IndicatorResult {

    private LocalDateTime time;

    private BigDecimal aroonUp;

    private BigDecimal aroonDown;

    private BigDecimal aroonOscillator;

}
