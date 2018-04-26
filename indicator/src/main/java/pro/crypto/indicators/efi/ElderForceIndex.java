package pro.crypto.indicators.efi;

import pro.crypto.helper.FakeTicksCreator;
import pro.crypto.indicators.ma.MovingAverageFactory;
import pro.crypto.model.Indicator;
import pro.crypto.model.IndicatorType;
import pro.crypto.model.request.EFIRequest;
import pro.crypto.model.request.MARequest;
import pro.crypto.model.result.EFIResult;
import pro.crypto.model.result.MAResult;
import pro.crypto.model.tick.Tick;

import java.math.BigDecimal;
import java.util.stream.Stream;

import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;
import static pro.crypto.model.IndicatorType.ELDERS_FORCE_INDEX;
import static pro.crypto.model.IndicatorType.EXPONENTIAL_MOVING_AVERAGE;
import static pro.crypto.model.tick.PriceType.CLOSE;

public class ElderForceIndex implements Indicator<EFIResult> {

    private final Tick[] originalData;
    private final int period;

    private EFIResult[] result;

    public ElderForceIndex(EFIRequest request) {
        this.originalData = request.getOriginalData();
        this.period = request.getPeriod();
        checkIncomingData();
    }

    @Override
    public IndicatorType getType() {
        return ELDERS_FORCE_INDEX;
    }

    @Override
    public void calculate() {
        result = new EFIResult[originalData.length];
        BigDecimal[] forceIndexValues = calculateForceIndexValues();
        calculateEldersForceIndex(forceIndexValues);
    }

    @Override
    public EFIResult[] getResult() {
        if (isNull(result)) {
            calculate();
        }
        return result;
    }

    private void checkIncomingData() {
        checkOriginalData(originalData);
        checkOriginalDataSize(originalData, period + 1);
        checkPeriod(period);
    }

    private BigDecimal[] calculateForceIndexValues() {
        BigDecimal[] forceIndexValues = new BigDecimal[originalData.length];
        for (int currentIndex = 1; currentIndex < forceIndexValues.length; currentIndex++) {
            forceIndexValues[currentIndex] = calculateForceIndex(currentIndex);
        }
        return forceIndexValues;
    }

    private BigDecimal calculateForceIndex(int currentIndex) {
        return originalData[currentIndex].getClose()
                .subtract(originalData[currentIndex - 1]
                        .getClose()).multiply(originalData[currentIndex].getBaseVolume());
    }

    private void calculateEldersForceIndex(BigDecimal[] forceIndexValues) {
        BigDecimal[] smoothedIndexValues = smoothValue(forceIndexValues);
        buildEldersForceIndexResult(forceIndexValues, smoothedIndexValues);
    }

    private BigDecimal[] smoothValue(BigDecimal[] forceIndexValues) {
        return extractMAResult(MovingAverageFactory.create(buildMARequest(forceIndexValues)).getResult());
    }

    private MARequest buildMARequest(BigDecimal[] forceIndexValues) {
        return MARequest.builder()
                .originalData(FakeTicksCreator.createWithCloseOnly(forceIndexValues))
                .indicatorType(EXPONENTIAL_MOVING_AVERAGE)
                .priceType(CLOSE)
                .period(period)
                .build();
    }

    private BigDecimal[] extractMAResult(MAResult[] result) {
        return Stream.of(result)
                .map(MAResult::getIndicatorValue)
                .toArray(BigDecimal[]::new);
    }

    private void buildEldersForceIndexResult(BigDecimal[] forceIndexValues, BigDecimal[] smoothedIndexValues) {
        for (int currentIndex = 0; currentIndex < result.length; currentIndex++) {
            result[currentIndex] = new EFIResult(
                    originalData[currentIndex].getTickTime(),
                    nonNull(forceIndexValues[currentIndex])
                            ? smoothedIndexValues[currentIndex - 1]
                            : null
            );
        }
    }

}
