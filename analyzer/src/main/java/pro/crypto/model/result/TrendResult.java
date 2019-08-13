package pro.crypto.model.result;

import pro.crypto.model.analyzer.Trend;

public interface TrendResult extends AnalyzerResult {

    Trend getTrend();

}
