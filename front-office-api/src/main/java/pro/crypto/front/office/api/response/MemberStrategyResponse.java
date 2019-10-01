package pro.crypto.front.office.api.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import pro.crypto.model.market.Stock;
import pro.crypto.model.strategy.MemberStrategyStatus;
import pro.crypto.model.strategy.Position;
import pro.crypto.model.strategy.StrategyType;
import pro.crypto.model.tick.TimeFrame;
import pro.crypto.model.update.UpdateTimeUnit;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class MemberStrategyResponse {

    private Long id;

    private Long memberId;

    private Long marketId;

    private String marketName;

    private String logoUrl;

    private BigDecimal baseVolume;

    private BigDecimal priceDiff;

    private StrategyType strategyType;

    private TimeFrame timeFrame;

    private UpdateTimeUnit updateTimeUnit;

    private Integer updateTimeValue;

    private Stock stock;

    private String strategyName;

    private String customStrategyName;

    private MemberStrategyStatus status;

    private LocalDateTime lastSignalTickTime;

    private Position lastSignalPosition;

}
