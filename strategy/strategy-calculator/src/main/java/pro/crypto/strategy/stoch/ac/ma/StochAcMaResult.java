package pro.crypto.strategy.stoch.ac.ma;

import lombok.Builder;
import lombok.Data;
import pro.crypto.model.strategy.Position;
import pro.crypto.model.tick.Tick;
import pro.crypto.response.StrategyResult;

import java.math.BigDecimal;
import java.util.Set;

@Data
@Builder
public class StochAcMaResult implements StrategyResult {

    private Tick tick;

    private Set<Position> positions;

    private BigDecimal stopLose;

    private BigDecimal acResult;

    private Boolean acIncreased;

    private BigDecimal fastStochasticAc;

    private BigDecimal slowStochasticAc;

    private BigDecimal maResult;

}
