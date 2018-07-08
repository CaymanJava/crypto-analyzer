package pro.crypto.indicator.psar;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import pro.crypto.model.IndicatorRequest;
import pro.crypto.model.tick.Tick;

import java.math.BigDecimal;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class PSARRequest implements IndicatorRequest {

    private Tick[] originalData;

    private BigDecimal minAccelerationFactor;

    private BigDecimal maxAccelerationFactor;

}
