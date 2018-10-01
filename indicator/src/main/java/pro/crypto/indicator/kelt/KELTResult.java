package pro.crypto.indicator.kelt;

import lombok.Value;
import pro.crypto.model.IndicatorBandResult;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Value
public class KELTResult implements IndicatorBandResult {

    private LocalDateTime time;

    private BigDecimal upperBand;

    private BigDecimal middleBand;

    private BigDecimal lowerBand;

}
