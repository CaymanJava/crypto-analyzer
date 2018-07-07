package pro.crypto.indicator.ro;

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
public class RORequest {

    private Tick[] originalData;

    private PriceType priceType;

    private int period;

    private int highLowLookBack;

}
