package pro.crypto.model;

import pro.crypto.model.tick.Tick;

public interface StrategyRequest {

    Tick[] getOriginalData();

}
