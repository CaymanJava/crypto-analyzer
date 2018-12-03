package pro.crypto.strategy.cci.rsi.atr;

import lombok.Builder;
import lombok.Data;
import pro.crypto.model.Position;
import pro.crypto.model.StrategyResult;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Set;

@Data
@Builder
public class CciRsiAtrResult implements StrategyResult {

    private LocalDateTime time;

    private BigDecimal stopLose;

    private Set<Position> positions;

}
