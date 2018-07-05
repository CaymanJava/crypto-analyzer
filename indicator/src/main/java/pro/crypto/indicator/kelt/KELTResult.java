package pro.crypto.indicator.kelt;

import lombok.Value;
import pro.crypto.model.IndicatorResult;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Value
public class KELTResult implements IndicatorResult {

    private LocalDateTime time;

    private BigDecimal basis;

    private BigDecimal upperEnvelope;

    private BigDecimal lowerEnvelope;

}