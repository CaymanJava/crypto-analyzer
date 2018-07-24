package pro.crypto.analyzer.ac;

import lombok.Value;
import pro.crypto.model.AnalyzerResult;
import pro.crypto.model.Signal;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Value
public class ACAnalyzeResult implements AnalyzerResult {

    private LocalDateTime time;

    private Signal signal;

    private BigDecimal indicatorValue;

    private BigDecimal closePrice;

}
