package pro.crypto.strategy.stoch.adx.ma;

import lombok.Builder;
import lombok.Data;
import pro.crypto.model.IndicatorType;
import pro.crypto.model.Position;
import pro.crypto.model.StrategyRequest;
import pro.crypto.model.tick.Tick;

import java.util.Set;

@Data
@Builder
public class StochAdxMaRequest implements StrategyRequest {

    private Tick[] originalData;

    private IndicatorType stochMovingAverageType;

    private int stochFastPeriod;

    private int stochSlowPeriod;

    private int adxPeriod;

    private int firstMaPeriod;

    private int secondMaPeriod;

    private int thirdMaPeriod;

    private Set<Position> positions;

}
