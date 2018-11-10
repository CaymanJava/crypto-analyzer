package pro.crypto.strategy.adxstochma;

import lombok.Builder;
import lombok.Data;
import pro.crypto.model.Position;
import pro.crypto.model.StrategyResult;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Set;

@Data
@Builder
public class ADXStochMAResult implements StrategyResult {

    private LocalDateTime time;

    private Set<Position> positions;

    private BigDecimal stopLose;

}
