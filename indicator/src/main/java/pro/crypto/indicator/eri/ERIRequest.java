package pro.crypto.indicator.eri;

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
public class ERIRequest implements IndicatorRequest {

    private Tick[] originalData;

    private int period;

    private int signalLinePeriod;

    private int smoothLinePeriod;

}
