package pro.crypto.indicator.eis;

import lombok.Builder;
import lombok.Data;
import pro.crypto.response.IndicatorResult;

import java.time.LocalDateTime;

@Data
@Builder
public class EISResult implements IndicatorResult {

    private LocalDateTime time;

    private BarColor barColor;

}
