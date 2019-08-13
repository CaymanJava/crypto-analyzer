package pro.crypto.indicator.ce;

import lombok.Value;
import pro.crypto.response.IndicatorResult;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Value
public class CEResult implements IndicatorResult {

    private LocalDateTime time;

    private BigDecimal longChandelierExit;

    private BigDecimal shortChandelierExit;

}
