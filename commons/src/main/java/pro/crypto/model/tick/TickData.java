package pro.crypto.model.tick;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import pro.crypto.model.market.Stock;
import pro.crypto.model.market.StockMarket;


@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class TickData {

    private Stock stockExchangeName;

    private StockMarket market;

    private TimeFrame timeFrame;

    private Tick[] ticks;

}
