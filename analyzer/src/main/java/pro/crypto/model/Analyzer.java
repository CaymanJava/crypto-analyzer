package pro.crypto.model;

public interface Analyzer<T extends AnalyzerResult> {

    void analyze();

    T[] getResult();

}
