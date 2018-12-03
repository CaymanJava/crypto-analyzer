package pro.crypto.strategy.cci.rsi.atr;

import lombok.Builder;
import lombok.Data;
import pro.crypto.model.IndicatorType;
import pro.crypto.model.Position;
import pro.crypto.model.StrategyRequest;
import pro.crypto.model.tick.Tick;

import java.util.Set;

@Data
@Builder
public class CciRsiAtrRequest implements StrategyRequest {

    private Tick[] originalData;

    private int cciPeriod;

    private Double cciSignalLine;

    private IndicatorType rsiMaType;

    private int rsiPeriod;

    private Double rsiSignalLine;

    private int atrPeriod;

    private IndicatorType atrMaType;

    private int atrMaPeriod;

    private Set<Position> positions;

}
