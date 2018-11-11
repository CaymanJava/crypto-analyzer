package pro.crypto.strategy.hastoch;

import lombok.Builder;
import lombok.Data;
import pro.crypto.model.Position;
import pro.crypto.model.StrategyResult;

import java.time.LocalDateTime;
import java.util.Set;

@Data
@Builder
public class HAStochResult implements StrategyResult {

    private LocalDateTime time;

    private Set<Position> positions;

}
