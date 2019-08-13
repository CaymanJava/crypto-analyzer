package pro.crypto.indicator.obv;

import pro.crypto.helper.FakeTicksCreator;
import pro.crypto.helper.IndicatorResultExtractor;
import pro.crypto.helper.MathHelper;
import pro.crypto.indicator.ma.MARequest;
import pro.crypto.indicator.ma.MAResult;
import pro.crypto.indicator.ma.MovingAverageFactory;
import pro.crypto.model.Indicator;
import pro.crypto.model.indicator.IndicatorType;
import pro.crypto.model.tick.Tick;
import pro.crypto.request.IndicatorRequest;

import java.math.BigDecimal;
import java.util.stream.IntStream;

import static java.util.Objects.isNull;
import static pro.crypto.model.indicator.IndicatorType.ON_BALANCE_VOLUME;
import static pro.crypto.model.tick.PriceType.CLOSE;

public class OnBalanceVolume implements Indicator<OBVResult> {

    private final Tick[] originalData;
    private final IndicatorType movingAverageType;
    private final int movingAveragePeriod;

    private BigDecimal[] indicatorResults;
    private OBVResult[] result;

    public OnBalanceVolume(IndicatorRequest creationRequest) {
        OBVRequest request = (OBVRequest) creationRequest;
        this.originalData = request.getOriginalData();
        this.movingAverageType = request.getMovingAverageType();
        this.movingAveragePeriod = request.getMovingAveragePeriod();
        checkIncomingData();
    }

    @Override
    public IndicatorType getType() {
        return ON_BALANCE_VOLUME;
    }

    @Override
    public void calculate() {
        calculateOnBalanceVolumesValues();
        BigDecimal[] signalLineValues = calculateSignalLineValues();
        buildOnBalanceVolumeResult(signalLineValues);
    }

    @Override
    public OBVResult[] getResult() {
        if (isNull(result)) {
            calculate();
        }
        return result;
    }

    private void checkIncomingData() {
        checkOriginalData(originalData);
        checkOriginalDataSize(originalData, movingAveragePeriod);
        checkPeriod(movingAveragePeriod);
        checkMovingAverageType(movingAverageType);
    }

    private void calculateOnBalanceVolumesValues() {
        indicatorResults = new BigDecimal[originalData.length];
        indicatorResults[0] = originalData[0].getBaseVolume();
        IntStream.range(1, originalData.length)
                .forEach(idx -> indicatorResults[idx] = calculateOnBalanceVolumeValue(idx));
    }

    private BigDecimal calculateOnBalanceVolumeValue(int currentIndex) {
        int priceComparing = compareCurrentCloseWithPrevious(currentIndex);
        if (priceComparing > 0) {
            return buildRisingPriceResult(currentIndex);
        }
        if (priceComparing < 0) {
            return buildFallingPriceResult(currentIndex);
        }
        return buildSamePriceResult(currentIndex);
    }

    private BigDecimal buildSamePriceResult(int currentIndex) {
        return MathHelper.scaleAndRound(indicatorResults[currentIndex - 1].subtract(new BigDecimal(1)));
    }

    private BigDecimal buildFallingPriceResult(int currentIndex) {
        return MathHelper.scaleAndRound(indicatorResults[currentIndex - 1].subtract(originalData[currentIndex].getBaseVolume()));
    }

    private BigDecimal buildRisingPriceResult(int currentIndex) {
        return MathHelper.scaleAndRound(indicatorResults[currentIndex - 1].add(originalData[currentIndex].getBaseVolume()));
    }

    private int compareCurrentCloseWithPrevious(int currentIndex) {
        return originalData[currentIndex].getClose().compareTo(originalData[currentIndex - 1].getClose());
    }

    private BigDecimal[] calculateSignalLineValues() {
        return IndicatorResultExtractor.extractIndicatorValues(calculateMovingAverageValues());
    }

    private MAResult[] calculateMovingAverageValues() {
        return MovingAverageFactory.create(buildMARequest()).getResult();
    }

    private IndicatorRequest buildMARequest() {
        return MARequest.builder()
                .originalData(FakeTicksCreator.createWithCloseOnly(indicatorResults))
                .priceType(CLOSE)
                .period(movingAveragePeriod)
                .indicatorType(movingAverageType)
                .build();
    }

    private void buildOnBalanceVolumeResult(BigDecimal[] signalLineValues) {
        result = IntStream.range(0, originalData.length)
                .mapToObj(idx -> new OBVResult(originalData[idx].getTickTime(), indicatorResults[idx], signalLineValues[idx]))
                .toArray(OBVResult[]::new);
    }

}
