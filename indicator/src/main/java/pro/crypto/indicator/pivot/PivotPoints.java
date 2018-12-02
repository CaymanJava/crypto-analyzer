package pro.crypto.indicator.pivot;

import pro.crypto.helper.MathHelper;
import pro.crypto.model.Indicator;
import pro.crypto.model.tick.Tick;

import java.math.BigDecimal;
import java.util.stream.IntStream;

import static java.util.Objects.isNull;

public abstract class PivotPoints implements Indicator<PivotResult> {

    protected final Tick[] originalData;
    protected PivotResult[] result;

    BigDecimal pivot;

    PivotPoints(Tick[] originalData) {
        this.originalData = originalData;
        checkOriginalData(this.originalData);
    }

    @Override
    public PivotResult[] getResult() {
        if (isNull(result)) {
            calculate();
        }
        return result;
    }

    void calculatePivotPoints() {
        result = new PivotResult[originalData.length];
        fillInFirstValue();
        IntStream.range(1, result.length)
                .forEach(this::calculatePivotPoint);
    }

    abstract BigDecimal calculatePivot(int currentIndex);

    abstract BigDecimal calculateFirstResistance(int currentIndex);

    abstract BigDecimal calculateSecondResistance(int currentIndex);

    abstract BigDecimal calculateThirdResistance(int currentIndex);

    abstract BigDecimal calculateFourthResistance(int currentIndex);

    abstract BigDecimal calculateFirstSupport(int currentIndex);

    abstract BigDecimal calculateSecondSupport(int currentIndex);

    abstract BigDecimal calculateThirdSupport(int currentIndex);

    abstract BigDecimal calculateFourthSupport(int currentIndex);

    // (H + L + C) / 3
    BigDecimal calculateDefaultPivot(int currentIndex) {
        return MathHelper.average(originalData[currentIndex - 1].getHigh(), originalData[currentIndex - 1].getLow(), originalData[currentIndex - 1].getClose());
    }

    BigDecimal empty() {
        return null;
    }

    private void fillInFirstValue() {
        result[0] = new PivotResult(originalData[0].getTickTime());
    }

    private void calculatePivotPoint(int currentIndex) {
        pivot = calculatePivot(currentIndex);
        BigDecimal firstResistance = calculateFirstResistance(currentIndex);
        BigDecimal secondResistance = calculateSecondResistance(currentIndex);
        BigDecimal thirdResistance = calculateThirdResistance(currentIndex);
        BigDecimal fourthResistance = calculateFourthResistance(currentIndex);
        BigDecimal firstSupport = calculateFirstSupport(currentIndex);
        BigDecimal secondSupport = calculateSecondSupport(currentIndex);
        BigDecimal thirdSupport = calculateThirdSupport(currentIndex);
        BigDecimal fourthSupport = calculateFourthSupport(currentIndex);
        result[currentIndex] = new PivotResult(originalData[currentIndex].getTickTime(), pivot,
                firstResistance, secondResistance, thirdResistance, fourthResistance,
                firstSupport, secondSupport, thirdSupport, fourthSupport);
    }

}
