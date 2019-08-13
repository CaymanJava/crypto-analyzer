package pro.crypto.indicator.dc;

import lombok.Value;
import pro.crypto.response.IndicatorBandResult;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Value
public class DCResult implements IndicatorBandResult {

    private LocalDateTime time;

    private BigDecimal middleBand;

    private BigDecimal upperBand;

    private BigDecimal lowerBand;

}
