package pro.crypto.front.office.api.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import pro.crypto.model.market.Stock;
import pro.crypto.model.strategy.Position;
import pro.crypto.model.strategy.StrategyType;
import pro.crypto.model.tick.TimeFrame;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Set;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class SignalResponse {

    private Long id;

    private Set<Position> positions;

    private Long marketId;

    private Long memberStrategyId;

    private String logoUrl;

    private BigDecimal baseVolume;

    private BigDecimal priceDiff;

    private StrategyType strategyType;

    private TimeFrame timeFrame;

    private String marketName;

    private Stock stock;

    private String strategyName;

    private String customStrategyName;

    private LocalDateTime tickTime;

    private LocalDateTime creationTime;

}
