package pro.crypto.indicators.ma;

import pro.crypto.helper.MathHelper;
import pro.crypto.model.IndicatorType;
import pro.crypto.model.result.MAResult;
import pro.crypto.model.tick.PriceType;
import pro.crypto.model.tick.Tick;

import java.math.BigDecimal;

import static java.util.Objects.isNull;
import static pro.crypto.model.IndicatorType.EXPONENTIAL_MOVING_AVERAGE;

public class ExponentialMovingAverage extends MovingAverage {

    private final Tick[] originalData;
    private final int period;
    private final BigDecimal alphaCoefficient;

    ExponentialMovingAverage(Tick[] originalData, int period, PriceType priceType, BigDecimal alphaCoefficient) {
        checkIncomingData(originalData, period, priceType);
        this.originalData = originalData;
        this.period = period;
        this.priceType = priceType;
        this.alphaCoefficient = isNull(alphaCoefficient) ? calculateAlphaCoefficient(period) : alphaCoefficient;
    }

    @Override
    public IndicatorType getType() {
        return EXPONENTIAL_MOVING_AVERAGE;
    }

    @Override
    public void calculate() {
        initResultArray(originalData.length);
        fillInInitialPositions(originalData, period);
        fillInInitialIndicatorValue();
        fillInRemainPositions();
    }

    // α = 2 / (N + 1)
    private BigDecimal calculateAlphaCoefficient(int antiAliasingInterval) {
        return MathHelper.divide(new BigDecimal(2), new BigDecimal(antiAliasingInterval).add(new BigDecimal(1)));
    }

    private void fillInInitialIndicatorValue() {
        calculateSimpleAverage(0, period - 1, originalData);
    }

    private void fillInRemainPositions() {
        for (int currentIndex = period; currentIndex < result.length; currentIndex++) {
            result[currentIndex] = buildMovingAverageResult(currentIndex);
        }
    }

    private MAResult buildMovingAverageResult(int currentIndex) {
        return new MAResult(
                originalData[currentIndex].getTickTime(),
                MathHelper.scaleAndRound(calculateExponentialAverage(originalData, currentIndex, alphaCoefficient)));
    }

}
