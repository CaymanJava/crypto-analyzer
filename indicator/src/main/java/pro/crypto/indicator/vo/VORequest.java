package pro.crypto.indicator.vo;

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
public class VORequest implements IndicatorRequest {

    private Tick[] originalData;

    private int shortPeriod;

    private int longPeriod;

}
