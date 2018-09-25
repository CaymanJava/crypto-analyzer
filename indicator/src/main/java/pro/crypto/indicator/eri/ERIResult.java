package pro.crypto.indicator.eri;

import lombok.Value;
import pro.crypto.model.SimpleIndicatorResult;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Value
public class ERIResult implements SimpleIndicatorResult {

    private LocalDateTime time;

    private BigDecimal movingAverageValue;

    private BigDecimal indicatorValue;

    private BigDecimal signalLineValue;

    private BigDecimal smoothedLineValue;

}
