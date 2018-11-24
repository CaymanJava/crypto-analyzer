package pro.crypto.strategy.lrsi.ma.psar;

import lombok.Builder;
import lombok.Data;
import pro.crypto.model.IndicatorType;
import pro.crypto.model.Position;
import pro.crypto.model.StrategyRequest;
import pro.crypto.model.tick.PriceType;
import pro.crypto.model.tick.Tick;

import java.util.Set;

@Data
@Builder
public class LrsiMaPsarRequest implements StrategyRequest {

    private Tick[] originalData;

    private double lrsiGamma;

    private Double lrsiOversoldLevel;

    private Double lrsiOverboughtLevel;

    private IndicatorType maType;

    private PriceType maPriceType;

    private int maPeriod;

    private double psarMinAccelerationFactor;

    private double psarMaxAccelerationFactor;

    private Set<Position> positions;

}
