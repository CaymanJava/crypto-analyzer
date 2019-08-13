package pro.crypto.strategy.stoch.adx.ma;

import lombok.Builder;
import lombok.Data;
import pro.crypto.model.strategy.Position;
import pro.crypto.model.tick.Tick;
import pro.crypto.response.StrategyResult;

import java.math.BigDecimal;
import java.util.Set;

@Data
@Builder
public class StochAdxMaResult implements StrategyResult {

    private Tick tick;

    private Set<Position> positions;

    private BigDecimal stopLose;

    private BigDecimal fastStochastic;

    private BigDecimal slowStochastic;

    private BigDecimal positiveAdxResult;

    private BigDecimal negativeAdxResult;

    private BigDecimal averageAdxResult;

    private BigDecimal firstMaResult;

    private BigDecimal secondMaResult;

    private BigDecimal thirdMaResult;

}
