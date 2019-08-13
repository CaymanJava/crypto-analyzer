package pro.crypto.indicator.kst;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import pro.crypto.model.tick.PriceType;
import pro.crypto.model.tick.Tick;
import pro.crypto.request.IndicatorRequest;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class KSTRequest implements IndicatorRequest {

    private Tick[] originalData;

    private PriceType priceType;

    private int lightestROCPeriod;

    private int lightestSMAPeriod;

    private int lightROCPeriod;

    private int lightSMAPeriod;

    private int heavyROCPeriod;

    private int heavySMAPeriod;

    private int heaviestROCPeriod;

    private int heaviestSMAPeriod;

    private int signalLinePeriod;

}
