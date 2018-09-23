package pro.crypto.analyzer.env;

import lombok.Value;
import pro.crypto.model.SignalStrength;
import pro.crypto.model.result.CrossBandResult;
import pro.crypto.model.result.SignalStrengthResult;

import java.time.LocalDateTime;

@Value
public class ENVAnalyzerResult implements SignalStrengthResult, CrossBandResult {

    private LocalDateTime time;

    private SignalStrength signalStrength;

    private boolean crossUpperBand;

    private boolean crossLowerBand;

    private boolean crossMiddleBand;

}
