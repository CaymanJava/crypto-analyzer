package pro.crypto.indicator.vi;

import pro.crypto.helper.FakeTicksCreator;
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
import static pro.crypto.model.tick.PriceType.CLOSE;

public abstract class VolumeIndex implements Indicator<VIResult> {

    final Tick[] originalData;
    final int period;
    final IndicatorType movingAverageType;
    final PriceType priceType;

    VIResult[] result;

    VolumeIndex(IndicatorRequest creationRequest) {
        VIRequest request = (VIRequest) creationRequest;
        this.originalData = request.getOriginalData();
        this.period = request.getPeriod();
        this.movingAverageType = request.getMovingAverageType();
        this.priceType = request.getPriceType();
        checkIncomingData();
    }

    @Override
    public abstract IndicatorType getType();

    @Override
    public void calculate() {
        result = new VIResult[originalData.length];
        BigDecimal[] volumeIndexes = calculateVolumeIndexes();
        BigDecimal[] movingAverageValues = calculateMovingAverageValues(volumeIndexes);
        buildNegativeVolumeIndexResult(volumeIndexes, movingAverageValues);
    }

    @Override
    public VIResult[] getResult() {
        if (isNull(result)) {
            calculate();
        }
        return result;
    }

    BigDecimal[] calculateVolumeIndexes() {
        BigDecimal[] negativeVolumeIndexes = new BigDecimal[originalData.length];
        negativeVolumeIndexes[0] = originalData[0].getBaseVolume();
        IntStream.range(1, negativeVolumeIndexes.length)
                .forEach(idx -> negativeVolumeIndexes[idx] = calculateVolumeIndexValue(negativeVolumeIndexes, idx));

        return negativeVolumeIndexes;
    }

    abstract BigDecimal calculateVolumeIndexValue(BigDecimal[] negativeVolumeIndexes, int currentIndex);

    boolean isCurrentVolumeMoreThanPrevious(int currentIndex) {
        return originalData[currentIndex].getBaseVolume().compareTo(originalData[currentIndex - 1].getBaseVolume()) > 0;
    }

    BigDecimal calculateVolumeIndexValue(BigDecimal previousNegativeVolumeIndex, int currentIndex) {
        return previousNegativeVolumeIndex.multiply(calculatePriceRatio(currentIndex));
    }

    private BigDecimal[] calculateMovingAverageValues(BigDecimal[] negativeVolumeIndexes) {
        return IndicatorResultExtractor.extractIndicatorValue(calculateMovingAverage(negativeVolumeIndexes));
    }

    private void buildNegativeVolumeIndexResult(BigDecimal[] negativeVolumeIndexes, BigDecimal[] movingAverageValues) {
        IntStream.range(0, result.length)
                .forEach(idx -> result[idx] = new VIResult(
                        originalData[idx].getTickTime(),
                        MathHelper.scaleAndRound(negativeVolumeIndexes[idx]),
                        movingAverageValues[idx]));
    }

    private void checkIncomingData() {
        checkMovingAverageType(movingAverageType);
        checkOriginalData(originalData);
        checkOriginalDataSize(originalData, period);
        checkPeriod(period);
        checkPriceType(priceType);
    }

    private BigDecimal calculatePriceRatio(int currentIndex) {
        return MathHelper.divide(originalData[currentIndex].getPriceByType(priceType), originalData[currentIndex - 1].getPriceByType(priceType));
    }

    private SimpleIndicatorResult[] calculateMovingAverage(BigDecimal[] negativeVolumeIndexes) {
        return MovingAverageFactory.create(buildMARequest(negativeVolumeIndexes)).getResult();
    }

    private IndicatorRequest buildMARequest(BigDecimal[] negativeVolumeIndexes) {
        return MARequest.builder()
                .originalData(FakeTicksCreator.createWithCloseOnly(negativeVolumeIndexes))
                .priceType(CLOSE)
                .period(period)
                .indicatorType(movingAverageType)
                .build();
    }

}
