package pro.crypto.indicator.fractal;

import pro.crypto.exception.WrongIncomingParametersException;
import pro.crypto.helper.PriceExtractor;
import pro.crypto.model.Indicator;
import pro.crypto.model.IndicatorRequest;
import pro.crypto.model.IndicatorType;
import pro.crypto.model.tick.PriceType;
import pro.crypto.model.tick.Tick;

import java.math.BigDecimal;
import java.util.function.BiFunction;
import java.util.stream.IntStream;

import static java.lang.String.format;
import static java.util.Objects.isNull;
import static pro.crypto.model.IndicatorType.FRACTAL;
import static pro.crypto.model.tick.PriceType.HIGH;
import static pro.crypto.model.tick.PriceType.LOW;

public class Fractal implements Indicator<FractalResult> {

    private final Tick[] originalData;

    private FractalResult[] result;

    public Fractal(IndicatorRequest creationRequest) {
        this.originalData = creationRequest.getOriginalData();
        checkIncomingData();
    }

    @Override
    public IndicatorType getType() {
        return FRACTAL;
    }

    @Override
    public void calculate() {
        result = new FractalResult[originalData.length];
        boolean[] upFractals = calculateFractals(HIGH, this::defineUpFractalPresence);
        boolean[] downFractals = calculateFractals(LOW, this::defineDownFractalPresence);
        buildFractalResult(upFractals, downFractals);
    }

    @Override
    public FractalResult[] getResult() {
        if (isNull(result)) {
            calculate();
        }
        return result;
    }

    private void checkIncomingData() {
        checkOriginalData(originalData);
        checkAdditionalCondition();
    }

    private void checkAdditionalCondition() {
        if (originalData.length < 5) {
            throw new WrongIncomingParametersException(format("In Fractal indicator incoming tick data size should be >= 5 {indicator: {%s}, size: {%d}}", getType().toString(), originalData.length));
        }
    }

    private boolean defineUpFractalPresence(BigDecimal[] highValues, int currentIndex) {
        return isCurrentValueMorePreviousTwo(highValues, currentIndex)
                && isCurrentValueMoreOrEqualsNextTwo(highValues, currentIndex);
    }

    private boolean isCurrentValueMorePreviousTwo(BigDecimal[] highValues, int currentIndex) {
        return highValues[currentIndex].compareTo(highValues[currentIndex - 1]) > 0 && highValues[currentIndex].compareTo(highValues[currentIndex - 2]) > 0;
    }

    private boolean isCurrentValueMoreOrEqualsNextTwo(BigDecimal[] highValues, int currentIndex) {
        return highValues[currentIndex].compareTo(highValues[currentIndex + 1]) >= 0 && highValues[currentIndex].compareTo(highValues[currentIndex + 2]) >= 0;
    }

    private boolean[] calculateFractals(PriceType priceType, BiFunction<BigDecimal[], Integer, Boolean> defineFractalFunction) {
        boolean[] fractals = new boolean[originalData.length];
        BigDecimal[] lowValues = PriceExtractor.extract(originalData, priceType);
        for (int currentIndex = 2; currentIndex < fractals.length; ) {
            if (isPossibleToDefineFractal(currentIndex)) {
                boolean fractal = defineFractalFunction.apply(lowValues, currentIndex);
                fractals[currentIndex] = fractal;
                currentIndex = calculateNextIndex(currentIndex, fractal);
            } else {
                currentIndex++;
            }
        }
        return fractals;
    }

    private boolean isPossibleToDefineFractal(int currentIndex) {
        return currentIndex + 2 < originalData.length;
    }

    private int calculateNextIndex(int currentIndex, boolean fractal) {
        return fractal ? currentIndex + 3 : currentIndex + 1;
    }

    private boolean defineDownFractalPresence(BigDecimal[] highValues, int currentIndex) {
        return isCurrentValueLessPreviousTwo(highValues, currentIndex)
                && isCurrentValueLessOrEqualsNextTwo(highValues, currentIndex);
    }

    private boolean isCurrentValueLessPreviousTwo(BigDecimal[] highValues, int currentIndex) {
        return highValues[currentIndex].compareTo(highValues[currentIndex - 1]) < 0 && highValues[currentIndex].compareTo(highValues[currentIndex - 2]) < 0;
    }

    private boolean isCurrentValueLessOrEqualsNextTwo(BigDecimal[] highValues, int currentIndex) {
        return highValues[currentIndex].compareTo(highValues[currentIndex + 1]) <= 0 && highValues[currentIndex].compareTo(highValues[currentIndex + 2]) <= 0;
    }

    private void buildFractalResult(boolean[] upFractals, boolean[] downFractals) {
        IntStream.range(0, result.length)
                .forEach(idx -> result[idx] = new FractalResult(originalData[idx].getTickTime(), upFractals[idx], downFractals[idx]));
    }

}
