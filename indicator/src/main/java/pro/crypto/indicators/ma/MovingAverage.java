package pro.crypto.indicators.ma;

import pro.crypto.helper.MathHelper;
import pro.crypto.model.Indicator;
import pro.crypto.model.tick.PriceType;
import pro.crypto.model.result.MAResult;
import pro.crypto.model.tick.Tick;

import java.math.BigDecimal;

import static java.util.Objects.isNull;

public abstract class MovingAverage implements Indicator<MAResult> {

    protected MAResult[] result;
    protected PriceType priceType;

    @Override
    public MAResult[] getResult() {
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
            result[currentIndex] = new MAResult(
                    originalData[currentIndex].getTickTime(),
                    MathHelper.scaleAndRoundValue(originalData[currentIndex].getPriceByType(priceType)),
                    null);
        }
    }

    void initResultArray(int length) {
        result = new MAResult[length];
    }

    BigDecimal countAndGetSimpleAverage(int fromIndex, int toIndex, Tick[] originalData, int period) {
        BigDecimal periodSum = new BigDecimal(0);
        for (int currentIndex = fromIndex; currentIndex <= toIndex; currentIndex++) {
            periodSum = periodSum.add(originalData[currentIndex].getPriceByType(priceType));
        }
        return MathHelper.divide(periodSum, new BigDecimal(period));
    }

    private MAResult buildMovingAverageResult(int currentIndex, Tick[] originalData, BigDecimal simpleAverage) {
        return new MAResult(
                originalData[currentIndex].getTickTime(),
                MathHelper.scaleAndRoundValue(originalData[currentIndex].getPriceByType(priceType)),
                simpleAverage);
    }

}
