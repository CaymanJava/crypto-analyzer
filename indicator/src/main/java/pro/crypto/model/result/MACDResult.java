package pro.crypto.model.result;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
public class MACDResult {

    private LocalDateTime time;

    private BigDecimal originalValue;

    private BigDecimal indicatorValue;

    private BigDecimal signalLineResult;

    private BigDecimal barChartValue;

}
