package pro.crypto.indicator.psar;

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
public class PSARRequest implements IndicatorRequest {

    private Tick[] originalData;

    private double minAccelerationFactor;

    private double maxAccelerationFactor;

}
