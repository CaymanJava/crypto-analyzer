package pro.crypto.strategy.stoch.ha;

import pro.crypto.analyzer.stoch.StochAnalyzer;
import pro.crypto.analyzer.stoch.StochAnalyzerRequest;
import pro.crypto.analyzer.stoch.StochAnalyzerResult;
import pro.crypto.indicator.ha.HARequest;
import pro.crypto.indicator.ha.HAResult;
import pro.crypto.indicator.ha.HeikenAshi;
import pro.crypto.indicator.stoch.StochRequest;
import pro.crypto.indicator.stoch.StochResult;
import pro.crypto.indicator.stoch.StochasticOscillator;
import pro.crypto.model.AnalyzerRequest;
import pro.crypto.model.IndicatorRequest;
import pro.crypto.model.IndicatorType;
import pro.crypto.model.Position;
import pro.crypto.model.SecurityLevel;
import pro.crypto.model.Strategy;
import pro.crypto.model.StrategyRequest;
import pro.crypto.model.StrategyType;
import pro.crypto.model.tick.Tick;

import java.util.HashSet;
import java.util.Set;
import java.util.stream.IntStream;

import static java.util.Arrays.stream;
import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;
import static pro.crypto.model.Position.ENTRY_LONG;
import static pro.crypto.model.Position.ENTRY_SHORT;
import static pro.crypto.model.SecurityLevel.OVERBOUGHT;
import static pro.crypto.model.SecurityLevel.OVERSOLD;
import static pro.crypto.model.StrategyType.STOCH_HA;

public class StochHaStrategy implements Strategy<StochHaResult> {

    private final Tick[] originalData;
    private final IndicatorType stochMovingAverageType;
    private final int stochFastPeriod;
    private final int stochSlowPeriod;
    private final Double stochOversoldLevel;
    private final Double stochOverboughtLevel;
    private final Set<Position> positions;

    private StochAnalyzerResult[] stochAnalyzerResults;
    private HAResult[] haResults;
    private StochHaResult[] result;

    public StochHaStrategy(StrategyRequest strategyRequest) {
        StochHaRequest request = (StochHaRequest) strategyRequest;
        this.originalData = request.getOriginalData();
        this.stochMovingAverageType = request.getStochMovingAverageType();
        this.stochFastPeriod = request.getStochFastPeriod();
        this.stochSlowPeriod = request.getStochSlowPeriod();
        this.stochOversoldLevel = request.getStochOversoldLevel();
        this.stochOverboughtLevel = request.getStochOverboughtLevel();
        this.positions = request.getPositions();
        checkPositions(positions);
    }

    @Override
    public StrategyType getType() {
        return STOCH_HA;
    }

    @Override
    public void analyze() {
        initResultArray();
        calculateHeikenAshiCandles();
        calculateAndAnalyzerStochastic();
        findEntries();
    }

    @Override
    public StochHaResult[] getResult() {
        if (isNull(result)) {
            analyze();
        }
        return result;
    }

    private void initResultArray() {
        result = stream(originalData)
                .map(originalDatum -> StochHaResult.builder()
                        .time(originalDatum.getTickTime())
                        .positions(new HashSet<>())
                        .build())
                .toArray(StochHaResult[]::new);
    }

    private StochResult[] calculateStochastic() {
        return new StochasticOscillator(buildStochRequest()).getResult();
    }

    private IndicatorRequest buildStochRequest() {
        return StochRequest.builder()
                .originalData(originalData)
                .movingAverageType(stochMovingAverageType)
                .slowStochPeriod(stochSlowPeriod)
                .fastStochPeriod(stochFastPeriod)
                .build();
    }

    private void calculateAndAnalyzerStochastic() {
        StochResult[] stochResults = calculateStochastic();
        stochAnalyzerResults = new StochAnalyzer(buildStochAnalyzerRequest(stochResults)).getResult();
    }

    private AnalyzerRequest buildStochAnalyzerRequest(StochResult[] stochResults) {
        return StochAnalyzerRequest.builder()
                .originalData(originalData)
                .indicatorResults(stochResults)
                .oversoldLevel(stochOversoldLevel)
                .overboughtLevel(stochOverboughtLevel)
                .build();
    }

    private void calculateHeikenAshiCandles() {
        haResults = new HeikenAshi(buildHARequest()).getResult();
    }

    private HARequest buildHARequest() {
        return new HARequest(originalData);
    }

    private void findEntries() {
        IntStream.range(0, haResults.length)
                .forEach(this::findEntry);
    }

    private void findEntry(int currentIndex) {
        if (isPossibleDefineEntry(currentIndex)) {
            defineEntry(currentIndex);
        }
    }

    private boolean isPossibleDefineEntry(int currentIndex) {
        return currentIndex > 1
                && nonNull(stochAnalyzerResults[currentIndex - 2].getSecurityLevel())
                && nonNull(stochAnalyzerResults[currentIndex - 1].getSecurityLevel())
                && nonNull(stochAnalyzerResults[currentIndex].getSecurityLevel());
    }

    private void defineEntry(int currentIndex) {
        defineLongEntry(currentIndex);
        defineShortEntry(currentIndex);
    }

    private void defineShortEntry(int currentIndex) {
        if (isRequired(ENTRY_SHORT) && isShortEntry(currentIndex)) {
            result[currentIndex].getPositions().add(ENTRY_SHORT);
        }
    }

    private void defineLongEntry(int currentIndex) {
        if (isRequired(ENTRY_LONG) && isLongEntry(currentIndex)) {
            result[currentIndex].getPositions().add(ENTRY_LONG);
        }
    }

    private boolean isRequired(Position entryLong) {
        return positions.contains(entryLong);
    }

    private boolean isLongEntry(int currentIndex) {
        return isRedCandle(currentIndex - 2) && isGreenCandle(currentIndex - 1) && isGreenCandle(currentIndex)
                && isStochCondition(currentIndex, OVERSOLD);
    }

    private boolean isStochCondition(int currentIndex, SecurityLevel oversold) {
        return (stochAnalyzerResults[currentIndex - 2].getSecurityLevel() == oversold
                && stochAnalyzerResults[currentIndex - 1].getSecurityLevel() != oversold
                && stochAnalyzerResults[currentIndex].getSecurityLevel() != oversold)
                || (stochAnalyzerResults[currentIndex - 1].getSecurityLevel() == oversold
                && stochAnalyzerResults[currentIndex].getSecurityLevel() != oversold);
    }

    private boolean isShortEntry(int currentIndex) {
        return isGreenCandle(currentIndex - 2) && isRedCandle(currentIndex - 1) && isRedCandle(currentIndex)
                && (isStochCondition(currentIndex, OVERBOUGHT));
    }

    private boolean isRedCandle(int index) {
        return haResults[index].getClose().compareTo(haResults[index].getOpen()) < 0;
    }

    private boolean isGreenCandle(int index) {
        return haResults[index].getClose().compareTo(haResults[index].getOpen()) > 0;
    }

}
