package pro.crypto.model;

import pro.crypto.model.tick.Tick;

public interface AnalyzerRequest {

    Tick[] getOriginalData();

    IndicatorResult[] getIndicatorResults();

}
