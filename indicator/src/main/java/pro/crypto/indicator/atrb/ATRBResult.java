package pro.crypto.indicator.atrb;

import lombok.Value;
import pro.crypto.model.IndicatorResult;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Value
public class ATRBResult implements IndicatorResult{

    private LocalDateTime time;

    private BigDecimal upperBand;

    private BigDecimal middleBand;

    private BigDecimal lowerBand;

}
