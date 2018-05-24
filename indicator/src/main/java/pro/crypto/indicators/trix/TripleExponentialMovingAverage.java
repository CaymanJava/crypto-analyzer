package pro.crypto.indicators.trix;

import pro.crypto.helper.FakeTicksCreator;
import pro.crypto.helper.IndicatorResultExtractor;
import pro.crypto.indicators.ma.MovingAverageFactory;
import pro.crypto.indicators.roc.RangeOfChange;
import pro.crypto.model.Indicator;
import pro.crypto.model.IndicatorType;
import pro.crypto.model.request.MARequest;
import pro.crypto.model.request.ROCRequest;
import pro.crypto.model.request.TRIXRequest;
import pro.crypto.model.result.TRIXResult;
import pro.crypto.model.tick.Tick;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;
import static pro.crypto.model.IndicatorType.EXPONENTIAL_MOVING_AVERAGE;
import static pro.crypto.model.IndicatorType.TRIPLE_EXPONENTIAL_MOVING_AVERAGE;
import static pro.crypto.model.tick.PriceType.CLOSE;

public class TripleExponentialMovingAverage implements Indicator<TRIXResult> {

    private final static int SIGNAL_LINE_PERIOD = 9;
    private final Tick[] originalData;
    private final int period;

    private TRIXResult[] result;

    public TripleExponentialMovingAverage(TRIXRequest request) {
        this.originalData = request.getOriginalData();
        this.period = request.getPeriod();
        checkIncomingData();
    }

    @Override
    public IndicatorType getType() {
        return TRIPLE_EXPONENTIAL_MOVING_AVERAGE;
    }

    @Override
    public void calculate() {
        result = new TRIXResult[originalData.length];
        BigDecimal[] singleEMA = calculateOriginalMovingAverage();
        BigDecimal[] doubleEMA = calculateDoubleMovingAverage(singleEMA);
        BigDecimal[] tripleEMA = calculateDoubleMovingAverage(doubleEMA);
        BigDecimal[] trixValues = calculateTrixValues(tripleEMA);
        BigDecimal[] signalLineValues = calculateSignalLine(trixValues);
        buildTripleExponentialMovingAverage(trixValues, signalLineValues);
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
        return IndicatorResultExtractor.extract(MovingAverageFactory
                .create(buildMARequest(originalData, period)).getResult());
    }

    private BigDecimal[] calculateDoubleMovingAverage(BigDecimal[] singleEMA) {
        return IndicatorResultExtractor.extract(MovingAverageFactory.create(
                buildMARequest(FakeTicksCreator.createWithCloseOnly(singleEMA), period))
                .getResult());
    }

    private BigDecimal[] calculateTrixValues(BigDecimal[] tripleEMA) {
        return IndicatorResultExtractor.extract(new RangeOfChange(buildROCRequest(tripleEMA)).getResult());
    }

    private ROCRequest buildROCRequest(BigDecimal[] tripleEMA) {
        return ROCRequest.builder()
                .originalData(FakeTicksCreator.createWithCloseOnly(tripleEMA))
                .period(1)
                .priceType(CLOSE)
                .build();
    }

    private BigDecimal[] calculateSignalLine(BigDecimal[] trixValues) {
        return IndicatorResultExtractor.extract(MovingAverageFactory.create(
                buildMARequest(FakeTicksCreator.createWithCloseOnly(trixValues), SIGNAL_LINE_PERIOD))
                .getResult());
    }

    private MARequest buildMARequest(Tick[] data, int period) {
        return MARequest.builder()
                .originalData(data)
                .period(period)
                .priceType(CLOSE)
                .indicatorType(EXPONENTIAL_MOVING_AVERAGE)
                .build();
    }

    private void buildTripleExponentialMovingAverage(BigDecimal[] trixValues, BigDecimal[] signalLineValues) {
        int indicatorStartIndex = period * 3 - 3;
        for (int currentIndex = 0; currentIndex < result.length; currentIndex++) {
            result[currentIndex] = buildTRIXResult(trixValues, signalLineValues, indicatorStartIndex, currentIndex);
        }
    }

    private TRIXResult buildTRIXResult(BigDecimal[] trixValues, BigDecimal[] signalLineValues, int indicatorStartIndex, int currentIndex) {
        return currentIndex >= indicatorStartIndex
                ? buildTRIXResult(
                originalData[currentIndex].getTickTime(),
                trixValues[currentIndex - indicatorStartIndex],
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
