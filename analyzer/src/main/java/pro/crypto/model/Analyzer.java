package pro.crypto.model;

import pro.crypto.model.result.AnalyzerResult;

public interface Analyzer<T extends AnalyzerResult> {

    void analyze();

    T[] getResult();

}
