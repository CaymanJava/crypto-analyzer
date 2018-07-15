package pro.crypto.indicator.smi;

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
public class SMIRequest implements IndicatorRequest {

    private Tick[] originalData;

    private int period;

    private int smoothingPeriod;

    private IndicatorType movingAverageType;

}
