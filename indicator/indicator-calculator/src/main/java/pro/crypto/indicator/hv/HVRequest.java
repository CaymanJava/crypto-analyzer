package pro.crypto.indicator.hv;

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
public class HVRequest implements IndicatorRequest {

    private Tick[] originalData;

    private int period;

    private PriceType priceType;

    private int daysPerYear;

    private int standardDeviations;

}
