package pro.crypto.indicator.vi;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import pro.crypto.model.IndicatorRequest;
import pro.crypto.model.IndicatorType;
import pro.crypto.model.tick.PriceType;
import pro.crypto.model.tick.Tick;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class VIRequest implements IndicatorRequest {

    private Tick[] originalData;

    private int period;

    private IndicatorType volumeIndexType;

    private IndicatorType movingAverageType;

    private PriceType priceType;

}
