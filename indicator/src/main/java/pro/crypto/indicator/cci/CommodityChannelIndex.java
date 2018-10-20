package pro.crypto.indicator.cci;

import pro.crypto.helper.FakeTicksCreator;
import pro.crypto.helper.IndicatorResultExtractor;
import pro.crypto.helper.MathHelper;
import pro.crypto.helper.TypicalPriceCalculator;
import pro.crypto.indicator.ma.MARequest;
import pro.crypto.indicator.ma.MovingAverageFactory;
import pro.crypto.model.Indicator;
import pro.crypto.model.IndicatorRequest;
import pro.crypto.model.IndicatorType;
import pro.crypto.model.SimpleIndicatorResult;
import pro.crypto.model.tick.Tick;

import java.math.BigDecimal;
import java.util.stream.IntStream;

import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;
import static pro.crypto.model.IndicatorType.COMMODITY_CHANNEL_INDEX;
import static pro.crypto.model.IndicatorType.SIMPLE_MOVING_AVERAGE;
import static pro.crypto.model.tick.PriceType.CLOSE;

public class CommodityChannelIndex implements Indicator<CCIResult> {

    private final Tick[] originalData;
    private final int period;

    private CCIResult[] result;

    public CommodityChannelIndex(IndicatorRequest creationRequest) {
        CCIRequest request = (CCIRequest) creationRequest;
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
        BigDecimal[] typicalPrices = TypicalPriceCalculator.calculateTypicalPrices(originalData);
        BigDecimal[] smaResult = calculateMovingAverageValues(typicalPrices);
        BigDecimal[] meanAbsoluteDeviations = calculateMeanAbsoluteDeviations(typicalPrices, smaResult);
        BigDecimal[] commodityChannelIndexes = calculateCommodityChannelIndexes(typicalPrices, smaResult, meanAbsoluteDeviations);
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

    private BigDecimal[] calculateMovingAverageValues(BigDecimal[] typicalPrices) {
        return IndicatorResultExtractor.extractIndicatorValues(calculateMovingAverage(typicalPrices));
    }

    private SimpleIndicatorResult[] calculateMovingAverage(BigDecimal[] typicalPrices) {
        return MovingAverageFactory.create(buildMovingAverageRequest(typicalPrices)).getResult();
    }

    private IndicatorRequest buildMovingAverageRequest(BigDecimal[] typicalPrices) {
        return MARequest.builder()
                .originalData(FakeTicksCreator.createWithCloseOnly(typicalPrices))
                .indicatorType(SIMPLE_MOVING_AVERAGE)
                .period(period)
                .priceType(CLOSE)
                .build();
    }

    private BigDecimal[] calculateMeanAbsoluteDeviations(BigDecimal[] typicalPrices, BigDecimal[] smaResult) {
        return IntStream.range(0, originalData.length)
                .mapToObj(idx -> calculateMeanAbsoluteDeviation(typicalPrices, smaResult, idx))
                .toArray(BigDecimal[]::new);
    }

    private BigDecimal calculateMeanAbsoluteDeviation(BigDecimal[] typicalPrices, BigDecimal[] smaResult, int currentIndex) {
        return currentIndex >= period - 1
                ? calculateMeanAbsoluteDeviationValue(typicalPrices, smaResult, currentIndex)
                : null;
    }

    private BigDecimal calculateMeanAbsoluteDeviationValue(BigDecimal[] typicalPrices, BigDecimal[] smaResult, int currentIndex) {
        BigDecimal absoluteSum = calculateAbsoluteSum(typicalPrices, smaResult, currentIndex);
        return MathHelper.divide(absoluteSum, new BigDecimal(period));
    }

    private BigDecimal calculateAbsoluteSum(BigDecimal[] typicalPrices, BigDecimal[] smaResult, int outerIndex) {
        return IntStream.rangeClosed(outerIndex - period + 1, outerIndex)
                .mapToObj(idx -> calculateAbsolute(typicalPrices, smaResult, outerIndex, idx))
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    private BigDecimal calculateAbsolute(BigDecimal[] typicalPrices, BigDecimal[] smaResult, int outerIndex, int currentIndex) {
        return smaResult[outerIndex].subtract(typicalPrices[currentIndex]).abs();
    }

    private BigDecimal[] calculateCommodityChannelIndexes(BigDecimal[] typicalPrices, BigDecimal[] smaResult, BigDecimal[] meanAbsoluteDeviations) {
        return IntStream.range(0, originalData.length)
                .mapToObj(idx -> calculateCommodityChannelIndex(typicalPrices[idx], smaResult[idx], meanAbsoluteDeviations[idx]))
                .toArray(BigDecimal[]::new);
    }

    private BigDecimal calculateCommodityChannelIndex(BigDecimal typicalPrice, BigDecimal smaIndicatorValue, BigDecimal meanAbsoluteDeviation) {
        return isPossibleToCalculate(smaIndicatorValue, meanAbsoluteDeviation)
                ? calculateCommodityChannelIndexValue(typicalPrice, smaIndicatorValue, meanAbsoluteDeviation)
                : null;
    }

    private boolean isPossibleToCalculate(BigDecimal smaValue, BigDecimal meanAbsoluteDeviation) {
        return nonNull(smaValue) && nonNull(meanAbsoluteDeviation) && meanAbsoluteDeviation.compareTo(BigDecimal.ZERO) != 0;
    }

    private BigDecimal calculateCommodityChannelIndexValue(BigDecimal typicalPrice, BigDecimal smaIndicatorValue, BigDecimal meanAbsoluteDeviation) {
        return MathHelper.divide(typicalPrice.subtract(smaIndicatorValue),
                new BigDecimal(0.015).multiply(meanAbsoluteDeviation));
    }

    private void buildResult(BigDecimal[] commodityChannelIndexes) {
        IntStream.range(0, result.length)
                .forEach(idx -> result[idx] = new CCIResult(originalData[idx].getTickTime(), commodityChannelIndexes[idx]));
    }

}
