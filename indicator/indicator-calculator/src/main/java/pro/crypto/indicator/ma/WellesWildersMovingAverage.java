package pro.crypto.indicator.ma;

import pro.crypto.helper.MathHelper;
import pro.crypto.model.indicator.IndicatorType;
import pro.crypto.model.tick.PriceType;
import pro.crypto.model.tick.Tick;
import pro.crypto.request.IndicatorRequest;

import java.math.BigDecimal;

import static pro.crypto.model.indicator.IndicatorType.EXPONENTIAL_MOVING_AVERAGE;
import static pro.crypto.model.indicator.IndicatorType.WELLES_WILDERS_MOVING_AVERAGE;

public class WellesWildersMovingAverage extends MovingAverage {

    private final Tick[] originalData;
    private final int period;

    public WellesWildersMovingAverage(Tick[] originalData, int period, PriceType priceType) {
        this.originalData = originalData;
        this.period = period;
        this.priceType = priceType;
        checkIncomingData(originalData, period, priceType);
    }

    @Override
    public IndicatorType getType() {
        return WELLES_WILDERS_MOVING_AVERAGE;
    }

    @Override
    public void calculate() {
        BigDecimal alphaCoefficient = calculateAlphaCoefficient();
        result = calculateExponentialMovingAverage(alphaCoefficient);
    }

    private BigDecimal calculateAlphaCoefficient() {
        return MathHelper.divide(BigDecimal.ONE, new BigDecimal(period));
    }

    private MAResult[] calculateExponentialMovingAverage(BigDecimal alphaCoefficient) {
        return MovingAverageFactory.create(buildEMARequest(alphaCoefficient)).getResult();
    }

    private IndicatorRequest buildEMARequest(BigDecimal alphaCoefficient) {
        return MARequest.builder()
                .originalData(originalData)
                .priceType(priceType)
                .period(period)
                .alphaCoefficient(alphaCoefficient)
                .indicatorType(EXPONENTIAL_MOVING_AVERAGE)
                .build();
    }

}
