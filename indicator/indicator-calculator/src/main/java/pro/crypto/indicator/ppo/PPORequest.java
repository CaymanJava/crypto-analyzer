package pro.crypto.indicator.ppo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import pro.crypto.model.indicator.IndicatorType;
import pro.crypto.model.tick.PriceType;
import pro.crypto.model.tick.Tick;
import pro.crypto.request.IndicatorRequest;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class PPORequest implements IndicatorRequest {

    private Tick[] originalData;

    private IndicatorType movingAverageType;

    private PriceType priceType;

    private int slowPeriod;

    private int fastPeriod;

    private int signalPeriod;

}
