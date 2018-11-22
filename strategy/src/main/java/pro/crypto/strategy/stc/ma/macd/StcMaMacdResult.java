package pro.crypto.strategy.stc.ma.macd;

import lombok.Builder;
import lombok.Data;
import pro.crypto.model.Position;
import pro.crypto.model.StrategyResult;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Set;

@Data
@Builder
public class StcMaMacdResult implements StrategyResult {

    private LocalDateTime time;

    private BigDecimal stopLose;

    private Set<Position> positions;

}
