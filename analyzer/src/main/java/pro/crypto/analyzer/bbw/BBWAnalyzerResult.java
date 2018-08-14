package pro.crypto.analyzer.bbw;

import lombok.Value;
import pro.crypto.model.AnalyzerResult;
import pro.crypto.model.Signal;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Value
public class BBWAnalyzerResult implements AnalyzerResult {

    private LocalDateTime time;

    private Signal signal;

    private BigDecimal indicatorValue;

    private BigDecimal closePrice;

    private Boolean startTrend;

    private Boolean trendExist;

}
