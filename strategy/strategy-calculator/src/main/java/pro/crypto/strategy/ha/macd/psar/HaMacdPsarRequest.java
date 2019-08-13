package pro.crypto.strategy.ha.macd.psar;

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
public class HaMacdPsarRequest implements StrategyRequest {

    private Tick[] originalData;

    private IndicatorType macdMaType;

    private PriceType macdPriceType;

    private int macdFastPeriod;

    private int macdSlowPeriod;

    private int macdSignalPeriod;

    private double psarMinAccelerationFactor;

    private double psarMaxAccelerationFactor;

    private Set<Position> positions;

}
