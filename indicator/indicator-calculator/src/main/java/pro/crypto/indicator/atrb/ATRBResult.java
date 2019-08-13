package pro.crypto.indicator.atrb;

import lombok.Value;
import pro.crypto.response.IndicatorBandResult;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Value
public class ATRBResult implements IndicatorBandResult {

    private LocalDateTime time;

    private BigDecimal upperBand;

    private BigDecimal middleBand;

    private BigDecimal lowerBand;

}
