package pro.crypto.indicator.rsi;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import pro.crypto.model.indicator.IndicatorType;
import pro.crypto.model.tick.Tick;
import pro.crypto.request.IndicatorRequest;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class RSIRequest implements IndicatorRequest {

    private Tick[] originalData;

    private IndicatorType movingAverageType;

    private int period;

}
