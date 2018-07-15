package pro.crypto.indicator.smi;


import lombok.Value;
import pro.crypto.model.SimpleIndicatorResult;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Value
public class SMIResult implements SimpleIndicatorResult {

    private LocalDateTime time;

    private BigDecimal indicatorValue;

    private BigDecimal signalLineValue;

}
