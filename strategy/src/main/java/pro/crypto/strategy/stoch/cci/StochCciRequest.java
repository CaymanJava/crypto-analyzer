package pro.crypto.strategy.stoch.cci;

import lombok.Builder;
import lombok.Data;
import pro.crypto.model.IndicatorType;
import pro.crypto.model.Position;
import pro.crypto.model.StrategyRequest;
import pro.crypto.model.tick.Tick;

import java.util.Set;

@Data
@Builder
public class StochCciRequest implements StrategyRequest {

    private Tick[] originalData;

    private IndicatorType stochMovingAverageType;

    private int fastStochPeriod;

    private int slowStochPeriod;

    private Double stochOversoldLevel;

    private Double stochOverboughtLevel;

    private int cciPeriod;

    private Double cciOversoldLevel;

    private Double cciOverboughtLevel;

    private Set<Position> positions;

}
