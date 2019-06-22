package pro.crypto.indicator.pivot;

import pro.crypto.helper.MathHelper;
import pro.crypto.model.IndicatorType;
import pro.crypto.model.tick.Tick;

import java.math.BigDecimal;

import static java.util.Objects.nonNull;
import static pro.crypto.model.IndicatorType.DE_MARK_PIVOT_POINTS;

public class DeMarkPivotPoints extends PivotPoints {

    DeMarkPivotPoints(Tick[] oneDayData, Tick[] resultTickData) {
        super(oneDayData, resultTickData);
    }

    @Override
    public IndicatorType getType() {
        return DE_MARK_PIVOT_POINTS;
    }

    @Override
    BigDecimal calculatePivot(int currentIndex) {
        return calculateAuxiliaryCoefficient(currentIndex);
    }

    // X / 2 - L
    @Override
    BigDecimal calculateFirstResistance(int currentIndex) {
        return calculateResistance(currentIndex);
    }

    @Override
    BigDecimal calculateSecondResistance(int currentIndex) {
        return empty();
    }

    @Override
    BigDecimal calculateThirdResistance(int currentIndex) {
        return empty();
    }

    @Override
    BigDecimal calculateFourthResistance(int currentIndex) {
        return empty();
    }

    @Override
    BigDecimal calculateFirstSupport(int currentIndex) {
        return calculateSupport(currentIndex);
    }

    @Override
    BigDecimal calculateSecondSupport(int currentIndex) {
        return empty();
    }

    @Override
    BigDecimal calculateThirdSupport(int currentIndex) {
        return empty();
    }

    @Override
    BigDecimal calculateFourthSupport(int currentIndex) {
        return empty();
    }

    @Override
    BigDecimal calculateDefaultPivot(int currentIndex) {
        return super.calculateDefaultPivot(currentIndex);
    }

    private BigDecimal calculateAuxiliaryCoefficient(int currentIndex) {
        if (isCloseLessThanOpen(currentIndex)) {
            return calculateCloseLessOpenCoefficient(currentIndex);
        }
        if (isCloseMoreThanOpen(currentIndex)) {
            return calculateCloseMoreOpenCoefficient(currentIndex);
        }
        return calculateCloseEqualsOpenCoefficient(currentIndex);
    }

    private boolean isCloseLessThanOpen(int currentIndex) {
        return originalData[currentIndex - 1].getClose()
                .compareTo(originalData[currentIndex - 1].getOpen()) < 0;
    }

    // 2 x L + H + C
    private BigDecimal calculateCloseLessOpenCoefficient(int currentIndex) {
        return new BigDecimal(2).multiply(originalData[currentIndex - 1].getLow())
                .add(originalData[currentIndex - 1].getHigh())
                .add(originalData[currentIndex - 1].getClose());
    }

    private boolean isCloseMoreThanOpen(int currentIndex) {
        return originalData[currentIndex - 1].getClose().compareTo(originalData[currentIndex - 1].getOpen()) > 0;
    }

    // 2 x H + L + C
    private BigDecimal calculateCloseMoreOpenCoefficient(int currentIndex) {
        return new BigDecimal(2).multiply(originalData[currentIndex - 1].getHigh())
                .add(originalData[currentIndex - 1].getLow())
                .add(originalData[currentIndex - 1].getClose());
    }

    // 2 x C + H + L
    private BigDecimal calculateCloseEqualsOpenCoefficient(int currentIndex) {
        return new BigDecimal(2).multiply(originalData[currentIndex - 1].getClose())
                .add(originalData[currentIndex - 1].getHigh())
                .add(originalData[currentIndex - 1].getLow());
    }

    private BigDecimal calculateResistance(int currentIndex) {
        BigDecimal dividedTwoCoefficient = MathHelper.divide(pivot, new BigDecimal(2));
        return nonNull(dividedTwoCoefficient)
                ? dividedTwoCoefficient.subtract(originalData[currentIndex - 1].getLow())
                : null;
    }

    private BigDecimal calculateSupport(int currentIndex) {
        BigDecimal dividedTwoCoefficient = MathHelper.divide(pivot, new BigDecimal(2));
        return nonNull(dividedTwoCoefficient)
                ? dividedTwoCoefficient.subtract(originalData[currentIndex - 1].getHigh())
                : null;
    }

}
