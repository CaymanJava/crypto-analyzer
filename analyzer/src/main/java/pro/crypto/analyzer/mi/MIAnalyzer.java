package pro.crypto.analyzer.mi;

import pro.crypto.indicator.mi.MIResult;
import pro.crypto.model.Analyzer;
import pro.crypto.model.AnalyzerRequest;

import java.math.BigDecimal;
import java.util.Set;
import java.util.stream.IntStream;

import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;
import static java.util.stream.Collectors.toSet;

public class MIAnalyzer implements Analyzer<MIAnalyzerResult> {

    private final static BigDecimal FIRST_REVERSAL_LINE = new BigDecimal(27);
    private final static BigDecimal SECOND_REVERSAL_LINE = new BigDecimal(26.5);
    private final static int ALLOWABLE_GAP = 25;

    private final MIResult[] indicatorResults;

    private MIAnalyzerResult[] result;

    public MIAnalyzer(AnalyzerRequest request) {
        this.indicatorResults = (MIResult[]) request.getIndicatorResults();
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
        return indicatorResults[currentIndex - 1].getIndicatorValue().compareTo(FIRST_REVERSAL_LINE) < 0
                && indicatorResults[currentIndex].getIndicatorValue().compareTo(FIRST_REVERSAL_LINE) > 0;
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
        return indicatorResults[currentIndex - 1].getIndicatorValue().compareTo(SECOND_REVERSAL_LINE) > 0
                && indicatorResults[currentIndex].getIndicatorValue().compareTo(SECOND_REVERSAL_LINE) < 0;
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
        return IntStream.range(secondIntersectionIndex - ALLOWABLE_GAP, secondIntersectionIndex)
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
