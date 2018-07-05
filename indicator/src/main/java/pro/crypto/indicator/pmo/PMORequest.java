package pro.crypto.indicator.pmo;

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
public class PMORequest {

    private Tick[] originalData;

    private PriceType priceType;

    private int smoothingPeriod;

    private int doubleSmoothingPeriod;

    private int signalPeriod;

}