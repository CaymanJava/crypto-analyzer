package pro.crypto.indicator.trix;

import pro.crypto.helper.FakeTicksCreator;
import pro.crypto.helper.IndicatorResultExtractor;
import pro.crypto.indicator.ma.MARequest;
import pro.crypto.indicator.ma.MovingAverageFactory;
import pro.crypto.indicator.roc.ROCRequest;
import pro.crypto.indicator.roc.RateOfChange;
import pro.crypto.model.Indicator;
import pro.crypto.model.indicator.IndicatorType;
import pro.crypto.model.tick.Tick;
import pro.crypto.request.IndicatorRequest;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.stream.IntStream;

import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;
import static pro.crypto.model.indicator.IndicatorType.EXPONENTIAL_MOVING_AVERAGE;
import static pro.crypto.model.indicator.IndicatorType.TRIPLE_EXPONENTIAL_AVERAGE;
import static pro.crypto.model.tick.PriceType.CLOSE;

public class TripleExponentialAverage implements Indicator<TRIXResult> {

    private final static int SIGNAL_LINE_PERIOD = 9;
    private final Tick[] originalData;
    private final int period;

    private TRIXResult[] result;

    public TripleExponentialAverage(IndicatorRequest creationRequest) {
        TRIXRequest request = (TRIXRequest) creationRequest;
        this.originalData = request.getOriginalData();
        this.period = request.getPeriod();
        checkIncomingData();
    }

    @Override
    public IndicatorType getType() {
        return TRIPLE_EXPONENTIAL_AVERAGE;
    }

    @Override
    public void calculate() {
        result = new TRIXResult[originalData.length];
        BigDecimal[] singleEMA = calculateOriginalMovingAverage();
        BigDecimal[] doubleEMA = calculateDoubleMovingAverage(singleEMA);
        BigDecimal[] tripleEMA = calculateDoubleMovingAverage(doubleEMA);
        BigDecimal[] trixValues = calculateTrixValues(tripleEMA);
        BigDecimal[] signalLineValues = calculateSignalLine(trixValues);
        buildTripleExponentialAverage(trixValues, signalLineValues);
    }

    @Override
    public TRIXResult[] getResult() {
        if (isNull(result)) {
            calculate();
        }
        return result;
    }

    private void checkIncomingData() {
        checkOriginalData(originalData);
        checkOriginalDataSize(originalData, period * 3 + 10);
        checkPeriod(period);
    }

    private BigDecimal[] calculateOriginalMovingAverage() {
        return IndicatorResultExtractor.extractIndicatorValues(MovingAverageFactory
                .create(buildMARequest(originalData, period)).getResult());
    }

    private BigDecimal[] calculateDoubleMovingAverage(BigDecimal[] singleEMA) {
        return IndicatorResultExtractor.extractIndicatorValues(MovingAverageFactory.create(
                buildMARequest(FakeTicksCreator.createWithCloseOnly(singleEMA), period))
                .getResult());
    }

    private BigDecimal[] calculateTrixValues(BigDecimal[] tripleEMA) {
        return IndicatorResultExtractor.extractIndicatorValues(new RateOfChange(buildROCRequest(tripleEMA)).getResult());
    }

    private IndicatorRequest buildROCRequest(BigDecimal[] tripleEMA) {
        return ROCRequest.builder()
                .originalData(FakeTicksCreator.createWithCloseOnly(tripleEMA))
                .period(1)
                .priceType(CLOSE)
                .build();
    }

    private BigDecimal[] calculateSignalLine(BigDecimal[] trixValues) {
        return IndicatorResultExtractor.extractIndicatorValues(MovingAverageFactory.create(
                buildMARequest(FakeTicksCreator.createWithCloseOnly(trixValues), SIGNAL_LINE_PERIOD))
                .getResult());
    }

    private IndicatorRequest buildMARequest(Tick[] data, int period) {
        return MARequest.builder()
                .originalData(data)
                .period(period)
                .priceType(CLOSE)
                .indicatorType(EXPONENTIAL_MOVING_AVERAGE)
                .build();
    }

    private void buildTripleExponentialAverage(BigDecimal[] trixValues, BigDecimal[] signalLineValues) {
        int indicatorStartIndex = period * 3 - 3;
        IntStream.range(0, result.length)
                .forEach(idx -> result[idx] = buildTRIXResult(trixValues, signalLineValues, indicatorStartIndex, idx));
    }

    private TRIXResult buildTRIXResult(BigDecimal[] trixValues, BigDecimal[] signalLineValues, int indicatorStartIndex, int currentIndex) {
        return currentIndex >= indicatorStartIndex
                ? buildTRIXResult(originalData[currentIndex].getTickTime(), trixValues[currentIndex - indicatorStartIndex],
                extractSignalLineValue(trixValues, signalLineValues, currentIndex, indicatorStartIndex))
                : buildEmptyTRIXResult(originalData[currentIndex].getTickTime());
    }

    private BigDecimal extractSignalLineValue(BigDecimal[] trixValues, BigDecimal[] signalLineValues, int currentIndex, int indicatorStartIndex) {
        return nonNull(trixValues[currentIndex - indicatorStartIndex])
                ? signalLineValues[currentIndex - indicatorStartIndex - 1]
                : null;
    }

    private TRIXResult buildEmptyTRIXResult(LocalDateTime tickTime) {
        return new TRIXResult(tickTime, null, null);
    }

    private TRIXResult buildTRIXResult(LocalDateTime tickTime, BigDecimal trixValue, BigDecimal signalLineValue) {
        return new TRIXResult(tickTime, trixValue, signalLineValue);
    }

}
