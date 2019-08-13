package pro.crypto.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import pro.crypto.model.tick.Tick;
import pro.crypto.response.IndicatorResult;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AnalyzerRequest {

    private Tick[] originalData;

    private IndicatorResult[] indicatorResults;

}
