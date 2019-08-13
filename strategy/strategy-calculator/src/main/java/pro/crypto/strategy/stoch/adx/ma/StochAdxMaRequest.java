package pro.crypto.strategy.stoch.adx.ma;

import lombok.Builder;
import lombok.Data;
import pro.crypto.model.indicator.IndicatorType;
import pro.crypto.model.strategy.Position;
import pro.crypto.model.tick.Tick;
import pro.crypto.request.StrategyRequest;

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

    private Double stochasticSignalLine;

    private Double adxSignalLine;

    private Set<Position> positions;

}
