package pro.crypto.indicator.dpo;

import pro.crypto.helper.IndicatorResultExtractor;
import pro.crypto.helper.MathHelper;
import pro.crypto.indicator.ma.MARequest;
import pro.crypto.indicator.ma.MovingAverageFactory;
import pro.crypto.model.Indicator;
import pro.crypto.model.IndicatorRequest;
import pro.crypto.model.IndicatorType;
import pro.crypto.model.SimpleIndicatorResult;
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

    public DetrendedPriceOscillator(IndicatorRequest creationRequest) {
        DPORequest request = (DPORequest) creationRequest;
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
        return IndicatorResultExtractor.extractIndicatorValues(calculateMovingAverage());
    }

    private SimpleIndicatorResult[] calculateMovingAverage() {
        return MovingAverageFactory.create(buildMARequest())
                .getResult();
    }

    private IndicatorRequest buildMARequest() {
        return MARequest.builder()
                .originalData(originalData)
                .period(period)
                .indicatorType(movingAverageType)
                .priceType(priceType)
                .build();
    }

    private void buildDetrendedPriceOscillatorResult(BigDecimal[] movingAverageValues) {
        IntStream.range(0, result.length)
                .forEach(idx -> result[idx] = buildDPOResult(movingAverageValues, idx));
    }

    private DPOResult buildDPOResult(BigDecimal[] movingAverageValues, int currentIndex) {
        return new DPOResult(
                originalData[currentIndex].getTickTime(),
                calculateDetrendedPriceOscillator(movingAverageValues, originalData[currentIndex].getPriceByType(priceType), currentIndex));
    }

    private BigDecimal calculateDetrendedPriceOscillator(BigDecimal[] movingAverageValues, BigDecimal priceByType, int currentIndex) {
        int maIndex = currentIndex - (period / 2 + 1);
        return maIndex > 0 && nonNull(movingAverageValues[maIndex])
                ? calculateDetrendedPriceOscillatorValue(movingAverageValues[maIndex], priceByType)
                : null;
    }

    private BigDecimal calculateDetrendedPriceOscillatorValue(BigDecimal movingAverageValue, BigDecimal price) {
        return MathHelper.scaleAndRound(price.subtract(movingAverageValue));
    }

}
