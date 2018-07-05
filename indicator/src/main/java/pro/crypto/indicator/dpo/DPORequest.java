package pro.crypto.indicator.dpo;

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
public class DPORequest {

    private Tick[] originalData;

    private int period;

    private IndicatorType movingAverageType;

    private PriceType priceType;

}