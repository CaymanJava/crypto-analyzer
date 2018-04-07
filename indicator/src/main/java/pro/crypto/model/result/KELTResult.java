package pro.crypto.model.result;

import lombok.AllArgsConstructor;
import lombok.Data;
import pro.crypto.model.IndicatorResult;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
public class KELTResult implements IndicatorResult {

    private LocalDateTime time;

    private BigDecimal basis;

    private BigDecimal upperEnvelope;

    private BigDecimal lowerEnvelope;

}