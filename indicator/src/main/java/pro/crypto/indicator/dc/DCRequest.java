package pro.crypto.indicator.dc;

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
public class DCRequest implements IndicatorRequest {

    private Tick[] originalData;

    private int highPeriod;

    private int lowPeriod;

}
