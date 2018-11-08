package pro.crypto.indicator.fractal;

import lombok.AllArgsConstructor;
import lombok.Data;
import pro.crypto.model.IndicatorResult;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
public class FractalResult implements IndicatorResult {

    private LocalDateTime time;

    private boolean upFractal;

    private boolean downFractal;

}
