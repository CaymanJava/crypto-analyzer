package pro.crypto.indicator.ic;

import lombok.Value;
import pro.crypto.model.IndicatorResult;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Value
public class ICResult implements IndicatorResult {

    private LocalDateTime time;

    private BigDecimal conversionLineValue; // Tenkan-sen

    private BigDecimal baseLineValue; // Kijun-sen

    private BigDecimal leadingSpanAValue; // Senkou Span A

    private BigDecimal leadingSpanBValue; // Senkou Span B

    private BigDecimal laggingSpanValue; // Chinkou Span

}
