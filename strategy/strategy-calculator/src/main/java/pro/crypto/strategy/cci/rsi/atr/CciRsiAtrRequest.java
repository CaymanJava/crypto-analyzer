package pro.crypto.strategy.cci.rsi.atr;

import lombok.Builder;
import lombok.Data;
import pro.crypto.model.indicator.IndicatorType;
import pro.crypto.model.strategy.Position;
import pro.crypto.model.tick.Tick;
import pro.crypto.request.StrategyRequest;

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
