package pro.crypto.analyzer.helper.divergence;

import pro.crypto.helper.IncreasedQualifier;
import pro.crypto.helper.MathHelper;
import pro.crypto.helper.PriceExtractor;
import pro.crypto.model.tick.PriceType;
import pro.crypto.model.tick.Tick;

import java.math.BigDecimal;
import java.util.*;
import java.util.function.BiFunction;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import static java.math.BigDecimal.ZERO;
import static java.util.Comparator.comparing;
import static java.util.Objects.nonNull;
import static java.util.stream.Stream.*;
import static pro.crypto.analyzer.helper.divergence.DivergenceClass.*;

public class Divergence {

    private final Tick[] originalData;
    private final BigDecimal[] indicatorValues;

    private Integer[] indicatorIncreaseIndexes;
    private Integer[] indicatorDecreaseIndexes;
    private Set<Integer> priceIncreaseIndexes;
    private Set<Integer> priceDecreaseIndexes;

    public Divergence(DivergenceRequest request) {
        this.originalData = request.getOriginalData();
        this.indicatorValues = request.getIndicatorValues();
    }

    public DivergenceResult[] find() {
        Boolean[] priceIncreases = calculatePriceIncreases();
        Boolean[] indicatorIncreases = calculateIndicatorIncreases();
        priceIncreaseIndexes = new HashSet<>(Arrays.asList(extractIncreaseDecreaseIndexes(priceIncreases, true)));
        priceDecreaseIndexes = new HashSet<>(Arrays.asList(extractIncreaseDecreaseIndexes(priceIncreases, false)));
        indicatorIncreaseIndexes = extractIncreaseDecreaseIndexes(indicatorIncreases, true);
        indicatorDecreaseIndexes = extractIncreaseDecreaseIndexes(indicatorIncreases, false);
        return findDivergences();
    }

    private Boolean[] calculatePriceIncreases() {
        return IncreasedQualifier.define(PriceExtractor.extractValuesByType(originalData, PriceType.CLOSE));
    }

    private Boolean[] calculateIndicatorIncreases() {
        return IncreasedQualifier.define(indicatorValues);
    }

    private Integer[] extractIncreaseDecreaseIndexes(Boolean[] increasesDecreases, Boolean increase) {
        return IntStream.range(0, increasesDecreases.length)
                .filter(idx -> nonNull(increasesDecreases[idx]))
                .filter(idx -> increasesDecreases[idx] == increase)
                .boxed()
                .toArray(Integer[]::new);
    }

    private DivergenceResult[] findDivergences() {
        DivergenceResult[] bearerDivergences = findBearerDivergences();
        DivergenceResult[] bullishDivergences = findBullishDivergences();
        return mergeDivergences(bearerDivergences, bullishDivergences);
    }

    private DivergenceResult[] findBearerDivergences() {
        return IntStream.range(0, indicatorIncreaseIndexes.length - 1)
                .filter(idx -> nonNull(indicatorDecreaseIndexes[idx]))
                .mapToObj(this::findBearerDivergenceWithRemainIndexes)
                .flatMap(Stream::of)
                .toArray(DivergenceResult[]::new);
    }

    private DivergenceResult[] findBearerDivergenceWithRemainIndexes(int outerIndex) {
        return IntStream.range(outerIndex + 1, indicatorIncreaseIndexes.length)
                .filter(idx -> pricesHaveIncreases(outerIndex, idx))
                .filter(idx -> !indicatorValuesCrossZero(outerIndex, idx))
                .filter(idx -> isDivergenceExist(outerIndex, idx))
                .filter(idx -> !middleIncreaseValuesCrossLine(outerIndex, idx))
                .mapToObj(idx -> defineBearerDivergence(outerIndex, idx))
                .toArray(DivergenceResult[]::new);
    }

    private boolean pricesHaveIncreases(int fromIndex, int toIndex) {
        return priceIncreaseIndexes.contains(fromIndex) && priceIncreaseIndexes.contains(toIndex);
    }

    private boolean middleIncreasePricesCrossLine(int fromIndex, int toIndex) {
        return IntStream.range(fromIndex + 1, toIndex)
                .mapToObj(idx -> isMiddleIncreasePriceCrossLine(fromIndex, toIndex, idx))
                .filter(crossLine -> crossLine)
                .findAny()
                .orElse(false);
    }

