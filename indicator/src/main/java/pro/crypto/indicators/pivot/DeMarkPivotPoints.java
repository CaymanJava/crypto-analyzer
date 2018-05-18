package pro.crypto.indicators.pivot;

import pro.crypto.helper.MathHelper;
import pro.crypto.model.IndicatorType;
import pro.crypto.model.tick.Tick;

import java.math.BigDecimal;

import static java.util.Objects.nonNull;
import static pro.crypto.model.IndicatorType.DE_MARK_PIVOT_POINTS;

public class DeMarkPivotPoints extends PivotPoints {

    DeMarkPivotPoints(Tick originalData) {
        super(originalData);
    }

    @Override
    public IndicatorType getType() {
        return DE_MARK_PIVOT_POINTS;
    }

    @Override
    public void calculate() {
        calculatePivotPoints();
    }

    @Override
    BigDecimal calculatePivot() {
        return calculateAuxiliaryCoefficient();
    }

    // X / 2 - L
    @Override
    BigDecimal calculateFirstResistance() {
        return calculateResistance();
    }

    @Override
    BigDecimal calculateFirstSupport() {
        return calculateSupport();
    }

    private BigDecimal calculateAuxiliaryCoefficient() {
        if (isCloseLessThanOpen()) {
            return calculateCloseLessOpenCoefficient();
        }
        if (isCloseMoreThanOpen()) {
            return calculateCloseMoreOpenCoefficient();
        }
        return calculateCloseEqualsOpenCoefficient();
    }

    private boolean isCloseLessThanOpen() {
        return originalData.getClose().compareTo(originalData.getOpen()) < 0;
    }

    // 2 x L + H + C
    private BigDecimal calculateCloseLessOpenCoefficient() {
        return new BigDecimal(2).multiply(originalData.getLow())
                .add(originalData.getHigh())
                .add(originalData.getClose());
    }

    private boolean isCloseMoreThanOpen() {
        return originalData.getClose().compareTo(originalData.getOpen()) > 0;
    }

    // 2 x H + L + C
    private BigDecimal calculateCloseMoreOpenCoefficient() {
        return new BigDecimal(2).multiply(originalData.getHigh())
                .add(originalData.getLow())
                .add(originalData.getClose());
    }

    // 2 x C + H + L
    private BigDecimal calculateCloseEqualsOpenCoefficient() {
        return new BigDecimal(2).multiply(originalData.getClose())
                .add(originalData.getHigh())
                .add(originalData.getLow());
    }

    private BigDecimal calculateResistance() {
        BigDecimal dividedTwoCoefficient = MathHelper.divide(pivot, new BigDecimal(2));
        return nonNull(dividedTwoCoefficient)
                ? dividedTwoCoefficient.subtract(originalData.getLow())
                : null;
    }

    private BigDecimal calculateSupport() {
        BigDecimal dividedTwoCoefficient = MathHelper.divide(pivot, new BigDecimal(2));
        return nonNull(dividedTwoCoefficient)
                ? dividedTwoCoefficient.subtract(originalData.getHigh())
                : null;
    }

}
