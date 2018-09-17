package pro.crypto.indicator.efi;

import pro.crypto.helper.FakeTicksCreator;
import pro.crypto.helper.IndicatorResultExtractor;
import pro.crypto.indicator.ma.MARequest;
import pro.crypto.indicator.ma.MovingAverageFactory;
import pro.crypto.model.Indicator;
import pro.crypto.model.IndicatorRequest;
import pro.crypto.model.IndicatorType;
import pro.crypto.model.tick.Tick;

import java.math.BigDecimal;
import java.util.stream.IntStream;

import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;
import static pro.crypto.model.IndicatorType.ELDERS_FORCE_INDEX;
import static pro.crypto.model.IndicatorType.EXPONENTIAL_MOVING_AVERAGE;
import static pro.crypto.model.tick.PriceType.CLOSE;

public class ElderForceIndex implements Indicator<EFIResult> {

    private final Tick[] originalData;
    private final int period;

    private EFIResult[] result;

    public ElderForceIndex(IndicatorRequest creationRequest) {
        EFIRequest request = (EFIRequest) creationRequest;
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
        return IntStream.range(0, originalData.length)
                .mapToObj(this::calculateForceIndex)
                .toArray(BigDecimal[]::new);
    }

    private BigDecimal calculateForceIndex(int currentIndex) {
        return currentIndex > 0
                ? calculateForceIndexValues(currentIndex)
                : null;
    }

    private BigDecimal calculateForceIndexValues(int currentIndex) {
        return originalData[currentIndex].getClose()
                .subtract(originalData[currentIndex - 1]
                        .getClose()).multiply(originalData[currentIndex].getBaseVolume());
    }

    private void calculateEldersForceIndex(BigDecimal[] forceIndexValues) {
        BigDecimal[] smoothedIndexValues = smoothValue(forceIndexValues);
        buildEldersForceIndexResult(forceIndexValues, smoothedIndexValues);
    }

    private BigDecimal[] smoothValue(BigDecimal[] forceIndexValues) {
        return IndicatorResultExtractor.extract(MovingAverageFactory.create(buildMARequest(forceIndexValues)).getResult());
    }

    private IndicatorRequest buildMARequest(BigDecimal[] forceIndexValues) {
        return MARequest.builder()
                .originalData(FakeTicksCreator.createWithCloseOnly(forceIndexValues))
                .indicatorType(EXPONENTIAL_MOVING_AVERAGE)
                .priceType(CLOSE)
                .period(period)
                .build();
    }

    private void buildEldersForceIndexResult(BigDecimal[] forceIndexValues, BigDecimal[] smoothedIndexValues) {
        IntStream.range(0, result.length)
                .forEach(idx -> result[idx] = new EFIResult(
                        originalData[idx].getTickTime(),
                        extractSmoothedIndex(forceIndexValues, smoothedIndexValues, idx)
                ));
    }

    private BigDecimal extractSmoothedIndex(BigDecimal[] forceIndexValues, BigDecimal[] smoothedIndexValues, int currentIndex) {
        return nonNull(forceIndexValues[currentIndex])
                ? smoothedIndexValues[currentIndex - 1]
                : null;
    }

}
