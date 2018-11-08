package pro.crypto.strategy.bws;

import lombok.Builder;
import lombok.Data;
import pro.crypto.model.Position;
import pro.crypto.model.StrategyResult;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Set;

@Data
@Builder
public class BWSResult implements StrategyResult {

    private LocalDateTime time;

    private Set<Position> positions;

    private BigDecimal entryPrice;

    private BigDecimal stopLose;

}
