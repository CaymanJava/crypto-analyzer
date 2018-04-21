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
public class BBWRequest {

    private Tick[] originalData;

    private int period;

    private PriceType priceType;

    private int standardDeviationCoefficient;

    private IndicatorType movingAverageType;

}
