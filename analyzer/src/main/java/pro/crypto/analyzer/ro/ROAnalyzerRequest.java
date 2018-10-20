package pro.crypto.analyzer.ro;

import lombok.*;
import pro.crypto.model.AnalyzerRequest;
import pro.crypto.model.IndicatorResult;
import pro.crypto.model.tick.Tick;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(callSuper = false)
public class ROAnalyzerRequest extends AnalyzerRequest {

    private Tick[] originalData;

    private IndicatorResult[] indicatorResults;

    private Double minUptrendEnvelopeLevel;

    private Double maxUptrendEnvelopeLevel;

    private Double acceptableSignalEnvelopeLevel;

}
