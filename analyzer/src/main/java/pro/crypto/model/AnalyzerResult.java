package pro.crypto.model;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public interface AnalyzerResult {

    LocalDateTime getTime();

    Signal getSignal();

    BigDecimal getIndicatorValue();

    BigDecimal getClosePrice();

}
