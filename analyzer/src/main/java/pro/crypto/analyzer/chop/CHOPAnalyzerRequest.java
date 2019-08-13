package pro.crypto.analyzer.chop;

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
public class CHOPAnalyzerRequest extends AnalyzerRequest {

    private Tick[] originalData;

    private IndicatorResult[] indicatorResults;

    private Double lowerTrendLine;

    private Double upperTrendLine;

}
