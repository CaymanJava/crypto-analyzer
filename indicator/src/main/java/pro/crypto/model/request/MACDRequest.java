package pro.crypto.model.request;

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
public class MACDRequest {

    private Tick[] originalData;

    private IndicatorType movingAverageType;

    private PriceType priceType;

    private int slowPeriod;

    private int fastPeriod;

    private int signalPeriod;

}
