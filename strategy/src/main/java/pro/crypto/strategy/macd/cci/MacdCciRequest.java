package pro.crypto.strategy.macd.cci;

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
