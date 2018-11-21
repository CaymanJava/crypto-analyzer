package pro.crypto.strategy.rsi.eis.ma;

import lombok.Builder;
import lombok.Data;
import pro.crypto.model.IndicatorType;
import pro.crypto.model.Position;
import pro.crypto.model.StrategyRequest;
import pro.crypto.model.tick.PriceType;
import pro.crypto.model.tick.Tick;

import java.util.Set;

@Data
@Builder
public class RsiEisMaRequest implements StrategyRequest {

    private Tick[] originalData;

    private IndicatorType rsiMaType;

    private int rsiPeriod;

    private double rsiSignalLine;

    private int eisMaPeriod;

    private IndicatorType eisMaType;

    private PriceType eisMaPriceType;

    private IndicatorType eisMacdMaType;

    private PriceType eisMacdPriceType;

    private int eisMacdFastPeriod;

    private int eisMacdSlowPeriod;

    private int eisMacdSignalPeriod;

    private IndicatorType fastMaType;

    private PriceType fastMaPriceType;

    private int fastMaPeriod;

    private IndicatorType slowMaType;

    private PriceType slowMaPriceType;

    private int slowMaPeriod;

    private Set<Position> positions;

}
