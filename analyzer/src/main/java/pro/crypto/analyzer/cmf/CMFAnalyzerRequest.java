package pro.crypto.analyzer.cmf;

import lombok.*;
import pro.crypto.model.AnalyzerRequest;
import pro.crypto.model.IndicatorResult;
import pro.crypto.model.tick.Tick;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(callSuper = false)
public class CMFAnalyzerRequest extends AnalyzerRequest {

    private Tick[] originalData;

    private IndicatorResult[] indicatorResults;

    private Double bullishSignalLine;

    private Double bearerSignalLine;

}
