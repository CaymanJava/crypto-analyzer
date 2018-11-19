package pro.crypto.indicator.eis;

import lombok.Builder;
import lombok.Data;
import pro.crypto.model.IndicatorRequest;
import pro.crypto.model.IndicatorType;
import pro.crypto.model.tick.PriceType;
import pro.crypto.model.tick.Tick;

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
