package pro.crypto.strategy.bws;

import lombok.Builder;
import lombok.Data;
import pro.crypto.model.Position;
import pro.crypto.model.StrategyRequest;
import pro.crypto.model.tick.Tick;
import pro.crypto.model.tick.TimeFrame;

import java.util.Set;

@Data
@Builder
public class BWSRequest implements StrategyRequest {

    private Tick[] originalData;

    private int acSlowPeriod;

    private int acFastPeriod;

    private int acSmoothedPeriod;

    private int alligatorJawPeriod;

    private int alligatorJawOffset;

    private int alligatorTeethPeriod;

    private int alligatorTeethOffset;

    private int alligatorLipsPeriod;

    private int alligatorLipsOffset;

    private TimeFrame alligatorTimeFrame;

    private int aoSlowPeriod;

    private int aoFastPeriod;

    private Set<Position> positions;

}
