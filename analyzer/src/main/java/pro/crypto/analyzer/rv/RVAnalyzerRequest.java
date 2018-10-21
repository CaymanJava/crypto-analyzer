package pro.crypto.analyzer.rv;

import lombok.*;
import pro.crypto.model.AnalyzerRequest;
import pro.crypto.model.IndicatorResult;
import pro.crypto.model.tick.Tick;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(callSuper = false)
public class RVAnalyzerRequest extends AnalyzerRequest {

    private Tick[] originalData;

    private IndicatorResult[] indicatorResults;

    private Double bullishSignalLine;

    private Double bearerSignalLine;

}
