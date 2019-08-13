package pro.crypto.indicator.eis;

import lombok.Builder;
import lombok.Data;
import pro.crypto.model.indicator.IndicatorType;
import pro.crypto.model.tick.PriceType;
import pro.crypto.model.tick.Tick;
import pro.crypto.request.IndicatorRequest;

@Data
@Builder
public class EISRequest implements IndicatorRequest {

    private Tick[] originalData;

    private int maPeriod;

    private IndicatorType maType;

    private PriceType maPriceType;

    private IndicatorType macdMaType;

    private PriceType macdPriceType;

    private int macdFastPeriod;

    private int macdSlowPeriod;

    private int macdSignalPeriod;

}
