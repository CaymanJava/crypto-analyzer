package pro.crypto.strategy.stoch.adx.ma;

import lombok.Builder;
import lombok.Data;
import pro.crypto.model.Position;
import pro.crypto.model.StrategyResult;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Set;

@Data
@Builder
public class StochADXMAResult implements StrategyResult {

    private LocalDateTime time;

    private Set<Position> positions;

    private BigDecimal stopLose;

}
