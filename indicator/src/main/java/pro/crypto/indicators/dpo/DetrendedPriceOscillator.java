package pro.crypto.indicators.dpo;

import pro.crypto.helper.MAResultExtractor;
import pro.crypto.helper.MathHelper;
import pro.crypto.indicators.ma.MovingAverageFactory;
import pro.crypto.model.Indicator;
import pro.crypto.model.IndicatorType;
import pro.crypto.model.request.DPORequest;
import pro.crypto.model.request.MARequest;
import pro.crypto.model.result.DPOResult;
import pro.crypto.model.result.MAResult;
import pro.crypto.model.tick.PriceType;
import pro.crypto.model.tick.Tick;

import java.math.BigDecimal;

import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;
import static pro.crypto.model.IndicatorType.DETRENDED_PRICE_OSCILLATOR;

public class DetrendedPriceOscillator implements Indicator<DPOResult> {

    private final Tick[] originalData;
    private final int period;
    private final IndicatorType movingAverageType;
    private final PriceType priceType;

    private DPOResult[] result;

    public DetrendedPriceOscillator(DPORequest request) {
        this.originalData = request.getOriginalData();
        this.period = request.getPeriod();
        this.movingAverageType = request.getMovingAverageType();
        this.priceType = request.getPriceType();
        checkIncomingData();
    }

    @Override
    public IndicatorType getType() {
        return DETRENDED_PRICE_OSCILLATOR;
    }

    @Override
    public void calculate() {
        result = new DPOResult[originalData.length];
        BigDecimal[] movingAverageValues = calculateMovingAverageValues();
        buildDetrendedPriceOscillatorResult(movingAverageValues);
    }

    @Override
    public DPOResult[] getResult() {
        if (isNull(result)) {
            calculate();
        }
        return result;
    }

    private void checkIncomingData() {
        checkOriginalData(originalData);
        checkOriginalDataSize(originalData, period);
        checkPeriod(period);
        checkMovingAverageType(movingAverageType);
        checkPriceType(priceType);
    }

    private BigDecimal[] calculateMovingAverageValues() {
        return MAResultExtractor.extract(calculateMovingAverage());
    }

    private MAResult[] calculateMovingAverage() {
        return MovingAverageFactory.create(buildMARequest())
                .getResult();
    }

    private MARequest buildMARequest() {
        return MARequest.builder()
                .originalData(originalData)
                .period(period)
                .indicatorType(movingAverageType)
                .priceType(priceType)
                .build();
    }

    private void buildDetrendedPriceOscillatorResult(BigDecimal[] movingAverageValues) {
        for (int i = 0; i < result.length; i++) {
            result[i] = new DPOResult(
                    originalData[i].getTickTime(),
                    calculateDetrendedPriceOscillator(
                            movingAverageValues[i],
                            originalData[i].getPriceByType(priceType)));
        }
    }

    private BigDecimal calculateDetrendedPriceOscillator(BigDecimal movingAverageValue, BigDecimal priceByType) {
        return nonNull(movingAverageValue)
                ? calculateDetrendedPriceOscillatorValue(movingAverageValue, priceByType)
                : null;
    }

    private BigDecimal calculateDetrendedPriceOscillatorValue(BigDecimal movingAverageValue, BigDecimal price) {
        return MathHelper.scaleAndRound(price.subtract(movingAverageValue));
    }

}
