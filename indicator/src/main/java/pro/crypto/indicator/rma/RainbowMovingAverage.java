package pro.crypto.indicator.rma;

import pro.crypto.helper.IndicatorResultExtractor;
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
import static pro.crypto.helper.FakeTicksCreator.createWithCloseOnly;
import static pro.crypto.model.IndicatorType.RAINBOW_MOVING_AVERAGE;
import static pro.crypto.model.IndicatorType.SIMPLE_MOVING_AVERAGE;
import static pro.crypto.model.tick.PriceType.CLOSE;

public class RainbowMovingAverage implements Indicator<RMAResult> {

    private final Tick[] originalData;
    private final PriceType priceType;
    private final int period;

    private RMAResult[] result;

    private BigDecimal[] firstMAValues;
    private BigDecimal[] secondMAValues;
    private BigDecimal[] thirdMAValues;
    private BigDecimal[] fourthMAValues;
    private BigDecimal[] fifthMAValues;
    private BigDecimal[] sixthMAValues;
    private BigDecimal[] seventhMAValues;
    private BigDecimal[] eighthMAValues;
    private BigDecimal[] ninthMAValues;
    private BigDecimal[] tenthMAValues;

    public RainbowMovingAverage(IndicatorRequest creationRequest) {
        RMARequest request = (RMARequest) creationRequest;
        this.originalData = request.getOriginalData();
        this.priceType = request.getPriceType();
        this.period = request.getPeriod();
        checkIncomingData();
    }

    @Override
    public IndicatorType getType() {
        return RAINBOW_MOVING_AVERAGE;
    }

    @Override
    public void calculate() {
        firstMAValues = calculateMovingAverageValues(originalData, priceType);
        secondMAValues = calculateMovingAverageValues(createWithCloseOnly(firstMAValues), CLOSE);
        thirdMAValues = calculateMovingAverageValues(createWithCloseOnly(secondMAValues), CLOSE);
        fourthMAValues = calculateMovingAverageValues(createWithCloseOnly(thirdMAValues), CLOSE);
        fifthMAValues = calculateMovingAverageValues(createWithCloseOnly(fourthMAValues), CLOSE);
        sixthMAValues = calculateMovingAverageValues(createWithCloseOnly(fifthMAValues), CLOSE);
        seventhMAValues = calculateMovingAverageValues(createWithCloseOnly(sixthMAValues), CLOSE);
        eighthMAValues = calculateMovingAverageValues(createWithCloseOnly(seventhMAValues), CLOSE);
        ninthMAValues = calculateMovingAverageValues(createWithCloseOnly(eighthMAValues), CLOSE);
        tenthMAValues = calculateMovingAverageValues(createWithCloseOnly(ninthMAValues), CLOSE);
        buildRainbowMovingAverageResult();
    }

    @Override
    public RMAResult[] getResult() {
        if (isNull(result)) {
            calculate();
        }
        return result;
    }

    private void checkIncomingData() {
        checkOriginalData(originalData);
        checkOriginalDataSize(originalData, period * 10 - 10);
        checkPriceType(priceType);
        checkPeriod(period);
    }

    private BigDecimal[] calculateMovingAverageValues(Tick[] data, PriceType priceType) {
        BigDecimal[] maValues = IndicatorResultExtractor.extract(calculateMovingAverage(data, priceType));
        BigDecimal[] result = new BigDecimal[originalData.length];
        System.arraycopy(maValues, 0, result, originalData.length - maValues.length, maValues.length);
        return result;
    }

    private SimpleIndicatorResult[] calculateMovingAverage(Tick[] originalData, PriceType priceType) {
        return MovingAverageFactory.create(buildMAResult(originalData, priceType)).getResult();
    }

    private IndicatorRequest buildMAResult(Tick[] originalData, PriceType priceType) {
        return MARequest.builder()
                .originalData(originalData)
                .period(period)
                .priceType(priceType)
                .indicatorType(SIMPLE_MOVING_AVERAGE)
                .build();
    }

    private void buildRainbowMovingAverageResult() {
        result = IntStream.range(0, originalData.length)
                .mapToObj(this::buildRMAResult)
                .toArray(RMAResult[]::new);
    }

    private RMAResult buildRMAResult(int currentIndex) {
        return new RMAResult(
                originalData[currentIndex].getTickTime(), firstMAValues[currentIndex], secondMAValues[currentIndex], thirdMAValues[currentIndex],
                fourthMAValues[currentIndex], fifthMAValues[currentIndex], sixthMAValues[currentIndex], seventhMAValues[currentIndex],
                eighthMAValues[currentIndex], ninthMAValues[currentIndex], tenthMAValues[currentIndex]
        );
    }

}
