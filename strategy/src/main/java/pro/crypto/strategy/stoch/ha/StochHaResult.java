package pro.crypto.strategy.stoch.ha;

import lombok.Builder;
import lombok.Data;
import pro.crypto.model.Position;
import pro.crypto.model.StrategyResult;

import java.time.LocalDateTime;
import java.util.Set;

@Data
@Builder
public class StochHaResult implements StrategyResult {

    private LocalDateTime time;

    private Set<Position> positions;

}
