package pro.crypto.indicator.ac;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import pro.crypto.model.tick.Tick;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ACRequest {

    private Tick[] originalData;

    private int slowPeriod;

    private int fastPeriod;

    private int smoothedPeriod;

}
