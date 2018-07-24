package pro.crypto.analyzer.ac;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import pro.crypto.model.AnalyzerRequest;
import pro.crypto.model.IndicatorResult;
import pro.crypto.model.tick.Tick;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ACAnalyzeRequest implements AnalyzerRequest {

    private Tick[] originalData;

    private IndicatorResult[] indicatorResults;

}