    private Boolean isMiddleIncreasePriceCrossLine(int fromIndex, int toIndex, int middleIndex) {
        if (isMiddlePriceLower(fromIndex, toIndex, middleIndex) || pricesTheSame(fromIndex, toIndex, middleIndex)) {
            return false;
        }
        if (isMiddlePriceHigher(fromIndex, toIndex, middleIndex)) {
            return true;
        }
        return isPriceCrossLine(fromIndex, toIndex, middleIndex, this::isIncreasePriceCrossLine);
    }

    private boolean isMiddlePriceLower(int fromIndex, int toIndex, int middleIndex) {
        return comparePrices(middleIndex, fromIndex) < 0 && comparePrices(middleIndex, toIndex) < 0;
    }

    private boolean isMiddlePriceHigher(int fromIndex, int toIndex, int middleIndex) {
        return comparePrices(middleIndex, fromIndex) > 0 && comparePrices(middleIndex, toIndex) > 0;
    }

    private boolean pricesTheSame(int fromIndex, int toIndex, int middleIndex) {
        return comparePrices(middleIndex, fromIndex) == 0 && comparePrices(middleIndex, toIndex) == 0;
    }

    private Boolean isPriceCrossLine(int fromIndex, int toIndex, int middleIndex,
                                     BiFunction<BigDecimal, Integer, Boolean> crossFunction) {
        Triangular priceTriangular = calculatePriceTriangular(fromIndex, toIndex);
        BigDecimal internalOrdinateCatheter = calculateInternalOrdinateCatheter(priceTriangular, fromIndex, middleIndex);
        BigDecimal possiblePriceIntersection = definePossiblePriceIntersection(internalOrdinateCatheter, fromIndex, toIndex);
        return crossFunction.apply(possiblePriceIntersection, middleIndex);
    }

    private Boolean isIncreasePriceCrossLine(BigDecimal intersectionPoint, int priceIndex) {
        return originalData[priceIndex].getClose().compareTo(intersectionPoint) > 0;
    }

    private Triangular calculatePriceTriangular(int fromIndex, int toIndex) {
        BigDecimal abscissaCatheter = new BigDecimal(toIndex - fromIndex);
        BigDecimal ordinateCatheter = originalData[fromIndex].getClose().subtract(originalData[toIndex].getClose()).abs();
        BigDecimal hypotenuse = calculateHypotenuse(abscissaCatheter, ordinateCatheter);
        return new Triangular(abscissaCatheter, ordinateCatheter, hypotenuse);
    }

    private BigDecimal definePossiblePriceIntersection(BigDecimal internalOrdinateCatheter, int fromIndex, int toIndex) {
        return originalData[fromIndex].getClose().compareTo(originalData[toIndex].getClose()) > 0
                ? originalData[fromIndex].getClose().subtract(internalOrdinateCatheter)
                : originalData[fromIndex].getClose().add(internalOrdinateCatheter);
    }

    private boolean middleIncreaseValuesCrossLine(int fromIndex, int toIndex) {
        return middleIncreasePricesCrossLine(fromIndex, toIndex) || middleIncreaseIndicatorsCrossLine(fromIndex, toIndex);
    }

    private boolean middleIncreaseIndicatorsCrossLine(int fromIndex, int toIndex) {
        if (middleIndicatorsInAnotherPlane(fromIndex, toIndex)) {
            return true;
        }
        return IntStream.range(fromIndex + 1, toIndex)
                .mapToObj(idx -> isMiddleIncreaseIndicatorCrossLine(fromIndex, toIndex, idx))
                .filter(crossLine -> crossLine)
                .findAny()
                .orElse(false);
    }

    private boolean isMiddleIncreaseIndicatorCrossLine(int fromIndex, int toIndex, int middleIndex) {
        if (isMiddleIndicatorLower(fromIndex, toIndex, middleIndex) || indicatorValuesTheSame(fromIndex, toIndex, middleIndex)) {
            return false;
        }
        if (isMiddleIndicatorHigher(fromIndex, toIndex, middleIndex)) {
            return true;
        }
        return isIndicatorCrossLine(fromIndex, toIndex, middleIndex, this::isIncreaseIndicatorCrossLine);
    }

