package pro.crypto.strategy.pivot.rsi.macd.ma;

import lombok.Builder;
import lombok.Data;
import pro.crypto.model.strategy.Position;
import pro.crypto.model.tick.Tick;
import pro.crypto.response.StrategyResult;

import java.math.BigDecimal;
import java.util.Set;

@Data
@Builder
public class PivotRsiMacdMaResult implements StrategyResult {

    private Tick tick;

    private BigDecimal stopLose;

    private BigDecimal firstTakeProfit;

    private BigDecimal secondTakeProfit;

    private Set<Position> positions;

    private BigDecimal pivot;

    private BigDecimal firstResistance;

    private BigDecimal secondResistance;

    private BigDecimal thirdResistance;

    private BigDecimal fourthResistance;

    private BigDecimal firstSupport;

    private BigDecimal secondSupport;

    private BigDecimal thirdSupport;

    private BigDecimal fourthSupport;

    private BigDecimal rsiValue;

    private BigDecimal macdValue;

    private BigDecimal macdSignalLineValue;

    private BigDecimal macdBarChartValue;

    private BigDecimal maValue;

}
