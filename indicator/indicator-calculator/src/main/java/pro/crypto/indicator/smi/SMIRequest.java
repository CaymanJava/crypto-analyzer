package pro.crypto.indicator.smi;

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
public class SMIRequest implements IndicatorRequest {

    private Tick[] originalData;

    private int period;

    private int smoothingPeriod;

    private IndicatorType movingAverageType;

}
