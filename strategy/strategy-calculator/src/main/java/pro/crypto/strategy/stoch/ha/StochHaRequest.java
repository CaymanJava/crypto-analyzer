package pro.crypto.strategy.stoch.ha;

import lombok.Builder;
import lombok.Data;
import pro.crypto.model.indicator.IndicatorType;
import pro.crypto.model.strategy.Position;
import pro.crypto.model.tick.Tick;
import pro.crypto.request.StrategyRequest;

import java.util.Set;

@Data
@Builder
public class StochHaRequest implements StrategyRequest {

    private Tick[] originalData;

    private IndicatorType stochMovingAverageType;

    private int stochFastPeriod;

    private int stochSlowPeriod;

    private Double stochOversoldLevel;

    private Double stochOverboughtLevel;

    private Set<Position> positions;

}
