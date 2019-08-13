package pro.crypto.strategy.pivot.rsi.macd.ma;

import lombok.Builder;
import lombok.Data;
import pro.crypto.model.indicator.IndicatorType;
import pro.crypto.model.strategy.Position;
import pro.crypto.model.tick.PriceType;
import pro.crypto.model.tick.Tick;
import pro.crypto.request.StrategyRequest;

import java.util.Set;

@Data
@Builder
public class PivotRsiMacdMaRequest implements StrategyRequest {

    private Tick[] originalData;

    private Tick[] oneDayTickData;

    private IndicatorType rsiMaType;

    private int rsiPeriod;

    private Double rsiSignalLine;

    private IndicatorType macdMaType;

    private PriceType macdPriceType;

    private int macdFastPeriod;

    private int macdSlowPeriod;

    private int macdSignalPeriod;

    private IndicatorType maType;

    private PriceType maPriceType;

    private int maPeriod;

    private Set<Position> positions;

}
