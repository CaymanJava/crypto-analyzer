package pro.crypto.indicator.stc;

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
public class STCRequest implements IndicatorRequest {

    private Tick[] originalData;

    private PriceType priceType;

    private int period;

    private int shortCycle;

    private int longCycle;

    private IndicatorType movingAverageType;

}
