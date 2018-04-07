package pro.crypto.indicators.cci;

import pro.crypto.helper.FakeTicksCreator;
import pro.crypto.helper.MathHelper;
import pro.crypto.helper.TypicalPriceCalculator;
import pro.crypto.indicators.ma.MovingAverageFactory;
import pro.crypto.model.Indicator;
import pro.crypto.model.IndicatorType;
import pro.crypto.model.request.CCIRequest;
import pro.crypto.model.request.MARequest;
import pro.crypto.model.result.CCIResult;
import pro.crypto.model.result.MAResult;
import pro.crypto.model.tick.Tick;

import java.math.BigDecimal;
import java.util.stream.Stream;

import static java.util.Objects.isNull;
import static pro.crypto.model.IndicatorType.COMMODITY_CHANNEL_INDEX;
import static pro.crypto.model.IndicatorType.SIMPLE_MOVING_AVERAGE;
import static pro.crypto.model.tick.PriceType.CLOSE;

public class CommodityChannelIndex implements Indicator<CCIResult> {

    private final Tick[] originalData;
    private final int period;

    private CCIResult[] result;

    public CommodityChannelIndex(CCIRequest request) {
        this.originalData = request.getOriginalData();
        this.period = request.getPeriod();
        checkIncomingData();
    }

    @Override
    public IndicatorType getType() {
        return COMMODITY_CHANNEL_INDEX;
    }

    @Override
    public void calculate() {
        result = new CCIResult[originalData.length];
        BigDecimal[] typicalPrices = calculateTypicalPrices();
        MAResult[] smaResult = calculateSimpleMovingAverage(typicalPrices);
        BigDecimal[] meanAbsoluteDeviations = calculateMeanAbsoluteDeviations(smaResult);
        BigDecimal[] commodityChannelIndexes = calculateCommodityChannelIndexes(smaResult, meanAbsoluteDeviations);
        buildResult(commodityChannelIndexes);
    }

    @Override
    public CCIResult[] getResult() {
        if (isNull(result)) {
            calculate();
        }
        return result;
    }

    private void checkIncomingData() {
        checkOriginalData(originalData);
        checkOriginalDataSize(originalData, period);
        checkPeriod(period);
    }

    private BigDecimal[] calculateTypicalPrices() {
        return Stream.of(originalData)
                .map(TypicalPriceCalculator::calculate)
                .toArray(BigDecimal[]::new);
    }

    private MAResult[] calculateSimpleMovingAverage(BigDecimal[] typicalPrices) {
        Tick[] fakeTicks = FakeTicksCreator.createWithCloseOnly(typicalPrices);
        return MovingAverageFactory.createMovingAverage(buildTypicalPriceMovingAverageRequest(fakeTicks)).getResult();
    }

    private MARequest buildTypicalPriceMovingAverageRequest(Tick[] fakeTicks) {
        return MARequest.builder()
                .originalData(fakeTicks)
                .indicatorType(SIMPLE_MOVING_AVERAGE)
                .period(period)
                .priceType(CLOSE)
                .build();
    }

    private BigDecimal[] calculateMeanAbsoluteDeviations(MAResult[] smaResult) {
        BigDecimal[] meanAbsoluteDeviations = new BigDecimal[originalData.length];
        for (int currentIndex = period - 1; currentIndex < meanAbsoluteDeviations.length; currentIndex++) {
            meanAbsoluteDeviations[currentIndex] = calculateMeanAbsoluteDeviation(smaResult, currentIndex);
        }
        return meanAbsoluteDeviations;
    }

    private BigDecimal calculateMeanAbsoluteDeviation(MAResult[] smaResult, int currentIndex) {
        BigDecimal absoluteSum = calculateAbsoluteSum(smaResult, currentIndex);
        return MathHelper.divide(absoluteSum, new BigDecimal(period));
    }

    private BigDecimal calculateAbsoluteSum(MAResult[] smaResult, int currentIndex) {
        BigDecimal absoluteSum = BigDecimal.ZERO;
        for (int i = currentIndex - period + 1; i <= currentIndex; i++) {
            absoluteSum = absoluteSum.add(smaResult[currentIndex].getIndicatorValue().subtract(smaResult[i].getOriginalValue()).abs());
        }
        return absoluteSum;
    }

    private BigDecimal[] calculateCommodityChannelIndexes(MAResult[] smaResult, BigDecimal[] meanAbsoluteDeviations) {
        BigDecimal[] commodityChannelIndexes = new BigDecimal[originalData.length];
        for (int currentIndex = 0; currentIndex < commodityChannelIndexes.length; currentIndex++) {
            if (isNeedToSkip(currentIndex, smaResult, meanAbsoluteDeviations)) {
                continue;
            }
            commodityChannelIndexes[currentIndex] = calculateCommodityChannelIndex(currentIndex, smaResult, meanAbsoluteDeviations);
        }
        return commodityChannelIndexes;
    }

    private boolean isNeedToSkip(int index, MAResult[] smaResult, BigDecimal[] meanAbsoluteDeviations) {
        return isNull(smaResult[index].getIndicatorValue()) || isNull(smaResult[index].getOriginalValue()) ||
                isNull(meanAbsoluteDeviations[index]) || meanAbsoluteDeviations[index].compareTo(BigDecimal.ZERO) == 0;
    }

    private BigDecimal calculateCommodityChannelIndex(int index, MAResult[] smaResult, BigDecimal[] meanAbsoluteDeviations) {
        return MathHelper.divide(smaResult[index].getOriginalValue().subtract(smaResult[index].getIndicatorValue()),
                new BigDecimal(0.015).multiply(meanAbsoluteDeviations[index]));
    }

    private void buildResult(BigDecimal[] commodityChannelIndexes) {
        for (int i = 0; i < originalData.length; i++) {
            result[i] = new CCIResult(originalData[i].getTickTime(), commodityChannelIndexes[i]);
        }
    }

}
