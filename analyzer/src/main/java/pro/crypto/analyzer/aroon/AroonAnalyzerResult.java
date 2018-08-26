package pro.crypto.analyzer.aroon;

import lombok.Value;
import pro.crypto.model.TrendStrength;
import pro.crypto.model.result.TrendStrengthResult;

import java.time.LocalDateTime;

@Value
public class AroonAnalyzerResult implements TrendStrengthResult {

    private LocalDateTime time;

    private TrendStrength trendStrength;

}
