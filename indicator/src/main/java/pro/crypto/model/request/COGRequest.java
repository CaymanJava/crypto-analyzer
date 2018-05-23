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
public class COGRequest {

    private Tick[] originalData;

    private PriceType priceType;

    private int period;

    private IndicatorType movingAverageType;

    private int signalLinePeriod;

}
