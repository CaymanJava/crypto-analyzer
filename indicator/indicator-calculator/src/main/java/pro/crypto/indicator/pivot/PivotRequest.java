package pro.crypto.indicator.pivot;

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
public class PivotRequest implements IndicatorRequest {

    private Tick[] originalData;

    private Tick[] resultData;

    private IndicatorType indicatorType;

}
