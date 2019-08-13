package pro.crypto.strategy.stc.ma.macd;

import lombok.Builder;
import lombok.Data;
import pro.crypto.model.strategy.Position;
import pro.crypto.model.tick.Tick;
import pro.crypto.response.StrategyResult;

import java.math.BigDecimal;
import java.util.Set;

@Data
@Builder
public class StcMaMacdResult implements StrategyResult {

    private Tick tick;

    private BigDecimal stopLose;

    private Set<Position> positions;

    private BigDecimal stcResult;

    private BigDecimal macdValue;

    private BigDecimal macdSignalLineValue;

    private BigDecimal macdBarChartValue;

    private BigDecimal maResult;

}
