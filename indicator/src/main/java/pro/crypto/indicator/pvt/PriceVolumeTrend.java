package pro.crypto.indicator.pvt;

import pro.crypto.helper.FakeTicksCreator;
import pro.crypto.helper.IndicatorResultExtractor;
import pro.crypto.helper.MathHelper;
import pro.crypto.indicator.ma.MARequest;
import pro.crypto.indicator.ma.MAResult;
import pro.crypto.indicator.ma.MovingAverageFactory;
import pro.crypto.model.Indicator;
import pro.crypto.model.IndicatorRequest;
import pro.crypto.model.IndicatorType;
import pro.crypto.model.tick.PriceType;
import pro.crypto.model.tick.Tick;

import java.math.BigDecimal;
import java.util.stream.IntStream;

import static java.util.Objects.isNull;
import static pro.crypto.model.IndicatorType.PRICE_VOLUME_TREND;
import static pro.crypto.model.tick.PriceType.CLOSE;

public class PriceVolumeTrend implements Indicator<PVTResult> {

    private final Tick[] originalData;
    private final PriceType priceType;
    private final int movingAveragePeriod;
    private final IndicatorType movingAverageType;

    private BigDecimal[] indicatorValues;
    private PVTResult[] result;

    public PriceVolumeTrend(IndicatorRequest creationRequest) {
        PVTRequest request = (PVTRequest) creationRequest;
        this.originalData = request.getOriginalData();
        this.priceType = request.getPriceType();
        this.movingAveragePeriod = request.getMovingAveragePeriod();
        this.movingAverageType = request.getMovingAverageType();
        checkIncomingData();
    }

    @Override
    public IndicatorType getType() {
        return PRICE_VOLUME_TREND;
    }

    @Override
    public void calculate() {
        calculatePriceVolumeTrend();
        BigDecimal[] signalLineValues = calculateSignalLineValues();
        buildPriceVolumeTrendResult(signalLineValues);
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
        checkOriginalDataSize(originalData, movingAveragePeriod);
        checkPeriod(movingAveragePeriod);
        checkMovingAverageType(movingAverageType);
    }

    private void calculatePriceVolumeTrend() {
        indicatorValues = new BigDecimal[originalData.length];
        indicatorValues[0] = BigDecimal.ZERO;
        IntStream.range(1, originalData.length)
                .forEach(idx -> indicatorValues[idx] = calculatePriceVolumeTrendValue(idx));
    }

    // (Price(i) - Price(i - 1)) / Price(i - 1) * Volume + PVT(i - 1)
    private BigDecimal calculatePriceVolumeTrendValue(int currentIndex) {
        BigDecimal ratio = calculatePriceRatio(currentIndex);
        return MathHelper.scaleAndRound(ratio
                .multiply(originalData[currentIndex].getBaseVolume())
                .add(indicatorValues[currentIndex - 1]));
    }

    private BigDecimal calculatePriceRatio(int currentIndex) {
        return MathHelper.divide(
                originalData[currentIndex].getPriceByType(priceType).subtract(originalData[currentIndex - 1].getPriceByType(priceType)),
                originalData[currentIndex - 1].getPriceByType(priceType));
    }

    private BigDecimal[] calculateSignalLineValues() {
        return IndicatorResultExtractor.extractIndicatorValue(calculateMovingAverage());
    }

    private MAResult[] calculateMovingAverage() {
        return MovingAverageFactory.create(buildMARequest()).getResult();
    }

    private IndicatorRequest buildMARequest() {
        return MARequest.builder()
                .originalData(FakeTicksCreator.createWithCloseOnly(indicatorValues))
                .period(movingAveragePeriod)
                .priceType(CLOSE)
                .indicatorType(movingAverageType)
                .build();
    }

    private void buildPriceVolumeTrendResult(BigDecimal[] signalLineValues) {
        result = IntStream.range(0, originalData.length)
                .mapToObj(idx -> new PVTResult(originalData[idx].getTickTime(), indicatorValues[idx], signalLineValues[idx]))
                .toArray(PVTResult[]::new);
    }

}
