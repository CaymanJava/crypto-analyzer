package pro.crypto.strategy.bws;

import lombok.Builder;
import lombok.Data;
import pro.crypto.model.strategy.Position;
import pro.crypto.model.tick.Tick;
import pro.crypto.response.StrategyResult;

import java.math.BigDecimal;
import java.util.Set;

@Data
@Builder
public class BWSResult implements StrategyResult {

    private Tick tick;

    private Set<Position> positions;

    private BigDecimal entryPrice;

    private BigDecimal stopLose;

    private BigDecimal acValue;

    private Boolean acIncreased;

    private BigDecimal aoValue;

    private Boolean aoIncreased;

    private BigDecimal jawValue;

    private BigDecimal teethValue;

    private BigDecimal lipsValue;

    private boolean upFractal;

    private boolean downFractal;

}
