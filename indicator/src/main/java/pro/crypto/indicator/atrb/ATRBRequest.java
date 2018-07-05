package pro.crypto.indicator.atrb;

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
public class ATRBRequest {

    private Tick[] originalData;

    private int period;

    private double shift;

    private PriceType priceType;

}
