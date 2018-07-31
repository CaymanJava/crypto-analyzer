package pro.crypto.analyzer.adx;

import lombok.Builder;
import lombok.Value;
import pro.crypto.model.AnalyzerResult;
import pro.crypto.model.Signal;
import pro.crypto.model.TrendStrength;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Value
@Builder
public class ADXAnalyzerResult implements AnalyzerResult {

    private LocalDateTime time;

    private Signal signal;

    private BigDecimal indicatorValue;

    private BigDecimal closePrice;

    private BigDecimal entryPoint;

    private TrendStrength trendStrength;

}
