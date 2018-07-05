package pro.crypto.indicator.dpo;

import pro.crypto.helper.IndicatorResultExtractor;
import pro.crypto.helper.MathHelper;
import pro.crypto.indicator.ma.MARequest;
import pro.crypto.indicator.ma.MAResult;
import pro.crypto.indicator.ma.MovingAverageFactory;
import pro.crypto.model.Indicator;
import pro.crypto.model.IndicatorType;
import pro.crypto.model.tick.PriceType;
import pro.crypto.model.tick.Tick;

import java.math.BigDecimal;
import java.util.stream.IntStream;

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
        return IndicatorResultExtractor.extract(calculateMovingAverage());
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
        IntStream.range(0, result.length)
                .forEach(idx -> result[idx] = new DPOResult(
                        originalData[idx].getTickTime(),
                        calculateDetrendedPriceOscillator(
                                movingAverageValues[idx],
                                originalData[idx].getPriceByType(priceType))));
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