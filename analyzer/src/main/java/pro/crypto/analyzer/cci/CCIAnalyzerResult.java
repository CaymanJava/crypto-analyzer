package pro.crypto.analyzer.cci;

import lombok.Value;
import pro.crypto.model.AnalyzerResult;
import pro.crypto.model.SecurityLevel;
import pro.crypto.model.Signal;
import pro.crypto.model.Strength;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Value
public class CCIAnalyzerResult implements AnalyzerResult {

    private LocalDateTime time;

    private Signal signal;

    private Strength strength;

    private BigDecimal indicatorValue;

    private BigDecimal closePrice;

    private SecurityLevel securityLevel;

}
