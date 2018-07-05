package pro.crypto.indicator.eom;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import pro.crypto.model.IndicatorType;
import pro.crypto.model.tick.Tick;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class EOMRequest {

    private Tick[] originalData;

    private IndicatorType movingAverageType;

    private int movingAveragePeriod;

}
