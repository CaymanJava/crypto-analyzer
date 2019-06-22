package pro.crypto.indicator.pivot;

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
public class PivotRequest implements IndicatorRequest {

    private Tick[] originalData;

    private Tick[] resultData;

    private IndicatorType indicatorType;

}
