package pro.crypto.strategy.cci.rsi.atr;

import lombok.Builder;
import lombok.Data;
import pro.crypto.model.strategy.Position;
import pro.crypto.model.tick.Tick;
import pro.crypto.response.StrategyResult;

import java.math.BigDecimal;
import java.util.Set;

@Data
@Builder
public class CciRsiAtrResult implements StrategyResult {

    private Tick tick;

    private BigDecimal stopLose;

    private Set<Position> positions;

    private BigDecimal cciResult;

    private BigDecimal rsiResult;

    private BigDecimal atrValue;

    private BigDecimal atrSignalLineValue;

}
