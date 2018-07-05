package pro.crypto.indicator.ce;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import pro.crypto.model.tick.Tick;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CERequest {

    private Tick[] originalData;

    private int period;

    private double factor;

}
