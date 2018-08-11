package pro.crypto.analyzer.aroon;

import lombok.Value;
import pro.crypto.model.AnalyzerResult;
import pro.crypto.model.Signal;
import pro.crypto.model.Trend;
import pro.crypto.model.Strength;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Value
public class AroonAnalyzerResult implements AnalyzerResult {

    private LocalDateTime time;

    private Signal signal;

    private BigDecimal indicatorValue;

    private BigDecimal closePrice;

    private Trend trend;

    private Strength strength;

}
