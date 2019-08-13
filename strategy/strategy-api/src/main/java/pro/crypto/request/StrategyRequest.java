package pro.crypto.request;

import pro.crypto.model.tick.Tick;

public interface StrategyRequest {

    Tick[] getOriginalData();

    void setOriginalData(Tick[] ticks);

}
