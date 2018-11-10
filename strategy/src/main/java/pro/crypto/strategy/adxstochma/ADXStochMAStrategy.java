package pro.crypto.strategy.adxstochma;

import pro.crypto.helper.PeakValleyFinder;
import pro.crypto.indicator.adx.ADXRequest;
import pro.crypto.indicator.adx.ADXResult;
import pro.crypto.indicator.adx.AverageDirectionalMovementIndex;
import pro.crypto.indicator.ma.MARequest;
import pro.crypto.indicator.ma.MAResult;
import pro.crypto.indicator.ma.MovingAverageFactory;
import pro.crypto.indicator.stoch.StochRequest;
import pro.crypto.indicator.stoch.StochResult;
import pro.crypto.indicator.stoch.StochasticOscillator;
import pro.crypto.model.*;
import pro.crypto.model.tick.Tick;

import java.math.BigDecimal;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.IntStream;

import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;
import static pro.crypto.model.IndicatorType.EXPONENTIAL_MOVING_AVERAGE;
import static pro.crypto.model.Position.ENTRY_LONG;
import static pro.crypto.model.Position.ENTRY_SHORT;
import static pro.crypto.model.StrategyType.ADX_STOCH_MA;
import static pro.crypto.model.tick.PriceType.CLOSE;

public class ADXStochMAStrategy implements Strategy<ADXStochMAResult> {

    private final Tick[] originalData;
    private final IndicatorType stochMovingAverageType;
    private final int stochFastPeriod;
    private final int stochSlowPeriod;
    private final int adxPeriod;
    private final int firstMaPeriod;
    private final int secondMaPeriod;
    private final int thirdMaPeriod;
    private final Set<Position> positions;

    private Boolean[] peaks;
    private Boolean[] valleys;
    private StochResult[] stochResults;
    private ADXResult[] adxResults;
    private MAResult[] firstMaResult;
    private MAResult[] secondMaResult;
    private MAResult[] thirdMaResult;
    private int lastPeakIndex = 0;
    private int lastValleyIndex = 0;
    private ADXStochMAResult[] result;

    public ADXStochMAStrategy(StrategyRequest strategyRequest) {
        ADXStochMARequest request = (ADXStochMARequest) strategyRequest;
        this.originalData = request.getOriginalData();
        this.stochMovingAverageType = request.getStochMovingAverageType();
        this.stochFastPeriod = request.getStochFastPeriod();
        this.stochSlowPeriod = request.getStochSlowPeriod();
        this.adxPeriod = request.getAdxPeriod();
        this.firstMaPeriod = request.getFirstMaPeriod();
        this.secondMaPeriod = request.getSecondMaPeriod();
        this.thirdMaPeriod = request.getThirdMaPeriod();
        this.positions = request.getPositions();
        checkIncomingData();
    }

    @Override
    public StrategyType getType() {
        return ADX_STOCH_MA;
    }

    @Override
    public void analyze() {
        initResultArray();
        findPeaksAndValleys();
        calculateStochastic();
        calculateAverageDirectionalMovementIndex();
        calculateFirstMovingAverage();
        calculateSecondMovingAverage();
        calculateThirdMovingAverage();
        findEntries();
    }

    @Override
    public ADXStochMAResult[] getResult() {
        if (isNull(result)) {
            analyze();
        }
        return result;
    }

    private void checkIncomingData() {
        checkPositions(positions);
    }

    private void initResultArray() {
        result = IntStream.range(0, originalData.length)
                .mapToObj(idx -> ADXStochMAResult.builder()
                        .time(originalData[idx].getTickTime())
                        .positions(new HashSet<>())
                        .build())
                .toArray(ADXStochMAResult[]::new);
    }

    private void findPeaksAndValleys() {
        peaks = PeakValleyFinder.findPeaks(originalData);
        valleys = PeakValleyFinder.findValleys(originalData);
    }

    private void calculateStochastic() {
        stochResults = new StochasticOscillator(buildStochRequest()).getResult();
    }

    private IndicatorRequest buildStochRequest() {
        return StochRequest.builder()
                .originalData(originalData)
                .movingAverageType(stochMovingAverageType)
                .slowPeriod(stochSlowPeriod)
                .fastPeriod(stochFastPeriod)
                .build();
    }

