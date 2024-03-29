package pro.crypto.indicator.eri;

import lombok.Value;
import pro.crypto.response.SignalLineIndicatorResult;
import pro.crypto.response.SimpleIndicatorResult;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Value
public class ERIResult implements SimpleIndicatorResult, SignalLineIndicatorResult {

    private LocalDateTime time;

    private BigDecimal movingAverageValue;

    private BigDecimal indicatorValue;

    private BigDecimal signalLineValue;

    private BigDecimal smoothedLineValue;

}
