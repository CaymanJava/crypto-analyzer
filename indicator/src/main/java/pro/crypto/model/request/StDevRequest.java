package pro.crypto.model.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import pro.crypto.model.IndicatorType;
import pro.crypto.model.tick.PriceType;
import pro.crypto.model.tick.Tick;

import javax.validation.constraints.NotNull;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class StDevRequest {

    @NotNull
    private Tick[] originalData;

    @NotNull
    private PriceType priceType;

    @NotNull
    private IndicatorType movingAverageType;

    @NotNull
    private int period;

}
