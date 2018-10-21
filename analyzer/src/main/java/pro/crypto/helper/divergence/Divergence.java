package pro.crypto.helper.divergence;

import pro.crypto.helper.IncreasedQualifier;
import pro.crypto.helper.MathHelper;
import pro.crypto.helper.PriceVolumeExtractor;
import pro.crypto.model.tick.PriceType;
import pro.crypto.model.tick.Tick;

import java.math.BigDecimal;
import java.util.*;
import java.util.function.BiFunction;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import static java.math.BigDecimal.ONE;
import static java.math.BigDecimal.ZERO;
import static java.util.Arrays.asList;
import static java.util.Comparator.comparing;
import static java.util.Objects.nonNull;
import static java.util.stream.Stream.*;
import static pro.crypto.helper.divergence.DivergenceClass.*;
import static pro.crypto.helper.divergence.DivergenceType.*;
import static pro.crypto.helper.divergence.DivergenceType.BULLISH;

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
        priceIncreaseIndexes = new HashSet<>(asList(extractIncreaseDecreaseIndexes(priceIncreases, true)));
        priceDecreaseIndexes = new HashSet<>(asList(extractIncreaseDecreaseIndexes(priceIncreases, false)));
        indicatorIncreaseIndexes = extractIncreaseDecreaseIndexes(indicatorIncreases, true);
        indicatorDecreaseIndexes = extractIncreaseDecreaseIndexes(indicatorIncreases, false);
        return findDivergences();
    }

    private Boolean[] calculatePriceIncreases() {
        return IncreasedQualifier.define(PriceVolumeExtractor.extract(originalData, PriceType.CLOSE));
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
                .mapToObj(this::findBearerDivergenceWithRemainIndexes)
                .flatMap(Stream::of)
                .toArray(DivergenceResult[]::new);
    }

    private DivergenceResult[] findBearerDivergenceWithRemainIndexes(int outerIndex) {
        return IntStream.range(outerIndex + 1, indicatorIncreaseIndexes.length)
                .filter(idx -> pricesHaveIncreases(indicatorIncreaseIndexes[outerIndex], indicatorIncreaseIndexes[idx]))
                .filter(idx -> !isIndicatorValuesCrossZero(indicatorIncreaseIndexes[outerIndex], indicatorIncreaseIndexes[idx]))
                .filter(idx -> isDivergenceExist(indicatorIncreaseIndexes[outerIndex], indicatorIncreaseIndexes[idx]))
                .filter(idx -> !middleIncreaseValuesCrossLine(indicatorIncreaseIndexes[outerIndex], indicatorIncreaseIndexes[idx]))
                .mapToObj(idx -> defineBearerDivergence(indicatorIncreaseIndexes[outerIndex], indicatorIncreaseIndexes[idx]))
                .filter(Objects::nonNull)
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
                && isPriceDeviationLessThanOnePercent(indicatorValues[indicatorIndex], intersectionPoint);
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
        DivergenceClass divergenceClass = defineBearerDivergenceClass(fromIndex, toIndex);
        return nonNull(divergenceClass)
                ? new DivergenceResult(BEARISH, divergenceClass, fromIndex, toIndex)
                : null;
    }

    private DivergenceClass defineBearerDivergenceClass(int fromIndex, int toIndex) {
        if (isClassicBearerDivergence(fromIndex, toIndex)) {
            return CLASSIC;
        }
        if (isExtendedBearerDivergence(fromIndex, toIndex)) {
            return EXTENDED;
        }
        if (isHiddenBearerDivergence(fromIndex, toIndex)) {
            return HIDDEN;
        }
        return null;
    }

    private boolean isClassicBearerDivergence(int fromIndex, int toIndex) {
        return !isPriceDeviationLessThanOnePercent(fromIndex, toIndex)
                && isFirstClassicBearerType(fromIndex, toIndex)
                || isSecondClassicBearerType(fromIndex, toIndex);
    }

    private boolean isFirstClassicBearerType(int fromIndex, int toIndex) {
        return isLastPriceHigher(fromIndex, toIndex) && isLastIndicatorValueLower(fromIndex, toIndex);
    }

    private boolean isSecondClassicBearerType(int fromIndex, int toIndex) {
        return isLastPriceHigher(fromIndex, toIndex) && isIndicatorDeviationLessThanOnePercent(fromIndex, toIndex);
    }

    private boolean isExtendedBearerDivergence(int fromIndex, int toIndex) {
        return isPriceDeviationLessThanOnePercent(fromIndex, toIndex) && isLastIndicatorValueLower(fromIndex, toIndex);
    }

    private boolean isHiddenBearerDivergence(int fromIndex, int toIndex) {
        return isLastPriceLower(fromIndex, toIndex)
                && isLastIndicatorValueHigher(fromIndex, toIndex);
    }

    private boolean isLastPriceHigher(int fromIndex, int toIndex) {
        return originalData[toIndex].getClose().compareTo(originalData[fromIndex].getClose()) > 0;
    }

    private boolean isLastIndicatorValueLower(int fromIndex, int toIndex) {
        return indicatorValues[toIndex].compareTo(indicatorValues[fromIndex]) < 0;
    }

    private DivergenceResult[] findBullishDivergences() {
        return IntStream.range(0, indicatorDecreaseIndexes.length - 1)
                .mapToObj(this::findBullishDivergenceWithRemainIndexes)
                .flatMap(Stream::of)
                .toArray(DivergenceResult[]::new);
    }

    private DivergenceResult[] findBullishDivergenceWithRemainIndexes(int outerIndex) {
        return IntStream.range(outerIndex + 1, indicatorDecreaseIndexes.length)
                .filter(idx -> pricesHaveDecreases(indicatorDecreaseIndexes[outerIndex], indicatorDecreaseIndexes[idx]))
                .filter(idx -> !isIndicatorValuesCrossZero(indicatorDecreaseIndexes[outerIndex], indicatorDecreaseIndexes[idx]))
                .filter(idx -> isDivergenceExist(indicatorDecreaseIndexes[outerIndex], indicatorDecreaseIndexes[idx]))
                .filter(idx -> !middleDecreaseValuesCrossLine(indicatorDecreaseIndexes[outerIndex], indicatorDecreaseIndexes[idx]))
                .mapToObj(idx -> defineBullishDivergence(indicatorDecreaseIndexes[outerIndex], indicatorDecreaseIndexes[idx]))
                .filter(Objects::nonNull)
                .toArray(DivergenceResult[]::new);
    }

    private boolean pricesHaveDecreases(int fromIndex, int toIndex) {
        return priceDecreaseIndexes.contains(fromIndex) && priceDecreaseIndexes.contains(toIndex);
    }

    private boolean isIndicatorValuesCrossZero(int fromIndex, int toIndex) {
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
        return middleIndicatorsInAnotherPlane(fromIndex, toIndex) || IntStream.range(fromIndex + 1, toIndex)
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
                && isPriceDeviationLessThanOnePercent(indicatorValues[indicatorIndex], intersectionPoint);
    }

    private DivergenceResult defineBullishDivergence(int fromIndex, int toIndex) {
        DivergenceClass divergenceClass = defineBullishDivergenceClass(fromIndex, toIndex);
        return nonNull(divergenceClass)
                ? new DivergenceResult(BULLISH, divergenceClass, fromIndex, toIndex)
                : null;
    }

    private DivergenceClass defineBullishDivergenceClass(int fromIndex, int toIndex) {
        if (isClassicBullishDivergence(fromIndex, toIndex)) {
            return CLASSIC;
        }
        if (isExtendedBullishDivergence(fromIndex, toIndex)) {
            return EXTENDED;
        }
        if (isHiddenBullishDivergence(fromIndex, toIndex)) {
            return HIDDEN;
        }
        return null;
    }

    private boolean isClassicBullishDivergence(int fromIndex, int toIndex) {
        return !isPriceDeviationLessThanOnePercent(fromIndex, toIndex)
                && isFirstClassicBullishType(fromIndex, toIndex)
                && isSecondClassicBullishType(fromIndex, toIndex);
    }

    private boolean isFirstClassicBullishType(int fromIndex, int toIndex) {
        return isLastPriceLower(fromIndex, toIndex) && isLastIndicatorValueHigher(fromIndex, toIndex);
    }

    private boolean isLastPriceLower(int fromIndex, int toIndex) {
        return originalData[toIndex].getClose().compareTo(originalData[fromIndex].getClose()) < 0;
    }

    private boolean isLastIndicatorValueHigher(int fromIndex, int toIndex) {
        return indicatorValues[toIndex].compareTo(indicatorValues[fromIndex]) < 0;
    }

    private boolean isSecondClassicBullishType(int fromIndex, int toIndex) {
        return isLastPriceLower(fromIndex, toIndex) && isIndicatorDeviationLessThanOnePercent(fromIndex, toIndex);
    }

    private boolean isExtendedBullishDivergence(int fromIndex, int toIndex) {
        return isPriceDeviationLessThanOnePercent(fromIndex, toIndex) && isLastIndicatorValueHigher(fromIndex, toIndex);
    }

    private boolean isHiddenBullishDivergence(int fromIndex, int toIndex) {
        return isLastPriceHigher(fromIndex, toIndex)
                && isLastIndicatorValueLower(fromIndex, toIndex);
    }

    private boolean isPriceDeviationLessThanOnePercent(int fromIndex, int toIndex) {
        BigDecimal deviation = calculateDeviation(originalData[fromIndex].getClose(), originalData[toIndex].getClose());
        return deviation.compareTo(ONE) <= 0;
    }

    private boolean isIndicatorDeviationLessThanOnePercent(int fromIndex, int toIndex) {
        BigDecimal deviation = calculateDeviation(indicatorValues[fromIndex], indicatorValues[toIndex]);
        return deviation.compareTo(ONE) <= 0;
    }

    private boolean isPriceDeviationLessThanOnePercent(BigDecimal firstValue, BigDecimal secondValue) {
        BigDecimal deviation = calculateDeviation(firstValue, secondValue);
        return deviation.compareTo(ONE) <= 0;
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
