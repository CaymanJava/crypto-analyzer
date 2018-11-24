package pro.crypto.strategy.stc.ma.macd;

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
public class StcMaMacdRequest implements StrategyRequest {

    private Tick[] originalData;

    private PriceType stcPriceType;

    private int stcPeriod;

    private int stcShortCycle;

    private int stcLongCycle;

    private Double stcOversoldLevel;

    private Double stcOverboughtLevel;

    private IndicatorType stcMaType;

    private IndicatorType maType;

    private PriceType maPriceType;

    private int maPeriod;

    private IndicatorType macdMaType;

    private PriceType macdPriceType;

    private int macdFastPeriod;

    private int macdSlowPeriod;

    private int macdSignalPeriod;

    private Set<Position> positions;

}
