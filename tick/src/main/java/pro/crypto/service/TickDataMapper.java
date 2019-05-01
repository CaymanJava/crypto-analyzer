package pro.crypto.service;

import org.springframework.stereotype.Component;
import pro.crypto.model.tick.TickData;
import pro.crypto.snapshot.MarketSnapshot;
import pro.crypto.snapshot.TickDataSnapshot;

@Component
public class TickDataMapper {

    public TickDataSnapshot fromTickData(TickData tickData, MarketSnapshot market) {
        return TickDataSnapshot.builder()
                .market(market)
                .timeFrame(tickData.getTimeFrame())
                .ticks(tickData.getTicks())
                .build();
    }

}
