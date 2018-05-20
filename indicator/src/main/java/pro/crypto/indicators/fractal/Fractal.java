package pro.crypto.indicators.fractal;

import pro.crypto.exception.WrongIncomingParametersException;
import pro.crypto.helper.PriceExtractor;
import pro.crypto.model.Indicator;
import pro.crypto.model.IndicatorType;
import pro.crypto.model.request.FractalRequest;
import pro.crypto.model.result.FractalResult;
import pro.crypto.model.tick.Tick;

import java.math.BigDecimal;

import static java.lang.String.format;
import static java.util.Objects.isNull;
import static pro.crypto.model.IndicatorType.FRACTAL;
import static pro.crypto.model.tick.PriceType.HIGH;
import static pro.crypto.model.tick.PriceType.LOW;

public class Fractal implements Indicator<FractalResult> {

    private final Tick[] originalData;

    private FractalResult[] result;

    public Fractal(FractalRequest request) {
        this.originalData = request.getOriginalData();
        checkIncomingData();
    }

    @Override
    public IndicatorType getType() {
        return FRACTAL;
    }

    @Override
    public void calculate() {
        result = new FractalResult[originalData.length];
        boolean[] upFractals = calculateUpFractals();
        boolean[] downFractals = calculateDownFractals();
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

    private boolean[] calculateUpFractals() {
        boolean[] upFractals = new boolean[originalData.length];
        BigDecimal[] highValues = PriceExtractor.extractValuesByType(originalData, HIGH);
        for (int currentIndex = 2; currentIndex < upFractals.length; ) {
            if (isPossibleToDefineFractal(currentIndex)) {
                boolean upFractal = defineUpFractalPresence(highValues, currentIndex);
                upFractals[currentIndex] = upFractal;
                currentIndex = calculateNextIndex(currentIndex, upFractal);
            } else {
                currentIndex++;
            }
        }
        return upFractals;
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

    private boolean[] calculateDownFractals() {
        boolean[] downFractals = new boolean[originalData.length];
        BigDecimal[] lowValues = PriceExtractor.extractValuesByType(originalData, LOW);
        for (int currentIndex = 2; currentIndex < downFractals.length;) {
            if (isPossibleToDefineFractal(currentIndex)) {
                boolean downFractal = defineDownFractalPresence(lowValues, currentIndex);
                downFractals[currentIndex] = downFractal;
                currentIndex = calculateNextIndex(currentIndex, downFractal);
            } else {
                currentIndex++;
            }
        }
        return downFractals;
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
        for (int currentIndex = 0; currentIndex < result.length; currentIndex++) {
            result[currentIndex] = new FractalResult(
                    originalData[currentIndex].getTickTime(),
                    upFractals[currentIndex],
                    downFractals[currentIndex]);
        }
    }

}
