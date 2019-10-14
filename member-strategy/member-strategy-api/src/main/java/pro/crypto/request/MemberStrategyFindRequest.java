package pro.crypto.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import pro.crypto.model.market.Stock;
import pro.crypto.model.strategy.MemberStrategyStatus;
import pro.crypto.model.strategy.StrategyType;
import pro.crypto.model.tick.TimeFrame;

import java.util.Set;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class MemberStrategyFindRequest {

    private String query;

    private Stock stock;

    private MemberStrategyStatus status;

    private StrategyType type;

    private TimeFrame timeFrame;

    private Set<Long> ids;

}
