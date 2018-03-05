package pro.crypto.moving.average;

import pro.crypto.model.IndicatorType;
import pro.crypto.model.PriceType;
import pro.crypto.model.result.MovingAverageResult;
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
        fillStartPositions(originalData, antiAliasingInterval);
        fillStartIndicatorValue();
        fillAllPositions();
    }

    private BigDecimal countAlphaCoefficient(int antiAliasingInterval) {
        return scaleAndRoundValue(new BigDecimal(2).divide(
                new BigDecimal(antiAliasingInterval).add(new BigDecimal(1))
        ));
    }

    private void fillStartIndicatorValue() {
        countSimpleAverage(0, antiAliasingInterval - 1, antiAliasingInterval, originalData);
    }

    private void fillAllPositions() {
        for (int i = antiAliasingInterval; i < result.length; i++) {
            result[i] = buildMovingAverageResult(i);
        }
    }

    private MovingAverageResult buildMovingAverageResult(int currentIndex) {
        return new MovingAverageResult(
                originalData[currentIndex].getTickTime(),
                scaleAndRoundValue(extractPriceByType(originalData[currentIndex])),
                scaleAndRoundValue(countExponentialAverage(currentIndex)));
    }

    // EMAt = α * Pt + (1 - α) * EMAt-1
    private BigDecimal countExponentialAverage(int currentIndex) {
        return alphaCoefficient.multiply(extractPriceByType(originalData[currentIndex]))
                .add(
                        (new BigDecimal(1).subtract(alphaCoefficient))
                                .multiply(result[currentIndex - 1].getIndicatorValue())
                );
    }

}
