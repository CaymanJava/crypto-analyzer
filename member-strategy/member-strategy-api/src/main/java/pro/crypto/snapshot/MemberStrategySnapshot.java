package pro.crypto.snapshot;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import pro.crypto.model.market.Stock;
import pro.crypto.model.notification.Destination;
import pro.crypto.model.strategy.MemberStrategyStatus;
import pro.crypto.model.strategy.StrategyType;
import pro.crypto.model.tick.TimeFrame;
import pro.crypto.model.update.UpdateTimeUnit;

import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class MemberStrategySnapshot {

    private Long id;

    @NotNull
    private Long memberId;

    @NotNull
    private Long marketId;

    @NotNull
    private String strategyConfiguration;

    @NotNull
    private String drawConfiguration;

    @NotNull
    private StrategyType strategyType;

    @NotNull
    private TimeFrame timeFrame;

    @NotNull
    private UpdateTimeUnit updateTimeUnit;

    @NotNull
    private Integer updateTimeValue;

    @NotNull
    private String marketName;

    @NotNull
    private Stock stock;

    @NotNull
    private String strategyName;

    @NotNull
    private String customStrategyName;

    @NotNull
    private MemberStrategyStatus status;

    @NotNull
    private Long cycleCount;

    @NotNull
    private Long failedCount;

    private String stoppedReason;

    private LocalDateTime nextExecutionTime;

    private LocalDateTime lastExecutionTime;

    private LocalDateTime lastSignalTickTime;

    private Integer lastSignalPositionHash;

    private Destination notificationDestination;

}
