package pro.crypto.model.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import pro.crypto.model.tick.PriceType;
import pro.crypto.model.tick.Tick;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class KSTRequest {

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
