package pro.crypto.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import pro.crypto.model.market.Stock;
import pro.crypto.model.strategy.Position;
import pro.crypto.model.strategy.StrategyType;
import pro.crypto.model.tick.TimeFrame;

import java.time.LocalDateTime;
import java.util.Set;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class SignalCreationRequest {

    private Set<Position> positions;

    private Long memberId;

    private Long marketId;

    private Long memberStrategyId;

    private StrategyType strategyType;

    private TimeFrame timeFrame;

    private String marketName;

    private Stock stock;

    private String strategyName;

    private String customStrategyName;

    private LocalDateTime tickTime;

}
