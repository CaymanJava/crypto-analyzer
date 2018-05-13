package pro.crypto.model.result;

import lombok.Value;
import pro.crypto.model.IndicatorResult;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Value
public class PivotResult implements IndicatorResult {

    private LocalDateTime time;

    private BigDecimal pivot;

    private BigDecimal firstResistance;

    private BigDecimal secondResistance;

    private BigDecimal thirdResistance;

    private BigDecimal fourthResistance;

    private BigDecimal firstSupport;

    private BigDecimal secondSupport;

    private BigDecimal thirdSupport;

    private BigDecimal fourthSupport;

}
