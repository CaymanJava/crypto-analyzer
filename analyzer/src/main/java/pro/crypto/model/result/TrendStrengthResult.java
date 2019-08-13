package pro.crypto.model.result;

import pro.crypto.model.analyzer.TrendStrength;

public interface TrendStrengthResult extends AnalyzerResult {

    TrendStrength getTrendStrength();

}
