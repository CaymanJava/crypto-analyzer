package pro.crypto.model.result;

import lombok.AllArgsConstructor;
import lombok.Data;
import pro.crypto.model.IndicatorResult;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
public class RSIResult implements IndicatorResult{

    private LocalDateTime time;

    private BigDecimal indicatorValue;

}
