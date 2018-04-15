package pro.crypto.model.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import pro.crypto.model.IndicatorType;
import pro.crypto.model.Shift;
import pro.crypto.model.tick.PriceType;
import pro.crypto.model.tick.Tick;

import java.math.BigDecimal;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class MARequest {

    private IndicatorType indicatorType;

    // only from DMA
    private IndicatorType originalIndicatorType;

    // only from DMA
    private Shift shift;

    private PriceType priceType;

    private Tick[] originalData;

    private int period;

    // only for EMA and for DMA which based on EMA
    private BigDecimal alphaCoefficient;

}
