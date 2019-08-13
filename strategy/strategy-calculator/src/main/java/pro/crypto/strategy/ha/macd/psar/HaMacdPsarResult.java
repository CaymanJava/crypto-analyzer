package pro.crypto.strategy.ha.macd.psar;

import lombok.Builder;
import lombok.Data;
import pro.crypto.model.strategy.Position;
import pro.crypto.model.tick.Tick;
import pro.crypto.response.StrategyResult;

import java.math.BigDecimal;
import java.util.Set;

@Data
@Builder
public class HaMacdPsarResult implements StrategyResult {

    private Tick tick;

    private Set<Position> positions;

    private BigDecimal haOpen;

    private BigDecimal haHigh;

    private BigDecimal haLow;

    private BigDecimal haClose;

    private BigDecimal macdValue;

    private BigDecimal macdSignalLineValue;

    private BigDecimal macdBarChartValue;

    private BigDecimal psarValue;

}
