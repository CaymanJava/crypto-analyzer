package pro.crypto.indicator.cc;

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
public class CCRequest {

    private Tick[] originalData;

    private int period;

    private int shortROCPeriod;

    private int longROCPeriod;

    private PriceType priceType;

}
