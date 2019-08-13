package pro.crypto.indicator.alligator;

import lombok.Value;
import pro.crypto.response.IndicatorResult;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Value
public class AlligatorResult implements IndicatorResult {

    private LocalDateTime time;

    private BigDecimal jawValue;

    private BigDecimal teethValue;

    private BigDecimal lipsValue;

}
