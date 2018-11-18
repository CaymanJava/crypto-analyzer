package pro.crypto.strategy.stoch.ac.ma;

import lombok.Builder;
import lombok.Data;
import pro.crypto.model.IndicatorType;
import pro.crypto.model.Position;
import pro.crypto.model.StrategyRequest;
import pro.crypto.model.tick.PriceType;
import pro.crypto.model.tick.Tick;

import java.util.Set;

@Data
@Builder
public class StochACMARequest implements StrategyRequest {

    private Tick[] originalData;

    private IndicatorType stochMovingAverageType;

    private int stochFastPeriod;

    private int stochSlowPeriod;

    private double stochOversoldLevel;

    private double stochOverboughtLevel;

    private int acSlowPeriod;

    private int acFastPeriod;

    private int acSmoothedPeriod;

    private int maPeriod;

    private IndicatorType movingAverageType;

    private PriceType maPriceType;

    private Set<Position> positions;

}
