package pro.crypto.indicator.uo;

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
public class UORequest implements IndicatorRequest {

    private Tick[] originalData;

    private int shortPeriod;

    private int middlePeriod;

    private int longPeriod;

}
