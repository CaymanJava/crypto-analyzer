package pro.crypto.analyzer.cci;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import pro.crypto.model.AnalyzerRequest;
import pro.crypto.model.tick.Tick;
import pro.crypto.response.IndicatorResult;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(callSuper = false)
public class CCIAnalyzerRequest extends AnalyzerRequest {

    private Tick[] originalData;

    private IndicatorResult[] indicatorResults;

    private Double oversoldLevel;

    private Double overboughtLevel;

}
