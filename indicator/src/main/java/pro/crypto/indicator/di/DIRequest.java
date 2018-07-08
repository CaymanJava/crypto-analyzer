package pro.crypto.indicator.di;

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
public class DIRequest implements IndicatorRequest {

    private Tick[] originalData;

    private IndicatorType movingAverageType;

    private int period;

    private PriceType priceType;

}
