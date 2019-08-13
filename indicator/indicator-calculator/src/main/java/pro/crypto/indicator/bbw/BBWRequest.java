package pro.crypto.indicator.bbw;

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
public class BBWRequest implements IndicatorRequest {

    private Tick[] originalData;

    private int period;

    private PriceType priceType;

    private double standardDeviationCoefficient;

    private IndicatorType movingAverageType;

}
