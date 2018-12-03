package pro.crypto.strategy.ha.macd.psar;

import lombok.Builder;
import lombok.Data;
import pro.crypto.model.Position;
import pro.crypto.model.StrategyResult;

import java.time.LocalDateTime;
import java.util.Set;

@Data
@Builder
public class HaMacdPsarResult implements StrategyResult {

    private LocalDateTime time;

    private Set<Position> positions;

}
