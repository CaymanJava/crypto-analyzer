package pro.crypto.model.result;

import lombok.Value;
import pro.crypto.model.IndicatorResult;

import java.time.LocalDateTime;

@Value
public class FractalResult implements IndicatorResult {

    private LocalDateTime time;

    private boolean upFractal;

    private boolean downFractal;

}
