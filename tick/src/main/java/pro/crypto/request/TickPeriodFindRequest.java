package pro.crypto.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import pro.crypto.model.tick.TimeFrame;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class TickPeriodFindRequest {

    private Long marketId;

    private TimeFrame timeFrame;

    private Integer period;

}
