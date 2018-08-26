package pro.crypto.analyzer.bb;

import lombok.Value;
import pro.crypto.model.result.CrossBandResult;

import java.time.LocalDateTime;

@Value
public class BBAnalyzerResult implements CrossBandResult {

    private LocalDateTime time;

    private boolean crossUpperBand;

    private boolean crossLowerBand;

    private boolean crossMiddleBand;

}
