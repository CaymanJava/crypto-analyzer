package pro.crypto.indicator.st;

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
public class STRequest implements IndicatorRequest {

    private Tick[] originalData;

    private int period;

    private double multiplier;

}