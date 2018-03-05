package pro.crypto.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import pro.crypto.model.market.Stock;
import pro.crypto.model.tick.TimeFrame;

import javax.validation.constraints.NotNull;


@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class GetTicksByPeriodRequest {

    @NotNull
    private Stock stock;

    @NotNull
    private long marketId;

    @NotNull
    private TimeFrame timeFrame;

    @NotNull
    private int period;

}
