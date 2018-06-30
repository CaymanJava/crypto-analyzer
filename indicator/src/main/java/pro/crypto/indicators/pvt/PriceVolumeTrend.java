package pro.crypto.indicators.pvt;

import pro.crypto.helper.MathHelper;
import pro.crypto.model.Indicator;
import pro.crypto.model.IndicatorType;
import pro.crypto.model.request.PVTRequest;
import pro.crypto.model.result.PVTResult;
import pro.crypto.model.tick.PriceType;
import pro.crypto.model.tick.Tick;

import java.math.BigDecimal;
import java.util.stream.IntStream;

import static java.util.Objects.isNull;
import static pro.crypto.model.IndicatorType.PRICE_VOLUME_TREND;

public class PriceVolumeTrend implements Indicator<PVTResult> {

    private final Tick[] originalData;
    private final PriceType priceType;

    private PVTResult[] result;

    public PriceVolumeTrend(PVTRequest request) {
        this.originalData = request.getOriginalData();
        this.priceType = request.getPriceType();
        checkIncomingData();
    }

    @Override
    public IndicatorType getType() {
        return PRICE_VOLUME_TREND;
    }

    @Override
    public void calculate() {
        result = new PVTResult[originalData.length];
        calculatePriceVolumeTrendResult();
    }

    @Override
    public PVTResult[] getResult() {
        if (isNull(result)) {
            calculate();
        }
        return result;
    }

    private void checkIncomingData() {
        checkOriginalData(originalData);
        checkPriceType(priceType);
    }

    private void calculatePriceVolumeTrendResult() {
        fillInFirstValue();
        fillInRemainValues();
    }

    private void fillInFirstValue() {
        result[0] = new PVTResult(originalData[0].getTickTime(), BigDecimal.ZERO);
    }

    private void fillInRemainValues() {
        IntStream.range(1, originalData.length)
                .forEach(this::calculatePriceVolumeTrend);
    }

    private void calculatePriceVolumeTrend(int currentIndex) {
        result[currentIndex] = new PVTResult(originalData[currentIndex].getTickTime(), calculatePriceVolumeTrendValue(currentIndex));
    }

    // (Price(i) - Price(i - 1)) / Price(i - 1) * Volume + PVT(i - 1)
    private BigDecimal calculatePriceVolumeTrendValue(int currentIndex) {
        BigDecimal ratio = calculatePriceRatio(currentIndex);
        return MathHelper.scaleAndRound(ratio
                .multiply(originalData[currentIndex].getBaseVolume())
                .add(result[currentIndex - 1].getIndicatorValue()));
    }

    private BigDecimal calculatePriceRatio(int currentIndex) {
        return MathHelper.divide(
                originalData[currentIndex].getPriceByType(priceType).subtract(originalData[currentIndex - 1].getPriceByType(priceType)),
                originalData[currentIndex - 1].getPriceByType(priceType));
    }

}
