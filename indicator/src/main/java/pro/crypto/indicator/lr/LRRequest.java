package pro.crypto.indicator.lr;

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
public class LRRequest implements IndicatorRequest {

    private Tick[] originalData;

    private boolean averageCalculation;

    private PriceType priceType;

    private int period;

}
