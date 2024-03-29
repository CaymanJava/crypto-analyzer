package pro.crypto.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import pro.crypto.model.market.Stock;
import pro.crypto.model.strategy.StrategyType;
import pro.crypto.model.tick.TimeFrame;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class SignalFindRequest {

    private String query;

    private Stock stock;

    private StrategyType type;

    private TimeFrame timeFrame;

}
