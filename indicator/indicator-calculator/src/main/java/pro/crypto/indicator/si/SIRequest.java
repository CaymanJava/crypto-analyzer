package pro.crypto.indicator.si;

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
public class SIRequest implements IndicatorRequest {

    private Tick[] originalData;

    private double limitMoveValue;

}
