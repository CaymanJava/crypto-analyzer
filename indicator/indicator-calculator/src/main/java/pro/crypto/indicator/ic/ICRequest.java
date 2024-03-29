package pro.crypto.indicator.ic;

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
public class ICRequest implements IndicatorRequest {

    private Tick[] originalData;

    private int conversionLinePeriod;

    private int baseLinePeriod;

    private int leadingSpanPeriod;

    private int displaced;

}