    private boolean isMiddleIndicatorLower(int fromIndex, int toIndex, int middleIndex) {
        return compareIndicatorValues(middleIndex, fromIndex) < 0 && compareIndicatorValues(middleIndex, toIndex) < 0;
    }

    private boolean isMiddleIndicatorHigher(int fromIndex, int toIndex, int middleIndex) {
        return compareIndicatorValues(middleIndex, fromIndex) > 0 && compareIndicatorValues(middleIndex, toIndex) > 0;
    }

    private boolean indicatorValuesTheSame(int fromIndex, int toIndex, int middleIndex) {
        return compareIndicatorValues(middleIndex, fromIndex) == 0 && compareIndicatorValues(middleIndex, toIndex) == 0;
    }

    private boolean isIndicatorCrossLine(int fromIndex, int toIndex, int middleIndex, BiFunction<BigDecimal, Integer, Boolean> crossFunction) {
        Triangular indicatorTriangular = calculateIndicatorTriangular(fromIndex, toIndex);
        BigDecimal internalOrdinateCatheter = calculateInternalOrdinateCatheter(indicatorTriangular, fromIndex, middleIndex);
        BigDecimal possiblePriceIntersection = definePossibleIndicatorIntersection(internalOrdinateCatheter, fromIndex, toIndex);
        return crossFunction.apply(possiblePriceIntersection, middleIndex);
    }

    private Boolean isIncreaseIndicatorCrossLine(BigDecimal intersectionPoint, int indicatorIndex) {
        return indicatorValues[indicatorIndex].compareTo(intersectionPoint) > 0
                && priceDeviationLessThanOnePercent(indicatorValues[indicatorIndex], intersectionPoint);
    }

    private Triangular calculateIndicatorTriangular(int fromIndex, int toIndex) {
        BigDecimal abscissaCatheter = new BigDecimal(toIndex - fromIndex);
        BigDecimal ordinateCatheter = indicatorValues[fromIndex].subtract(indicatorValues[toIndex]).abs();
        BigDecimal hypotenuse = calculateHypotenuse(abscissaCatheter, ordinateCatheter);
        return new Triangular(abscissaCatheter, ordinateCatheter, hypotenuse);
    }

    private BigDecimal calculateHypotenuse(BigDecimal abscissaCatheter, BigDecimal ordinateCatheter) {
        return MathHelper.sqrt(abscissaCatheter.multiply(abscissaCatheter).add(ordinateCatheter.multiply(ordinateCatheter)));
    }

    private BigDecimal calculateInternalOrdinateCatheter(Triangular externalTriangular, int fromIndex, int middleIndex) {
        BigDecimal ratio = MathHelper.divide(externalTriangular.getAbscissaCatheter(), new BigDecimal(middleIndex - fromIndex));
        BigDecimal internalHypotenuse = MathHelper.divide(externalTriangular.getHypotenuse(), ratio);
        return calculateOrdinateInternalCatheter(internalHypotenuse, new BigDecimal(middleIndex - fromIndex));
    }

    private BigDecimal calculateOrdinateInternalCatheter(BigDecimal hypotenuse, BigDecimal catheter) {
        return MathHelper.sqrt(hypotenuse.multiply(hypotenuse).subtract(catheter.multiply(catheter)).abs());
    }

    private BigDecimal definePossibleIndicatorIntersection(BigDecimal ordinateExternalCatheter, int fromIndex, int toIndex) {
        return indicatorValues[fromIndex].compareTo(indicatorValues[toIndex]) > 0
                ? indicatorValues[fromIndex].subtract(ordinateExternalCatheter)
                : indicatorValues[fromIndex].add(ordinateExternalCatheter);
    }

    private DivergenceResult defineBearerDivergence(int fromIndex, int toIndex) {
        return new DivergenceResult(DivergenceType.BEARER, defineBearerDivergenceClass(fromIndex, toIndex), fromIndex, toIndex);
    }

