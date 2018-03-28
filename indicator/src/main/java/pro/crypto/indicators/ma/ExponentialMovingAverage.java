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
    private final int antiAliasingInterval;
    private final BigDecimal alphaCoefficient;

    ExponentialMovingAverage(Tick[] originalData, int antiAliasingInterval, PriceType priceType, BigDecimal alphaCoefficient) {
        checkIncomingData(originalData, antiAliasingInterval, priceType);
        this.originalData = originalData;
        this.antiAliasingInterval = antiAliasingInterval;
        this.priceType = priceType;
        this.alphaCoefficient = isNull(alphaCoefficient) ? countAlphaCoefficient(antiAliasingInterval) : alphaCoefficient;
    }

    @Override
    public IndicatorType getType() {
        return EXPONENTIAL_MOVING_AVERAGE;
    }

    @Override
    public void calculate() {
        initResultArray(originalData.length);
        fillInInitialPositions(originalData, antiAliasingInterval);
        fillInInitialIndicatorValue();
        fillInRemainPositions();
    }

    private void fillInInitialIndicatorValue() {
        countSimpleAverage(0, antiAliasingInterval - 1, antiAliasingInterval, originalData);
    }

    private void fillInRemainPositions() {
        for (int i = antiAliasingInterval; i < result.length; i++) {
            result[i] = buildMovingAverageResult(i);
        }
    }

    private MAResult buildMovingAverageResult(int currentIndex) {
        return new MAResult(
                originalData[currentIndex].getTickTime(),
                MathHelper.scaleAndRound(originalData[currentIndex].getPriceByType(priceType)),
                MathHelper.scaleAndRound(countExponentialAverage(currentIndex)));
    }

    // EMAt = α * Pt + (1 - α) * EMAt-1
    private BigDecimal countExponentialAverage(int currentIndex) {
        return alphaCoefficient.multiply(originalData[currentIndex].getPriceByType(priceType))
                .add(
                        (new BigDecimal(1).subtract(alphaCoefficient))
                                .multiply(result[currentIndex - 1].getIndicatorValue())
                );
    }

    // α = 2 / (N + 1)
    private BigDecimal countAlphaCoefficient(int antiAliasingInterval) {
        return MathHelper.divide(new BigDecimal(2), new BigDecimal(antiAliasingInterval).add(new BigDecimal(1)));
    }

}
