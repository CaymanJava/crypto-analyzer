package pro.crypto.model.result;

import lombok.Value;
import pro.crypto.model.IndicatorResult;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Value
public class ICResult implements IndicatorResult {

    private LocalDateTime time;

    private BigDecimal conversionLineValue;

    private BigDecimal baseLineValue;

    private BigDecimal leadingSpanAValue;

    private BigDecimal leadingSpanBValue;

    private BigDecimal laggingSpanValue;

}