    private DivergenceClass defineBearerDivergenceClass(int fromIndex, int toIndex) {
        if (!priceDeviationLessThanOnePercent(fromIndex, toIndex) && lastPriceHigher(fromIndex, toIndex) && lastIndicatorValueLower(fromIndex, toIndex)) {
            return A_CLASS;
        }
        if (priceDeviationLessThanOnePercent(fromIndex, toIndex) && lastIndicatorValueLower(fromIndex, toIndex)) {
            return B_CLASS;
        }
        return C_CLASS;
    }

    private boolean lastPriceHigher(int fromIndex, int toIndex) {
        return originalData[toIndex].getClose().compareTo(originalData[fromIndex].getClose()) > 0;
    }

    private boolean lastIndicatorValueLower(int fromIndex, int toIndex) {
        return indicatorValues[toIndex].compareTo(indicatorValues[fromIndex]) < 0;
    }

    private DivergenceResult[] findBullishDivergences() {
        return IntStream.range(0, indicatorIncreaseIndexes.length - 1)
                .filter(idx -> nonNull(indicatorDecreaseIndexes[idx]))
                .mapToObj(this::findBullishDivergenceWithRemainIndexes)
                .flatMap(Stream::of)
                .toArray(DivergenceResult[]::new);
    }

    private DivergenceResult[] findBullishDivergenceWithRemainIndexes(int outerIndex) {
        return IntStream.range(outerIndex + 1, indicatorIncreaseIndexes.length)
                .filter(idx -> pricesHaveDecreases(outerIndex, idx))
                .filter(idx -> !indicatorValuesCrossZero(outerIndex, idx))
                .filter(idx -> isDivergenceExist(outerIndex, idx))
                .filter(idx -> !middleDecreaseValuesCrossLine(outerIndex, idx))
                .mapToObj(idx -> defineBullishDivergence(outerIndex, idx))
                .toArray(DivergenceResult[]::new);
    }

    private boolean pricesHaveDecreases(int fromIndex, int toIndex) {
        return priceDecreaseIndexes.contains(fromIndex) && priceDecreaseIndexes.contains(toIndex);
    }

    private boolean indicatorValuesCrossZero(int fromIndex, int toIndex) {
        return indicatorValues[fromIndex].compareTo(ZERO) != indicatorValues[toIndex].compareTo(ZERO);
    }

    private boolean isDivergenceExist(int fromIndex, int toIndex) {
        return comparePrices(fromIndex, toIndex) != compareIndicatorValues(fromIndex, toIndex);
    }

    private int comparePrices(int firstIndex, int secondIndex) {
        return originalData[firstIndex].getClose().compareTo(originalData[secondIndex].getClose());
    }

    private int compareIndicatorValues(int fromIndex, int toIndex) {
        return indicatorValues[fromIndex].compareTo(indicatorValues[toIndex]);
    }

    private boolean middleDecreaseValuesCrossLine(int fromIndex, int toIndex) {
        return middleDecreasePricesCrossLine(fromIndex, toIndex) || middleDecreaseIndicatorsCrossLine(fromIndex, toIndex);
    }

    private boolean middleDecreasePricesCrossLine(int fromIndex, int toIndex) {
        return IntStream.range(fromIndex + 1, toIndex)
                .mapToObj(idx -> isMiddleDecreasePriceCrossLine(fromIndex, toIndex, idx))
                .findAny()
                .orElse(false);
    }

    private Boolean isMiddleDecreasePriceCrossLine(int fromIndex, int toIndex, int middleIndex) {
        if (isMiddlePriceHigher(fromIndex, toIndex, middleIndex) || pricesTheSame(fromIndex, toIndex, middleIndex)) {
            return false;
        }
        if (isMiddlePriceLower(fromIndex, toIndex, middleIndex)) {
            return true;
        }
        return isPriceCrossLine(fromIndex, toIndex, middleIndex, this::isDecreasePriceCrossLine);
    }

    private Boolean isDecreasePriceCrossLine(BigDecimal intersectionPoint, int priceIndex) {
        return originalData[priceIndex].getClose().compareTo(intersectionPoint) < 0;
    }

