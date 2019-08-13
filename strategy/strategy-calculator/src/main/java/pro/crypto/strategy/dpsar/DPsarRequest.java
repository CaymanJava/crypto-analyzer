package pro.crypto.strategy.dpsar;

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
public class DPsarRequest implements StrategyRequest {

    private Tick[] originalData;

    private int movingAveragePeriod;

    private IndicatorType movingAverageType;

    private PriceType movingAveragePriceType;

    private IndicatorType macdMovingAverageType;

    private PriceType macdPriceType;

    private int macdSlowPeriod;

    private int macdFastPeriod;

    private int macdSignalPeriod;

    private double psarMinAccelerationFactor;

    private double psarMaxAccelerationFactor;

    private double pswMinAccelerationFactor;

    private double pswMaxAccelerationFactor;

    private Set<Position> positions;

}
