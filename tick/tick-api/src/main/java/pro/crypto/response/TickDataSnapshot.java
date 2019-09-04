package pro.crypto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import pro.crypto.model.tick.Tick;
import pro.crypto.model.tick.TimeFrame;
import pro.crypto.snapshot.MarketSnapshot;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class TickDataSnapshot {

    private MarketSnapshot market;

    private TimeFrame timeFrame;

    private Tick[] ticks;

}
