package pro.crypto.analyzer.mi;

import pro.crypto.indicator.mi.MIResult;
import pro.crypto.model.Analyzer;
import pro.crypto.model.AnalyzerRequest;

import java.math.BigDecimal;
import java.util.Set;
import java.util.stream.IntStream;

import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;
import static java.util.Optional.ofNullable;
import static java.util.stream.Collectors.toSet;

public class MIAnalyzer implements Analyzer<MIAnalyzerResult> {

    private final MIResult[] indicatorResults;
    private final BigDecimal firstReversalLine;
    private final BigDecimal secondReversalLine;
    private final int allowableGap;

    private MIAnalyzerResult[] result;

    public MIAnalyzer(AnalyzerRequest analyzerRequest) {
        MIAnalyzerRequest request = (MIAnalyzerRequest) analyzerRequest;
        this.indicatorResults = (MIResult[]) request.getIndicatorResults();
        this.firstReversalLine = extractFirstReversalLine(request);
        this.secondReversalLine = extractSecondReversalLine(request);
        this.allowableGap = extractAllowableGap(request);
    }

    @Override
    public void analyze() {
        boolean[] reversalBulges = findReversalBulges();
        buildMIAnalyzerResult(reversalBulges);
    }

    @Override
    public MIAnalyzerResult[] getResult() {
        if (isNull(result)) {
            analyze();
        }
        return result;
    }

    private BigDecimal extractFirstReversalLine(MIAnalyzerRequest request) {
        return ofNullable(request.getFirstReversalLine())
                .map(BigDecimal::new)
                .orElse(new BigDecimal(27));
    }

    private BigDecimal extractSecondReversalLine(MIAnalyzerRequest request) {
        return ofNullable(request.getSecondReversalLine())
                .map(BigDecimal::new)
                .orElse(new BigDecimal(26.5));
    }

    private Integer extractAllowableGap(MIAnalyzerRequest request) {
        return ofNullable(request.getAllowableGap()).orElse(25);
    }

    private boolean[] findReversalBulges() {
        Set<Integer> firstIntersectionIndexes = findFirstIntersectionIndexes();
        Set<Integer> secondIntersectionIndexes = findSecondIntersectionIndexes();
        return findReversalBulges(firstIntersectionIndexes, secondIntersectionIndexes);
    }

    private Set<Integer> findFirstIntersectionIndexes() {
        return IntStream.range(0, indicatorResults.length)
                .filter(this::isFirstIntersection)
                .boxed()
                .collect(toSet());
    }

    private boolean isFirstIntersection(int currentIndex) {
        return isPossibleDefineIntersection(currentIndex) && isFirstLineIntersection(currentIndex);
    }

    private boolean isFirstLineIntersection(int currentIndex) {
        return indicatorResults[currentIndex - 1].getIndicatorValue().compareTo(firstReversalLine) < 0
                && indicatorResults[currentIndex].getIndicatorValue().compareTo(firstReversalLine) > 0;
    }

    private Set<Integer> findSecondIntersectionIndexes() {
        return IntStream.range(0, indicatorResults.length)
                .filter(this::isSecondIntersection)
                .boxed()
                .collect(toSet());
    }

    private boolean isSecondIntersection(int currentIndex) {
        return isPossibleDefineIntersection(currentIndex) && isSecondLineIntersection(currentIndex);
    }

    private boolean isSecondLineIntersection(int currentIndex) {
        return indicatorResults[currentIndex - 1].getIndicatorValue().compareTo(secondReversalLine) > 0
                && indicatorResults[currentIndex].getIndicatorValue().compareTo(secondReversalLine) < 0;
    }

    private boolean isPossibleDefineIntersection(int currentIndex) {
        return currentIndex > 0
                && nonNull(indicatorResults[currentIndex - 1].getIndicatorValue())
                && nonNull(indicatorResults[currentIndex].getIndicatorValue());
    }

    private boolean[] findReversalBulges(Set<Integer> firstIntersectionIndexes, Set<Integer> secondIntersectionIndexes) {
        boolean[] reversalBulges = new boolean[indicatorResults.length];
        secondIntersectionIndexes.stream()
                .filter(index -> isReversalBulge(index, firstIntersectionIndexes))
                .forEach(index -> reversalBulges[index] = true);
        return reversalBulges;
    }

    private boolean isReversalBulge(int secondIntersectionIndex, Set<Integer> firstIntersectionIndexes) {
        return IntStream.range(secondIntersectionIndex - allowableGap, secondIntersectionIndex)
                .filter(firstIntersectionIndexes::contains)
                .findAny()
                .isPresent();
    }

    private void buildMIAnalyzerResult(boolean[] reversalBulges) {
        result = IntStream.range(0, indicatorResults.length)
                .mapToObj(idx -> new MIAnalyzerResult(indicatorResults[idx].getTime(), reversalBulges[idx]))
                .toArray(MIAnalyzerResult[]::new);
    }

}
