package pro.crypto.model.tick;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import pro.crypto.model.market.Market;
import pro.crypto.model.market.Stock;


@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class TickData {

    private Stock stockExchangeName;

    private Market market;

    private TimeFrame timeFrame;

    private Tick[] ticks;

}