    private boolean middleDecreaseIndicatorsCrossLine(int fromIndex, int toIndex) {
        return middleIndicatorsInAnotherPlane(fromIndex, toIndex) ? true : IntStream.range(fromIndex + 1, toIndex)
                .mapToObj(idx -> isMiddleDecreaseIndicatorCrossLine(fromIndex, toIndex, idx))
                .filter(crossLine -> crossLine)
                .findAny()
                .orElse(false);
    }

    private boolean middleIndicatorsInAnotherPlane(int fromIndex, int toIndex) {
        return IntStream.range(fromIndex + 1, toIndex)
                .mapToObj(idx -> middleIndicatorInAnotherPlane(fromIndex, idx))
                .filter(anotherPlane -> anotherPlane)
                .findAny()
                .orElse(false);
    }

    private boolean middleIndicatorInAnotherPlane(int fromIndex, int middleIndex) {
        return indicatorValues[fromIndex].compareTo(ZERO) != indicatorValues[middleIndex].compareTo(ZERO);
    }

    private boolean isMiddleDecreaseIndicatorCrossLine(int fromIndex, int toIndex, int middleIndex) {
        if (isMiddleIndicatorHigher(fromIndex, toIndex, middleIndex) || indicatorValuesTheSame(fromIndex, toIndex, middleIndex)) {
            return false;
        }
        if (isMiddleIndicatorLower(fromIndex, toIndex, middleIndex)) {
            return true;
        }
        return isIndicatorCrossLine(fromIndex, toIndex, middleIndex, this::isDecreaseIndicatorCrossLine);
    }

    private Boolean isDecreaseIndicatorCrossLine(BigDecimal intersectionPoint, int indicatorIndex) {
        return indicatorValues[indicatorIndex].compareTo(intersectionPoint) > 0
                && priceDeviationLessThanOnePercent(indicatorValues[indicatorIndex], intersectionPoint);
    }

    private DivergenceResult defineBullishDivergence(int fromIndex, int toIndex) {
        return new DivergenceResult(DivergenceType.BULLISH, defineBullishDivergenceClass(fromIndex, toIndex), fromIndex, toIndex);
    }

    private DivergenceClass defineBullishDivergenceClass(int fromIndex, int toIndex) {
        if (!priceDeviationLessThanOnePercent(fromIndex, toIndex) && lastPriceLower(fromIndex, toIndex) && lastIndicatorValueHigher(fromIndex, toIndex)) {
            return A_CLASS;
        }
        if (priceDeviationLessThanOnePercent(fromIndex, toIndex) && lastIndicatorValueHigher(fromIndex, toIndex)) {
            return B_CLASS;
        }
        return C_CLASS;
    }

    private boolean lastPriceLower(int fromIndex, int toIndex) {
        return originalData[toIndex].getClose().compareTo(originalData[fromIndex].getClose()) < 0;
    }

    private boolean lastIndicatorValueHigher(int fromIndex, int toIndex) {
        return indicatorValues[toIndex].compareTo(indicatorValues[fromIndex]) < 0;
    }

    private boolean priceDeviationLessThanOnePercent(int fromIndex, int toIndex) {
        BigDecimal deviation = calculateDeviation(originalData[fromIndex].getClose(), originalData[toIndex].getClose());
        return deviation.compareTo(new BigDecimal(1)) <= 0;
    }

    private boolean priceDeviationLessThanOnePercent(BigDecimal firstValue, BigDecimal secondValue) {
        BigDecimal deviation = calculateDeviation(firstValue, secondValue);
        return deviation.compareTo(new BigDecimal(1)) <= 0;
    }

    private BigDecimal calculateDeviation(BigDecimal firstValue, BigDecimal secondValue) {
        return MathHelper.divide(secondValue
                        .subtract(firstValue).abs()
                        .multiply(new BigDecimal(100)),
                MathHelper.max(secondValue, firstValue));
    }

    private DivergenceResult[] mergeDivergences(DivergenceResult[] bearerDivergences, DivergenceResult[] bullishDivergences) {
        return concat(of(bearerDivergences), of(bullishDivergences))
                .sorted(comparing(DivergenceResult::getIndexTo)
                        .thenComparing(DivergenceResult::getIndexFrom))
                .toArray(DivergenceResult[]::new);
    }

}