    private void calculateAverageDirectionalMovementIndex() {
        adxResults = new AverageDirectionalMovementIndex(buildADXRequest()).getResult();
    }

    private IndicatorRequest buildADXRequest() {
        return ADXRequest.builder()
                .originalData(originalData)
                .period(adxPeriod)
                .build();
    }

    private void calculateFirstMovingAverage() {
        firstMaResult = MovingAverageFactory.create(buildMARequest(firstMaPeriod)).getResult();
    }

    private void calculateSecondMovingAverage() {
        secondMaResult = MovingAverageFactory.create(buildMARequest(secondMaPeriod)).getResult();
    }

    private void calculateThirdMovingAverage() {
        thirdMaResult = MovingAverageFactory.create(buildMARequest(thirdMaPeriod)).getResult();
    }

    private IndicatorRequest buildMARequest(int period) {
        return MARequest.builder()
                .originalData(originalData)
                .period(period)
                .priceType(CLOSE)
                .indicatorType(EXPONENTIAL_MOVING_AVERAGE)
                .build();
    }

    private void findEntries() {
        IntStream.range(0, originalData.length)
                .forEach(this::findEntry);
    }

    private void findEntry(int currentIndex) {
        refreshPeakAndValley(currentIndex);
        if (isPossibleDefineEntry(currentIndex)) {
            defineEntry(currentIndex);
        }
    }

    private void refreshPeakAndValley(int currentIndex) {
        if (peaks[currentIndex]) {
            lastPeakIndex = currentIndex;
        }
        if (valleys[currentIndex]) {
            lastValleyIndex = currentIndex;
        }
    }

    private boolean isPossibleDefineEntry(int currentIndex) {
        return nonNull(stochResults[currentIndex].getFastStochastic())
                && nonNull(adxResults[currentIndex].getAverageDirectionalIndex())
                && nonNull(firstMaResult[currentIndex].getIndicatorValue())
                && nonNull(secondMaResult[currentIndex].getIndicatorValue())
                && nonNull(thirdMaResult[currentIndex].getIndicatorValue());
    }

    private void defineEntry(int currentIndex) {
        if (positions.contains(ENTRY_LONG) && isLongEntry(currentIndex)) {
            result[currentIndex].getPositions().add(ENTRY_LONG);
            result[currentIndex].setStopLose(originalData[lastValleyIndex].getLow());
        }

        if (positions.contains(ENTRY_SHORT) && isShortEntry(currentIndex)) {
            result[currentIndex].getPositions().add(ENTRY_SHORT);
            result[currentIndex].setStopLose(originalData[lastPeakIndex].getHigh());
        }
    }

    private boolean isLongEntry(int currentIndex) {
        return isStochasticLongEntryCondition(currentIndex)
                && isADXEntryCondition(currentIndex)
                && isMALongEntryCondition(currentIndex);
    }

    private boolean isStochasticLongEntryCondition(int currentIndex) {
        return stochResults[currentIndex - 1].getFastStochastic().compareTo(new BigDecimal(50)) > 0;
    }

    private boolean isMALongEntryCondition(int currentIndex) {
        return firstMaResult[currentIndex].getIndicatorValue().compareTo(secondMaResult[currentIndex].getIndicatorValue()) > 0
                && secondMaResult[currentIndex].getIndicatorValue().compareTo(thirdMaResult[currentIndex].getIndicatorValue()) > 0;
    }

    private boolean isShortEntry(int currentIndex) {
        return isStochasticShortEntryCondition(currentIndex)
                && isADXEntryCondition(currentIndex)
                && isMAShortEntryCondition(currentIndex);
    }

    private boolean isStochasticShortEntryCondition(int currentIndex) {
        return stochResults[currentIndex - 1].getFastStochastic().compareTo(new BigDecimal(50)) < 0;
    }

    private boolean isMAShortEntryCondition(int currentIndex) {
        return thirdMaResult[currentIndex].getIndicatorValue().compareTo(secondMaResult[currentIndex].getIndicatorValue()) > 0
                && secondMaResult[currentIndex].getIndicatorValue().compareTo(firstMaResult[currentIndex].getIndicatorValue()) > 0;
    }

    private boolean isADXEntryCondition(int currentIndex) {
        return adxResults[currentIndex].getAverageDirectionalIndex().compareTo(new BigDecimal(20)) > 0;
    }

}
