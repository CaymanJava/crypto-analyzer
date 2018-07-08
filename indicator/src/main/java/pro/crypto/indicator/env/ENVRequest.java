package pro.crypto.indicator.env;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import pro.crypto.model.IndicatorRequest;
import pro.crypto.model.IndicatorType;
import pro.crypto.model.tick.Tick;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ENVRequest implements IndicatorRequest {

    private Tick[] originalData;

    private IndicatorType movingAverageType;

    private int movingAveragePeriod;

    private int indentationPercentage;

}
