package pro.crypto.indicators.nvi;

import pro.crypto.helper.FakeTicksCreator;
import pro.crypto.helper.IndicatorResultExtractor;
import pro.crypto.helper.MathHelper;
import pro.crypto.indicators.ma.MovingAverageFactory;
import pro.crypto.model.Indicator;
import pro.crypto.model.IndicatorType;
import pro.crypto.model.request.MARequest;
import pro.crypto.model.request.NVIRequest;
import pro.crypto.model.result.MAResult;
import pro.crypto.model.result.NVIResult;
import pro.crypto.model.tick.PriceType;
import pro.crypto.model.tick.Tick;

import java.math.BigDecimal;

import static java.util.Objects.isNull;
import static pro.crypto.model.IndicatorType.NEGATIVE_VOLUME_INDEX;
import static pro.crypto.model.tick.PriceType.CLOSE;

public class NegativeVolumeIndex implements Indicator<NVIResult> {

    private final Tick[] originalData;
    private final int period;
    private final IndicatorType movingAverageType;
    private final PriceType priceType;

    private NVIResult[] result;

    public NegativeVolumeIndex(NVIRequest request) {
        this.originalData = request.getOriginalData();
        this.period = request.getPeriod();
        this.movingAverageType = request.getMovingAverageType();
        this.priceType = request.getPriceType();
        checkIncomingData();
    }

    @Override
    public IndicatorType getType() {
        return NEGATIVE_VOLUME_INDEX;
    }

    @Override
    public void calculate() {
        result = new NVIResult[originalData.length];
        BigDecimal[] negativeVolumeIndexes = calculateNegativeVolumeIndexes();
        BigDecimal[] movingAverageValues = calculateMovingAverageValues(negativeVolumeIndexes);
        buildNegativeVolumeIndexResult(negativeVolumeIndexes, movingAverageValues);
    }

    @Override
    public NVIResult[] getResult() {
        if (isNull(result)) {
            calculate();
        }
        return result;
    }

    private void checkIncomingData() {
        checkMovingAverageType(movingAverageType);
        checkOriginalData(originalData);
        checkOriginalDataSize(originalData, period);
        checkPeriod(period);
        checkPriceType(priceType);
    }

    private BigDecimal[] calculateNegativeVolumeIndexes() {
        BigDecimal[] negativeVolumeIndexes = new BigDecimal[originalData.length];
        negativeVolumeIndexes[0] = originalData[0].getBaseVolume();
        for (int currentIndex = 1; currentIndex < negativeVolumeIndexes.length; currentIndex++) {
            negativeVolumeIndexes[currentIndex] = calculateNegativeVolumeIndex(negativeVolumeIndexes, currentIndex);
        }
        return negativeVolumeIndexes;
    }

    private BigDecimal calculateNegativeVolumeIndex(BigDecimal[] negativeVolumeIndexes, int currentIndex) {
        return isCurrentVolumeMoreThanPrevious(currentIndex)
                ? negativeVolumeIndexes[currentIndex - 1]
                : calculateNegativeVolumeIndexValue(negativeVolumeIndexes[currentIndex - 1], currentIndex);
    }

    private boolean isCurrentVolumeMoreThanPrevious(int currentIndex) {
        return originalData[currentIndex].getBaseVolume().compareTo(originalData[currentIndex - 1].getBaseVolume()) > 0;
    }

    private BigDecimal calculateNegativeVolumeIndexValue(BigDecimal previousNegativeVolumeIndex, int currentIndex) {
        return previousNegativeVolumeIndex.multiply(calculatePriceRatio(currentIndex));
    }

    private BigDecimal calculatePriceRatio(int currentIndex) {
        return MathHelper.divide(originalData[currentIndex].getPriceByType(priceType), originalData[currentIndex - 1].getPriceByType(priceType));
    }

    private BigDecimal[] calculateMovingAverageValues(BigDecimal[] negativeVolumeIndexes) {
        return IndicatorResultExtractor.extract(calculateMovingAverage(negativeVolumeIndexes));
    }

    private MAResult[] calculateMovingAverage(BigDecimal[] negativeVolumeIndexes) {
        return MovingAverageFactory.create(buildMARequest(negativeVolumeIndexes)).getResult();
    }

    private MARequest buildMARequest(BigDecimal[] negativeVolumeIndexes) {
        return MARequest.builder()
                .originalData(FakeTicksCreator.createWithCloseOnly(negativeVolumeIndexes))
                .priceType(CLOSE)
                .period(period)
                .indicatorType(movingAverageType)
                .build();
    }

    private void buildNegativeVolumeIndexResult(BigDecimal[] negativeVolumeIndexes, BigDecimal[] movingAverageValues) {
        for (int currentIndex = 0; currentIndex < result.length; currentIndex++) {
            result[currentIndex] = new NVIResult(
                    originalData[currentIndex].getTickTime(),
                    MathHelper.scaleAndRound(negativeVolumeIndexes[currentIndex]),
                    movingAverageValues[currentIndex]);
        }
    }

}
