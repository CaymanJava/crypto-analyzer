package pro.crypto.indicator.hlb;

import lombok.Value;
import pro.crypto.model.IndicatorBandResult;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Value
public class HLBResult implements IndicatorBandResult {

    private LocalDateTime time;

    private BigDecimal upperBand;

    private BigDecimal middleBand;

    private BigDecimal lowerBand;

}
