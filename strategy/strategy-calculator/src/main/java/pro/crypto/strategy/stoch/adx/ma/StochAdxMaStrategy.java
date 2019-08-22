package pro.crypto.strategy.stoch.adx.ma;

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
import pro.crypto.model.Strategy;
import pro.crypto.model.indicator.IndicatorType;
import pro.crypto.model.strategy.Position;
import pro.crypto.model.strategy.StrategyType;
import pro.crypto.model.tick.Tick;
import pro.crypto.request.IndicatorRequest;
import pro.crypto.request.StrategyRequest;

import java.math.BigDecimal;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.IntStream;

import static java.util.Arrays.stream;
import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;
import static java.util.Optional.ofNullable;
import static pro.crypto.model.indicator.IndicatorType.EXPONENTIAL_MOVING_AVERAGE;
import static pro.crypto.model.strategy.Position.ENTRY_LONG;
import static pro.crypto.model.strategy.Position.ENTRY_SHORT;
import static pro.crypto.model.strategy.StrategyType.STOCH_ADX_MA;
import static pro.crypto.model.tick.PriceType.CLOSE;

public class StochAdxMaStrategy implements Strategy<StochAdxMaResult> {

    private final Tick[] originalData;
    private final IndicatorType stochMovingAverageType;
    private final int stochFastPeriod;
    private final int stochSlowPeriod;
    private final int adxPeriod;
    private final int firstMaPeriod;
    private final int secondMaPeriod;
    private final int thirdMaPeriod;
    private final BigDecimal stochasticSignalLine;
    private final BigDecimal adxSignalLine;
    private final Set<Position> positions;

    private Boolean[] peaks;
    private Boolean[] valleys;

    private StochResult[] stochResults;
    private ADXResult[] adxResults;
    private MAResult[] firstMaResults;
    private MAResult[] secondMaResults;
    private MAResult[] thirdMaResults;

    private int lastPeakIndex = 0;
    private int lastValleyIndex = 0;
    private StochAdxMaResult[] result;

    public StochAdxMaStrategy(StrategyRequest strategyRequest) {
        StochAdxMaRequest request = (StochAdxMaRequest) strategyRequest;
        this.originalData = request.getOriginalData();
        this.stochMovingAverageType = request.getStochMovingAverageType();
        this.stochFastPeriod = request.getStochFastPeriod();
        this.stochSlowPeriod = request.getStochSlowPeriod();
        this.adxPeriod = request.getAdxPeriod();
        this.firstMaPeriod = request.getFirstMaPeriod();
        this.secondMaPeriod = request.getSecondMaPeriod();
        this.thirdMaPeriod = request.getThirdMaPeriod();
        this.stochasticSignalLine = extractStochasticSignalLine(request);
        this.adxSignalLine = extractAdxSignalLine(request);
        this.positions = request.getPositions();
        checkPositions(positions);
    }

    @Override
    public StrategyType getType() {
        return STOCH_ADX_MA;
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
        addIndicatorsResults();
    }

    @Override
    public StochAdxMaResult[] getResult() {
        if (isNull(result)) {
            analyze();
        }
        return result;
    }

    private BigDecimal extractStochasticSignalLine(StochAdxMaRequest request) {
        return ofNullable(request.getStochasticSignalLine())
                .map(BigDecimal::new)
                .orElse(new BigDecimal(50));
    }

    private BigDecimal extractAdxSignalLine(StochAdxMaRequest request) {
        return ofNullable(request.getAdxSignalLine())
                .map(BigDecimal::new)
                .orElse(new BigDecimal(50));
    }

    private void initResultArray() {
        result = stream(originalData)
                .map(tick -> StochAdxMaResult.builder()
                        .tick(tick)
                        .positions(new HashSet<>())
                        .build())
                .toArray(StochAdxMaResult[]::new);
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
                .slowStochPeriod(stochSlowPeriod)
                .fastStochPeriod(stochFastPeriod)
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
        firstMaResults = MovingAverageFactory.create(buildMARequest(firstMaPeriod)).getResult();
    }

    private void calculateSecondMovingAverage() {
        secondMaResults = MovingAverageFactory.create(buildMARequest(secondMaPeriod)).getResult();
    }

    private void calculateThirdMovingAverage() {
        thirdMaResults = MovingAverageFactory.create(buildMARequest(thirdMaPeriod)).getResult();
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
                && nonNull(firstMaResults[currentIndex].getIndicatorValue())
                && nonNull(secondMaResults[currentIndex].getIndicatorValue())
                && nonNull(thirdMaResults[currentIndex].getIndicatorValue());
    }

