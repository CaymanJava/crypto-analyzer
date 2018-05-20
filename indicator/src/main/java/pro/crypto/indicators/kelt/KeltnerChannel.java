package pro.crypto.indicators.kelt;

import pro.crypto.exception.WrongIncomingParametersException;
import pro.crypto.helper.MathHelper;
import pro.crypto.indicators.atr.AverageTrueRange;
import pro.crypto.indicators.ma.MovingAverageFactory;
import pro.crypto.model.Indicator;
import pro.crypto.model.IndicatorType;
import pro.crypto.model.request.ATRRequest;
import pro.crypto.model.request.KELTRequest;
import pro.crypto.model.request.MARequest;
import pro.crypto.model.result.ATRResult;
import pro.crypto.model.result.KELTResult;
import pro.crypto.model.result.MAResult;
import pro.crypto.model.tick.PriceType;
import pro.crypto.model.tick.Tick;

import java.math.BigDecimal;

import static java.lang.String.format;
import static java.util.Locale.ENGLISH;
import static java.util.Objects.isNull;
import static pro.crypto.model.IndicatorType.KELTNER_CHANNEL;

public class KeltnerChannel implements Indicator<KELTResult> {

    private final Tick[] originalData;
    private final PriceType priceType;
    private final IndicatorType movingAverageType;
    private final int movingAveragePeriod;
    private final int averageTrueRangePeriod;
    private final double averageTrueRangeShift;

    private KELTResult[] result;

    public KeltnerChannel(KELTRequest request) {
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
        MAResult[] movingAverageResult = MovingAverageFactory.create(buildMovingAverageRequest()).getResult();
        ATRResult[] averageTrueRangeResult = new AverageTrueRange(buildAverageTrueRangeRequest()).getResult();
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
        checkShift();
        checkPriceType(priceType);
        checkMovingAverageType(movingAverageType);
    }

    private void checkShift() {
        if (averageTrueRangeShift <= 0) {
            throw new WrongIncomingParametersException(format(ENGLISH, "Average true range shift should be more than 0 {indicator: {%s}, shift: {%.2f}}",
                    getType().toString(), averageTrueRangeShift));
        }
    }

    private MARequest buildMovingAverageRequest() {
        return MARequest.builder()
                .originalData(originalData)
                .period(movingAveragePeriod)
                .priceType(priceType)
                .indicatorType(movingAverageType)
                .build();
    }

    private ATRRequest buildAverageTrueRangeRequest() {
        return ATRRequest.builder()
                .originalData(originalData)
                .period(averageTrueRangePeriod)
                .build();
    }

    private void calculateKeltnerChannelValues(MAResult[] movingAverageResult, ATRResult[] averageTrueRangeResult) {
        for (int currentIndex = 0; currentIndex < result.length; currentIndex++) {
            result[currentIndex] = calculateKeltnerChannelValue(
                    movingAverageResult[currentIndex].getIndicatorValue(),
                    averageTrueRangeResult[currentIndex].getIndicatorValue(),
                    currentIndex);
        }
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
                maValue,
                calculateUpperEnvelope(maValue, channelRadius),
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
