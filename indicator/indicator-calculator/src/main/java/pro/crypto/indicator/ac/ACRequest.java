package pro.crypto.indicator.ac;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import pro.crypto.model.tick.Tick;
import pro.crypto.request.IndicatorRequest;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ACRequest implements IndicatorRequest {

    private Tick[] originalData;

    private int slowPeriod;

    private int fastPeriod;

    private int smoothedPeriod;

}
