package pro.crypto.indicator.pmo;

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
public class PMORequest implements IndicatorRequest {

    private Tick[] originalData;

    private PriceType priceType;

    private int smoothingPeriod;

    private int doubleSmoothingPeriod;

    private int signalPeriod;

}
