package pro.crypto.indicators.moving.average;

import pro.crypto.helper.MathHelper;
import pro.crypto.model.Indicator;
import pro.crypto.model.tick.PriceType;
import pro.crypto.model.result.MovingAverageResult;
import pro.crypto.model.tick.Tick;

import java.math.BigDecimal;

import static java.util.Objects.isNull;

public abstract class MovingAverage implements Indicator<MovingAverageResult> {

    protected MovingAverageResult[] result;
    protected PriceType priceType;

    @Override
    public MovingAverageResult[] getResult() {
        if (isNull(result)) {
            calculate();
        }
        return result;
    }

    void checkIncomingData(Tick[] originalData, int period, PriceType priceType) {
        checkOriginalData(originalData);
        checkOriginalDataSize(originalData, period);
        checkPriceType(priceType);
    }

    void countSimpleAverage(int fromIndex, int currentIndex, int period, Tick[] originalData) {
        BigDecimal simpleAverage = countAndGetSimpleAverage(fromIndex, currentIndex, originalData, period);
        result[currentIndex] = buildMovingAverageResult(currentIndex, originalData, simpleAverage);
    }

    void fillStartPositions(Tick[] originalData, int period) {
        for (int currentIndex = 0; currentIndex < period - 1; currentIndex++) {
            result[currentIndex] = new MovingAverageResult(
                    originalData[currentIndex].getTickTime(),
                    MathHelper.scaleAndRoundValue(originalData[currentIndex].getPriceByType(priceType)),
                    null);
        }
    }

    void initResultArray(int length) {
        result = new MovingAverageResult[length];
    }

    BigDecimal countAndGetSimpleAverage(int fromIndex, int toIndex, Tick[] originalData, int period) {
        BigDecimal periodSum = new BigDecimal(0);
        for (int currentIndex = fromIndex; currentIndex <= toIndex; currentIndex++) {
            periodSum = periodSum.add(originalData[currentIndex].getPriceByType(priceType));
        }
        return MathHelper.scaleAndRoundValue(periodSum.divide(new BigDecimal(period), BigDecimal.ROUND_HALF_UP));
    }

    private MovingAverageResult buildMovingAverageResult(int currentIndex, Tick[] originalData, BigDecimal simpleAverage) {
        return new MovingAverageResult(
                originalData[currentIndex].getTickTime(),
                MathHelper.scaleAndRoundValue(originalData[currentIndex].getPriceByType(priceType)),
                simpleAverage);
    }

}
