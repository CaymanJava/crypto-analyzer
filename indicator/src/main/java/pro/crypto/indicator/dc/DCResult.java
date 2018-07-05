package pro.crypto.indicator.dc;

import lombok.Value;
import pro.crypto.model.IndicatorResult;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Value
public class DCResult implements IndicatorResult {

    private LocalDateTime time;

    private BigDecimal basis;

    private BigDecimal upperEnvelope;

    private BigDecimal lowerEnvelope;

}
