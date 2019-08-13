package pro.crypto.strategy.macd.cci;

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
public class MacdCciRequest implements StrategyRequest {

    private Tick[] originalData;

    private IndicatorType macdMaType;

    private PriceType macdPriceType;

    private int macdFastPeriod;

    private int macdSlowPeriod;

    private int macdSignalPeriod;

    private int cciPeriod;

    private Double cciOversoldLevel;

    private Double cciOverboughtLevel;

    private Set<Position> positions;

}
