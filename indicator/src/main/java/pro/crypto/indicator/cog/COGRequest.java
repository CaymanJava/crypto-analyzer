package pro.crypto.indicator.cog;

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
public class COGRequest implements IndicatorRequest {

    private Tick[] originalData;

    private PriceType priceType;

    private int period;

    private IndicatorType movingAverageType;

    private int signalLinePeriod;

}
