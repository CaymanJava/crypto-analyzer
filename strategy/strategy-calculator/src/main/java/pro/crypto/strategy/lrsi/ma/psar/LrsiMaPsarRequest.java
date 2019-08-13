package pro.crypto.strategy.lrsi.ma.psar;

import lombok.Builder;
import lombok.Data;
import pro.crypto.model.indicator.IndicatorType;
import pro.crypto.model.strategy.Position;
import pro.crypto.model.tick.PriceType;
import pro.crypto.model.tick.Tick;
import pro.crypto.request.StrategyRequest;

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