    private void defineEntry(int currentIndex) {
        defineLongEntry(currentIndex);
        defineShortEntry(currentIndex);
    }

    private void defineShortEntry(int currentIndex) {
        if (isRequired(ENTRY_SHORT) && isShortEntry(currentIndex)) {
            result[currentIndex].getPositions().add(ENTRY_SHORT);
            result[currentIndex].setStopLose(originalData[lastPeakIndex].getHigh());
        }
    }

    private void defineLongEntry(int currentIndex) {
        if (isRequired(ENTRY_LONG) && isLongEntry(currentIndex)) {
            result[currentIndex].getPositions().add(ENTRY_LONG);
            result[currentIndex].setStopLose(originalData[lastValleyIndex].getLow());
        }
    }

    private boolean isRequired(Position entryLong) {
        return positions.contains(entryLong);
    }

    private boolean isLongEntry(int currentIndex) {
        return isStochasticLongEntryCondition(currentIndex)
                && isADXEntryCondition(currentIndex)
                && isMALongEntryCondition(currentIndex);
    }

    private boolean isStochasticLongEntryCondition(int currentIndex) {
        return stochResults[currentIndex - 1].getFastStochastic().compareTo(stochasticSignalLine) > 0;
    }

    private boolean isMALongEntryCondition(int currentIndex) {
        return firstMaResults[currentIndex].getIndicatorValue().compareTo(secondMaResults[currentIndex].getIndicatorValue()) > 0
                && secondMaResults[currentIndex].getIndicatorValue().compareTo(thirdMaResults[currentIndex].getIndicatorValue()) > 0;
    }

    private boolean isShortEntry(int currentIndex) {
        return isStochasticShortEntryCondition(currentIndex)
                && isADXEntryCondition(currentIndex)
                && isMAShortEntryCondition(currentIndex);
    }

    private boolean isStochasticShortEntryCondition(int currentIndex) {
        return stochResults[currentIndex - 1].getFastStochastic().compareTo(stochasticSignalLine) < 0;
    }

    private boolean isMAShortEntryCondition(int currentIndex) {
        return thirdMaResults[currentIndex].getIndicatorValue().compareTo(secondMaResults[currentIndex].getIndicatorValue()) > 0
                && secondMaResults[currentIndex].getIndicatorValue().compareTo(firstMaResults[currentIndex].getIndicatorValue()) > 0;
    }

    private boolean isADXEntryCondition(int currentIndex) {
        return adxResults[currentIndex].getAverageDirectionalIndex().compareTo(adxSignalLine) > 0;
    }

    private void addIndicatorsResults() {
        IntStream.range(0, result.length)
                .forEach(this::addIndicatorsResult);
    }

    private void addIndicatorsResult(int index) {
        StochAdxMaResult stochAdxMaResult = result[index];
        addStochasticResult(stochResults[index], stochAdxMaResult);
        addAdxResult(adxResults[index], stochAdxMaResult);
        addFirstMaResult(firstMaResults[index], stochAdxMaResult);
        addSecondMaResult(secondMaResults[index], stochAdxMaResult);
        addThirdMaResult(thirdMaResults[index], stochAdxMaResult);
    }

    private void addStochasticResult(StochResult stochResult, StochAdxMaResult stochAdxMaResult) {
        stochAdxMaResult.setFastStochasticValue(stochResult.getFastStochastic());
        stochAdxMaResult.setSlowStochasticValue(stochResult.getSlowStochastic());
    }

    private void addAdxResult(ADXResult adxResult, StochAdxMaResult stochAdxMaResult) {
        stochAdxMaResult.setPositiveAdxValue(adxResult.getPositiveDirectionalIndicator());
        stochAdxMaResult.setNegativeAdxValue(adxResult.getNegativeDirectionalIndicator());
        stochAdxMaResult.setAverageAdxValue(adxResult.getAverageDirectionalIndex());
    }

    private void addFirstMaResult(MAResult firstMaResult, StochAdxMaResult stochAdxMaResult) {
        stochAdxMaResult.setFirstMaValue(firstMaResult.getIndicatorValue());
    }

    private void addSecondMaResult(MAResult secondMaResult, StochAdxMaResult stochAdxMaResult) {
        stochAdxMaResult.setSecondMaValue(secondMaResult.getIndicatorValue());
    }

    private void addThirdMaResult(MAResult thirdMaResult, StochAdxMaResult stochAdxMaResult) {
        stochAdxMaResult.setThirdMaValue(thirdMaResult.getIndicatorValue());
    }

}
