package pro.crypto.indicator.hlb;

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
public class HLBRequest {

    private Tick[] originalData;

    private int period;

    private PriceType priceType;

    private double shiftPercentage;

}