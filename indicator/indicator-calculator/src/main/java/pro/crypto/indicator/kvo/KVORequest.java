package pro.crypto.indicator.kvo;

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
public class KVORequest implements IndicatorRequest {

    private Tick[] originalData;

    private int shortPeriod;

    private int longPeriod;

    private int signalPeriod;

}
