package pro.crypto.response;

import pro.crypto.model.strategy.Position;
import pro.crypto.model.tick.Tick;

import java.util.Set;

public interface StrategyResult {

    Tick getTick();

    Set<Position> getPositions();

}
