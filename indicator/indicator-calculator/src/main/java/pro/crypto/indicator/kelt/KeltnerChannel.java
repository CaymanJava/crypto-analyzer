package pro.crypto.indicator.kelt;

import pro.crypto.helper.MathHelper;
import pro.crypto.indicator.atr.ATRRequest;
import pro.crypto.indicator.atr.ATRResult;
import pro.crypto.indicator.atr.AverageTrueRange;
import pro.crypto.indicator.ma.MARequest;
import pro.crypto.indicator.ma.MAResult;
import pro.crypto.indicator.ma.MovingAverageFactory;
import pro.crypto.model.Indicator;
import pro.crypto.model.indicator.IndicatorType;
import pro.crypto.model.tick.PriceType;
import pro.crypto.model.tick.Tick;
import pro.crypto.request.IndicatorRequest;

import java.math.BigDecimal;
import java.util.stream.IntStream;

import static java.util.Objects.isNull;
import static pro.crypto.model.indicator.IndicatorType.KELTNER_CHANNEL;

public class KeltnerChannel implements Indicator<KELTResult> {

    private final Tick[] originalData;
    private final PriceType priceType;
    private final IndicatorType movingAverageType;
    private final int movingAveragePeriod;
    private final int averageTrueRangePeriod;
    private final double averageTrueRangeShift;

    private KELTResult[] result;

    public KeltnerChannel(IndicatorRequest creationRequest) {
        KELTRequest request = (KELTRequest) creationRequest;
        this.originalData = request.getOriginalData();
        this.priceType = request.getPriceType();
        this.movingAverageType = request.getMovingAverageType();
        this.movingAveragePeriod = request.getMovingAveragePeriod();
        this.averageTrueRangePeriod = request.getAverageTrueRangePeriod();
        this.averageTrueRangeShift = request.getAverageTrueRangeShift();
        checkIncomingData();
    }

    @Override
    public IndicatorType getType() {
        return KELTNER_CHANNEL;
    }

    @Override
    public void calculate() {
        result = new KELTResult[originalData.length];
        MAResult[] movingAverageResult = MovingAverageFactory.create(buildMARequest()).getResult();
        ATRResult[] averageTrueRangeResult = new AverageTrueRange(buildATRRequest()).getResult();
        calculateKeltnerChannelValues(movingAverageResult, averageTrueRangeResult);
    }

    @Override
    public KELTResult[] getResult() {
        if (isNull(result)) {
            calculate();
        }
        return result;
    }

    private void checkIncomingData() {
        checkOriginalData(originalData);
        checkOriginalDataSize(originalData, movingAveragePeriod);
        checkOriginalDataSize(originalData, averageTrueRangePeriod);
        checkPeriod(movingAveragePeriod);
        checkPeriod(averageTrueRangePeriod);
        checkShift(averageTrueRangeShift);
        checkPriceType(priceType);
        checkMovingAverageType(movingAverageType);
    }

    private IndicatorRequest buildMARequest() {
        return MARequest.builder()
                .originalData(originalData)
                .period(movingAveragePeriod)
                .priceType(priceType)
                .indicatorType(movingAverageType)
                .build();
    }

    private IndicatorRequest buildATRRequest() {
        return ATRRequest.builder()
                .originalData(originalData)
                .period(averageTrueRangePeriod)
                .build();
    }

    private void calculateKeltnerChannelValues(MAResult[] movingAverageResult, ATRResult[] averageTrueRangeResult) {
        IntStream.range(0, result.length)
                .forEach(idx -> result[idx] = calculateKeltnerChannelValue(movingAverageResult[idx].getIndicatorValue(),
                        averageTrueRangeResult[idx].getIndicatorValue(), idx));
    }

    private KELTResult calculateKeltnerChannelValue(BigDecimal maValue, BigDecimal atrValue, int currentIndex) {
        return isNull(maValue) || isNull(atrValue)
                ? createEmptyKELTResult(currentIndex)
                : calculateKeltnerChannel(maValue, atrValue, currentIndex);
    }

    private KELTResult calculateKeltnerChannel(BigDecimal maValue, BigDecimal atrValue, int currentIndex) {
        BigDecimal channelRadius = calculateChannelRadius(atrValue);
        return new KELTResult(
                originalData[currentIndex].getTickTime(),
                calculateUpperEnvelope(maValue, channelRadius),
                maValue,
                calculateLowerEnvelope(maValue, channelRadius));
    }

    private BigDecimal calculateChannelRadius(BigDecimal atrValue) {
        return atrValue.multiply(new BigDecimal(averageTrueRangeShift));
    }

    private KELTResult createEmptyKELTResult(int currentIndex) {
        return new KELTResult(originalData[currentIndex].getTickTime(), null, null, null);
    }

    private BigDecimal calculateUpperEnvelope(BigDecimal maValue, BigDecimal channelRadius) {
        return MathHelper.sum(maValue, channelRadius);
    }

    private BigDecimal calculateLowerEnvelope(BigDecimal maValue, BigDecimal channelRadius) {
        return maValue.subtract(channelRadius);
    }

}
