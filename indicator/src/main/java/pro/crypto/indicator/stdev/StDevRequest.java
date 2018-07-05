package pro.crypto.indicator.stdev;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import pro.crypto.model.IndicatorType;
import pro.crypto.model.tick.PriceType;
import pro.crypto.model.tick.Tick;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class StDevRequest {

    private Tick[] originalData;

    private PriceType priceType;

    private IndicatorType movingAverageType;

    private int period;

}
