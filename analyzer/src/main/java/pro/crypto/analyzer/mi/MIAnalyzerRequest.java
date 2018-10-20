package pro.crypto.analyzer.mi;

import lombok.*;
import pro.crypto.model.AnalyzerRequest;
import pro.crypto.model.IndicatorResult;
import pro.crypto.model.tick.Tick;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(callSuper = false)
public class MIAnalyzerRequest extends AnalyzerRequest {

    private Tick[] originalData;

    private IndicatorResult[] indicatorResults;

    private Double firstReversalLine;

    private Double secondReversalLine;

    private Integer allowableGap;

}
