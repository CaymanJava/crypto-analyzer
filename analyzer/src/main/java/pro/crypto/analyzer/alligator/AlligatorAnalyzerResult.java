package pro.crypto.analyzer.alligator;

import lombok.Builder;
import lombok.Value;
import pro.crypto.model.AnalyzerResult;
import pro.crypto.model.Signal;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Value
@Builder
public class AlligatorAnalyzerResult implements AnalyzerResult {

    private LocalDateTime time;

    private Signal signal;

    private BigDecimal indicatorValue;

    private BigDecimal closePrice;

    private int awakePeriods;

    private boolean trend;

}
