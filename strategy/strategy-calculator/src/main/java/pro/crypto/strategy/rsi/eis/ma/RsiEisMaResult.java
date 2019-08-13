package pro.crypto.strategy.rsi.eis.ma;

import lombok.Builder;
import lombok.Data;
import pro.crypto.indicator.eis.BarColor;
import pro.crypto.model.strategy.Position;
import pro.crypto.model.tick.Tick;
import pro.crypto.response.StrategyResult;

import java.math.BigDecimal;
import java.util.Set;

@Data
@Builder
public class RsiEisMaResult implements StrategyResult {

    private Tick tick;

    private Set<Position> positions;

    private BigDecimal rsiResult;

    private BarColor eisBarColor;

    private BigDecimal fastMaResult;

    private BigDecimal slowMaResult;

}
