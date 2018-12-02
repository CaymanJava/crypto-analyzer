package pro.crypto.strategy.pivot.rsi.macd.ma;

import lombok.Builder;
import lombok.Data;
import pro.crypto.model.Position;
import pro.crypto.model.StrategyResult;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Set;

@Data
@Builder
public class PivotRsiMacdMaResult implements StrategyResult {

    private LocalDateTime time;

    private BigDecimal stopLose;

    private BigDecimal firstTakeProfit;

    private BigDecimal secondTakeProfit;

    private Set<Position> positions;

}
