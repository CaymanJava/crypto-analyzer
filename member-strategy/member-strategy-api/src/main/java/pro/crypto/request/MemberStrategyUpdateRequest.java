package pro.crypto.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import pro.crypto.model.market.Stock;
import pro.crypto.model.notification.Destination;
import pro.crypto.model.strategy.MemberStrategyStatus;
import pro.crypto.model.strategy.Position;
import pro.crypto.model.strategy.StrategyType;
import pro.crypto.model.tick.TimeFrame;
import pro.crypto.model.update.UpdateTimeUnit;

import java.time.LocalDateTime;
import java.util.Set;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class MemberStrategyUpdateRequest {

    private Long marketId;

    private String strategyConfiguration;

    private String drawConfiguration;

    private StrategyType strategyType;

    private TimeFrame timeFrame;

    private UpdateTimeUnit updateTimeUnit;

    private Integer updateTimeValue;

    private String marketName;

    private Stock stock;

    private String strategyName;

    private String customStrategyName;

    private MemberStrategyStatus status;

    private LocalDateTime nextExecutionTime;

    private LocalDateTime lastExecutionTime;

    private Long cycleCount;

    private Long failedCount;

    private LocalDateTime lastSignalTickTime;

    private Set<Position> lastSignalPositions;

    private Destination notificationDestination;

}
