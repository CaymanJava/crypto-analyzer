package pro.crypto.indicator.asi;

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
public class ASIRequest implements IndicatorRequest {

    private Tick[] originalData;

    private double limitMoveValue;

    private int movingAveragePeriod;

    private IndicatorType movingAverageType;

}
