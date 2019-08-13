package pro.crypto.analyzer.adx;

import lombok.Builder;
import lombok.Value;
import pro.crypto.model.analyzer.Signal;
import pro.crypto.model.analyzer.TrendStrength;
import pro.crypto.model.result.EntryPointResult;
import pro.crypto.model.result.SignalResult;
import pro.crypto.model.result.TrendStrengthResult;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Value
@Builder
public class ADXAnalyzerResult implements SignalResult, EntryPointResult, TrendStrengthResult {

    private LocalDateTime time;

    private Signal signal;

    private BigDecimal entryPoint;

    private TrendStrength trendStrength;

}
