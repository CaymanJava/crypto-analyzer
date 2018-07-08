package pro.crypto.indicator.kelt;

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
public class KELTRequest implements IndicatorRequest {

    private Tick[] originalData;

    private PriceType priceType;

    private IndicatorType movingAverageType;

    private int movingAveragePeriod;

    private int averageTrueRangePeriod;

    private double averageTrueRangeShift;

}
