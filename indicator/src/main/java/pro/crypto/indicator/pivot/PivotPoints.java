package pro.crypto.indicator.pivot;

import pro.crypto.helper.MathHelper;
import pro.crypto.model.Indicator;
import pro.crypto.model.tick.Tick;

import java.math.BigDecimal;

import static java.util.Objects.isNull;

public abstract class PivotPoints implements Indicator<PivotResult> {

    protected final Tick originalData;
    protected PivotResult[] result;
    BigDecimal pivot;

    PivotPoints(Tick originalData) {
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
        result = new PivotResult[1];
        pivot = calculatePivot();
        BigDecimal firstResistance = calculateFirstResistance();
        BigDecimal secondResistance = calculateSecondResistance();
        BigDecimal thirdResistance = calculateThirdResistance();
        BigDecimal fourthResistance = calculateFourthResistance();
        BigDecimal firstSupport = calculateFirstSupport();
        BigDecimal secondSupport = calculateSecondSupport();
        BigDecimal thirdSupport = calculateThirdSupport();
        BigDecimal fourthSupport = calculateFourthSupport();
        result[0] = new PivotResult(originalData.getTickTime(), pivot,
                firstResistance, secondResistance, thirdResistance, fourthResistance,
                firstSupport, secondSupport, thirdSupport, fourthSupport);
    }

    BigDecimal calculatePivot() {
        return null;
    }

    BigDecimal calculateFirstResistance() {
        return null;
    }

    BigDecimal calculateSecondResistance() {
        return null;
    }

    BigDecimal calculateThirdResistance() {
        return null;
    }

    BigDecimal calculateFourthResistance() {
        return null;
    }

    BigDecimal calculateFirstSupport() {
        return null;
    }

    BigDecimal calculateSecondSupport() {
        return null;
    }

    BigDecimal calculateThirdSupport() {
        return null;
    }

    BigDecimal calculateFourthSupport() {
        return null;
    }

    // (H + L + C) / 3
    BigDecimal calculateDefaultPivot() {
        return MathHelper.divide(originalData.getHigh().add(originalData.getLow()).add(originalData.getClose()), new BigDecimal(3));
    }

}
