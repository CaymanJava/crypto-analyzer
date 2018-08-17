package pro.crypto.indicator.atr;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import pro.crypto.model.IndicatorRequest;
import pro.crypto.model.IndicatorType;
import pro.crypto.model.tick.Tick;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ATRRequest implements IndicatorRequest {

    private Tick[] originalData;

    private int period;

    private IndicatorType movingAverageType;

    private int movingAveragePeriod;

}
