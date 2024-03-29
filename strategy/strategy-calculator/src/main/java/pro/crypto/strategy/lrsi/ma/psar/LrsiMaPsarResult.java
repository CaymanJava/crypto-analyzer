package pro.crypto.strategy.lrsi.ma.psar;

import lombok.Builder;
import lombok.Data;
import pro.crypto.model.strategy.Position;
import pro.crypto.model.tick.Tick;
import pro.crypto.response.StrategyResult;

import java.math.BigDecimal;
import java.util.Set;

@Data
@Builder
public class LrsiMaPsarResult implements StrategyResult {

    private Tick tick;

    private Set<Position> positions;

    private BigDecimal lrsiValue;

    private BigDecimal maValue;

    private BigDecimal psarValue;

}
