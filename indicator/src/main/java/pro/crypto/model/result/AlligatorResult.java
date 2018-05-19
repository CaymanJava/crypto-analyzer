package pro.crypto.model.result;

import lombok.Value;
import pro.crypto.model.IndicatorResult;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Value
public class AlligatorResult implements IndicatorResult {

    private LocalDateTime time;

    private BigDecimal jawValue;

    private BigDecimal teethValue;

    private BigDecimal lipsValue;

}
