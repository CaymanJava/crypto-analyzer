package pro.crypto.analyzer.kelt;

import lombok.Value;
import pro.crypto.model.analyzer.SignalStrength;
import pro.crypto.model.result.CrossBandResult;
import pro.crypto.model.result.SignalStrengthResult;

import java.time.LocalDateTime;

@Value
public class KELTAnalyzerResult implements SignalStrengthResult, CrossBandResult {

    private LocalDateTime time;

    private SignalStrength signalStrength;

    private boolean crossUpperBand;

    private boolean crossLowerBand;

    private boolean crossMiddleBand;

}
