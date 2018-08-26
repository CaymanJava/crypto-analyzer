package pro.crypto.model.result;

public interface CrossBandResult extends AnalyzerResult {

    boolean isCrossUpperBand();

    boolean isCrossLowerBand();

    boolean isCrossMiddleBand();

}
