package pro.crypto.strategy.macd.cci;

import lombok.Builder;
import lombok.Data;
import pro.crypto.model.strategy.Position;
import pro.crypto.model.tick.Tick;
import pro.crypto.response.StrategyResult;

import java.math.BigDecimal;
import java.util.Set;

@Data
@Builder
public class MacdCciResult implements StrategyResult {

    private Tick tick;

    private Set<Position> positions;

    private BigDecimal macdValue;

    private BigDecimal macdSignalLineValue;

    private BigDecimal macdBarChartValue;

    private BigDecimal cciMacdResult;

}
