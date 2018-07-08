package pro.crypto.indicator.rma;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import pro.crypto.model.IndicatorRequest;
import pro.crypto.model.tick.PriceType;
import pro.crypto.model.tick.Tick;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class RMARequest implements IndicatorRequest {

    private Tick[] originalData;

    private PriceType priceType;

    private int period;

}
