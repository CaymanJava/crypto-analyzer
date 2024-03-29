package pro.crypto.indicator.ma;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import pro.crypto.model.indicator.IndicatorType;
import pro.crypto.model.indicator.Shift;
import pro.crypto.model.tick.PriceType;
import pro.crypto.model.tick.Tick;
import pro.crypto.request.IndicatorRequest;

import java.math.BigDecimal;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class MARequest implements IndicatorRequest {

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
