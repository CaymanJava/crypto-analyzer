package pro.crypto.strategy.stochcci;

import lombok.Builder;
import lombok.Data;
import pro.crypto.model.Position;
import pro.crypto.model.StrategyResult;

import java.time.LocalDateTime;
import java.util.Set;

@Data
@Builder
public class StochCCIResult implements StrategyResult {

    private LocalDateTime time;

    private Set<Position> positions;

}
