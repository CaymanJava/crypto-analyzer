package pro.crypto.indicator.asi;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import pro.crypto.model.IndicatorRequest;
import pro.crypto.model.tick.Tick;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ASIRequest implements IndicatorRequest {

    private Tick[] originalData;

    private double limitMoveValue;

}
