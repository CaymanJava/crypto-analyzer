package pro.crypto.front.office.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import pro.crypto.model.tick.Tick;
import pro.crypto.model.tick.TimeFrame;
import pro.crypto.response.TickDataSnapshot;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class TickDataResponse {

    private MarketResponse market;

    private TimeFrame timeFrame;

    private Tick[] ticks;

    public static TickDataResponse fromSnapshot(TickDataSnapshot tickDataSnapshot) {
        return TickDataResponse.builder()
                .market(MarketResponse.fromSnapshot(tickDataSnapshot.getMarket()))
                .timeFrame(tickDataSnapshot.getTimeFrame())
                .ticks(tickDataSnapshot.getTicks())
                .build();
    }

}
