package pro.crypto.indicator.rv;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import pro.crypto.model.tick.PriceType;
import pro.crypto.model.tick.Tick;
import pro.crypto.request.IndicatorRequest;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class RVRequest implements IndicatorRequest {

    private Tick[] originalData;

    private int period;

    private int stDevPeriod;

    private PriceType priceType;

}
