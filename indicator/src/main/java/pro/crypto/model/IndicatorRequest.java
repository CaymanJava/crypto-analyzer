package pro.crypto.model;

import pro.crypto.model.tick.Tick;

public interface IndicatorRequest {

    Tick[] getOriginalData();

    void setOriginalData(Tick[] ticks);

}
