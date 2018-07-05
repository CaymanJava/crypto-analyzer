package pro.crypto.indicator.adx;

import lombok.Value;
import pro.crypto.model.IndicatorResult;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Value
public class ADXResult implements IndicatorResult {

    private LocalDateTime time;

    private BigDecimal positiveDirectionalIndicator;

    private BigDecimal negativeDirectionalIndicator;

    private BigDecimal averageDirectionalIndex;

}
