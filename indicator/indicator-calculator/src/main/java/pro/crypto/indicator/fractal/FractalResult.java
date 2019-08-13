package pro.crypto.indicator.fractal;

import lombok.AllArgsConstructor;
import lombok.Data;
import pro.crypto.response.IndicatorResult;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
public class FractalResult implements IndicatorResult {

    private LocalDateTime time;

    private boolean upFractal;

    private boolean downFractal;

    // we need high and low values for drawing
    private BigDecimal high;

    private BigDecimal low;

}
