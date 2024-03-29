package pro.crypto.model;

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
import pro.crypto.request.MemberStrategyUpdateRequest;

import javax.persistence.CollectionTable;
import javax.persistence.Column;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;
import java.util.Set;

import static java.util.Optional.ofNullable;
import static javax.persistence.EnumType.STRING;
import static javax.persistence.FetchType.EAGER;

@Entity
@Table(schema = "crypto_member_strategy")
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Data
public class MemberStrategy {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
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
    @Enumerated(STRING)
    private StrategyType strategyType;

    @NotNull
    @Enumerated(STRING)
    private TimeFrame timeFrame;

    @NotNull
    @Enumerated(STRING)
    private UpdateTimeUnit updateTimeUnit;

    @NotNull
    private Integer updateTimeValue;

    @NotNull
    private String marketName;

    @NotNull
    @Enumerated(STRING)
    private Stock stock;

    @NotNull
    private String strategyName;

    @NotNull
    private String customStrategyName;

    @NotNull
    @Enumerated(STRING)
    private MemberStrategyStatus status;

    @NotNull
    private Long cycleCount;

    @NotNull
    private Long failedCount;

    private String stoppedReason;

    private LocalDateTime nextExecutionTime;

    private LocalDateTime lastExecutionTime;

    private LocalDateTime lastSignalTickTime;

    @ElementCollection(targetClass = Position.class, fetch = EAGER)
    @CollectionTable(name = "member_strategy_position",
            schema = "crypto_member_strategy",
            joinColumns = @JoinColumn(name = "member_strategy_id"))
    @Column(name = "position")
    @Enumerated(STRING)
    private Set<Position> lastSignalPositions;

    @Enumerated(STRING)
    private Destination notificationDestination;

    public void update(MemberStrategyUpdateRequest request) {
        marketId = ofNullable(request.getMarketId()).orElse(marketId);
        strategyConfiguration = ofNullable(request.getStrategyConfiguration()).orElse(strategyConfiguration);
        drawConfiguration = ofNullable(request.getDrawConfiguration()).orElse(drawConfiguration);
        strategyType = ofNullable(request.getStrategyType()).orElse(strategyType);
        timeFrame = ofNullable(request.getTimeFrame()).orElse(timeFrame);
        updateTimeUnit = ofNullable(request.getUpdateTimeUnit()).orElse(updateTimeUnit);
        updateTimeValue = ofNullable(request.getUpdateTimeValue()).orElse(updateTimeValue);
        marketName = ofNullable(request.getMarketName()).orElse(marketName);
        stock = ofNullable(request.getStock()).orElse(stock);
        strategyName = ofNullable(request.getStrategyName()).orElse(strategyName);
        customStrategyName = ofNullable(request.getCustomStrategyName()).orElse(customStrategyName);
        status = ofNullable(request.getStatus()).orElse(status);
        failedCount = ofNullable(request.getFailedCount()).orElse(failedCount);
        lastSignalTickTime = ofNullable(request.getLastSignalTickTime()).orElse(lastSignalTickTime);
        lastSignalPositions = ofNullable(request.getLastSignalPositions()).orElse(lastSignalPositions);
        notificationDestination = ofNullable(request.getNotificationDestination()).orElse(notificationDestination);
    }

}
