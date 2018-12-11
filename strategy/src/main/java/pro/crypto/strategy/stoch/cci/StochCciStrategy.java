package pro.crypto.strategy.stoch.cci;

import pro.crypto.helper.IndicatorResultExtractor;
import pro.crypto.helper.SignalArrayMerger;
import pro.crypto.helper.StaticLineCrossAnalyzer;
import pro.crypto.indicator.cci.CCIRequest;
import pro.crypto.indicator.cci.CCIResult;
import pro.crypto.indicator.cci.CommodityChannelIndex;
import pro.crypto.indicator.stoch.StochRequest;
import pro.crypto.indicator.stoch.StochResult;
import pro.crypto.indicator.stoch.StochasticOscillator;
import pro.crypto.model.IndicatorRequest;
import pro.crypto.model.IndicatorType;
import pro.crypto.model.Position;
import pro.crypto.model.Signal;
import pro.crypto.model.Strategy;
import pro.crypto.model.StrategyRequest;
import pro.crypto.model.StrategyType;
import pro.crypto.model.tick.Tick;

import java.math.BigDecimal;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import static java.math.BigDecimal.ZERO;
import static java.util.Arrays.stream;
import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;
import static java.util.Optional.ofNullable;
import static pro.crypto.model.Position.ENTRY_LONG;
import static pro.crypto.model.Position.ENTRY_SHORT;
import static pro.crypto.model.Signal.BUY;
import static pro.crypto.model.Signal.NEUTRAL;
import static pro.crypto.model.Signal.SELL;
import static pro.crypto.model.StrategyType.STOCH_CCI;

public class StochCciStrategy implements Strategy<StochCciResult> {

    private final Tick[] originalData;
    private final IndicatorType stochMovingAverageType;
    private final int fastStochPeriod;
    private final int slowStochPeriod;
    private final BigDecimal stochOversoldLevel;
    private final BigDecimal stochOverboughtLevel;
    private final int cciPeriod;
    private final BigDecimal cciOversoldLevel;
    private final BigDecimal cciOverboughtLevel;
    private final Set<Position> positions;

    private BigDecimal[] fastStochasticLine;
    private Signal[] stochCrossSignals;
    private BigDecimal[] cciResult;
    private Signal[] cciCrossSignals;
    private Signal stochCurrentSignal;
    private Signal cciCurrentSignal;
    private StochCciResult[] result;

    public StochCciStrategy(StrategyRequest strategyRequest) {
        StochCciRequest request = (StochCciRequest) strategyRequest;
        this.originalData = request.getOriginalData();
        this.stochMovingAverageType = request.getStochMovingAverageType();
        this.fastStochPeriod = request.getFastStochPeriod();
        this.slowStochPeriod = request.getSlowStochPeriod();
        this.stochOversoldLevel = extractStochOversoldLevel(request);
        this.stochOverboughtLevel = extractStochOverboughtLevel(request);
        this.cciOversoldLevel = extractCCIOversoldLevel(request);
        this.cciOverboughtLevel = extractCCIOverboughtLevel(request);
        this.cciPeriod = request.getCciPeriod();
        this.positions = request.getPositions();
        checkPositions(positions);
    }

    @Override
    public StrategyType getType() {
        return STOCH_CCI;
    }

    @Override
    public void analyze() {
        initResultArray();
        calculateStochasticCrossSignals();
        calculateCCICrossSignals();
        findEntries();
    }

    @Override
    public StochCciResult[] getResult() {
        if (isNull(result)) {
            analyze();
        }
        return result;
    }

    private BigDecimal extractStochOversoldLevel(StochCciRequest request) {
        return ofNullable(request.getStochOversoldLevel())
                .map(BigDecimal::new)
                .orElse(new BigDecimal(20));
    }

    private BigDecimal extractStochOverboughtLevel(StochCciRequest request) {
        return ofNullable(request.getStochOverboughtLevel())
                .map(BigDecimal::new)
                .orElse(new BigDecimal(80));
    }

    private BigDecimal extractCCIOversoldLevel(StochCciRequest request) {
        return ofNullable(request.getCciOversoldLevel())
                .map(BigDecimal::new)
                .orElse(new BigDecimal(-100));
    }

    private BigDecimal extractCCIOverboughtLevel(StochCciRequest request) {
        return ofNullable(request.getCciOverboughtLevel())
                .map(BigDecimal::new)
                .orElse(new BigDecimal(100));
    }

    private void initResultArray() {
        result = stream(originalData)
                .map(originalDatum -> StochCciResult.builder()
                        .time(originalDatum.getTickTime())
                        .positions(new HashSet<>())
                        .build())
                .toArray(StochCciResult[]::new);
    }

    private void calculateStochasticCrossSignals() {
        calculateFastStochastic();
        Signal[] oversoldCrossSignals = findStochasticOversoldCrossSignals();
        Signal[] overboughtCrossSignals = findStochasticOverboughtCrossSignals();
        stochCrossSignals = SignalArrayMerger.mergeSignals(oversoldCrossSignals, overboughtCrossSignals);
    }

    private void calculateFastStochastic() {
        fastStochasticLine = Stream.of(new StochasticOscillator(buildStochRequest()).getResult())
                .map(StochResult::getFastStochastic)
                .toArray(BigDecimal[]::new);
    }

    private IndicatorRequest buildStochRequest() {
        return StochRequest.builder()
                .originalData(originalData)
                .movingAverageType(stochMovingAverageType)
                .fastStochPeriod(fastStochPeriod)
                .slowStochPeriod(slowStochPeriod)
                .build();
    }

    private Signal[] findStochasticOversoldCrossSignals() {
        return new StaticLineCrossAnalyzer(fastStochasticLine, stochOversoldLevel)
                .withRemovingFalsePositive(SELL)
                .analyze();
    }

    private Signal[] findStochasticOverboughtCrossSignals() {
        return new StaticLineCrossAnalyzer(fastStochasticLine, stochOverboughtLevel)
                .withRemovingFalsePositive(BUY)
                .analyze();
    }

    private void calculateCCICrossSignals() {
        cciResult = calculateCCIResult();
        cciCrossSignals = findCCICrossSignals();
    }

    private BigDecimal[] calculateCCIResult() {
        return IndicatorResultExtractor.extractIndicatorValues(calculateCCI());
    }

    private CCIResult[] calculateCCI() {
        return new CommodityChannelIndex(buildCCIRequest()).getResult();
    }

    private IndicatorRequest buildCCIRequest() {
        return CCIRequest.builder()
                .originalData(originalData)
                .period(cciPeriod)
                .build();
    }

    private Signal[] findCCICrossSignals() {
        return new StaticLineCrossAnalyzer(cciResult, ZERO).analyze();
    }

    private void findEntries() {
        IntStream.range(0, originalData.length)
                .forEach(this::findEntry);
    }

    private void findEntry(int currentIndex) {
        defineStochCurrentSignal(currentIndex);
        defineCCICurrentSignal(currentIndex);
        defineEntry(currentIndex);
    }

    private void defineStochCurrentSignal(int currentIndex) {
        if (nonNull(stochCrossSignals[currentIndex]) && stochCrossSignals[currentIndex] != NEUTRAL) {
            stochCurrentSignal = stochCrossSignals[currentIndex];
        }
        if (isFastStochasticInExtremeZone(currentIndex)) {
            stochCurrentSignal = null;
        }
    }

    private boolean isFastStochasticInExtremeZone(int currentIndex) {
        return nonNull(fastStochasticLine[currentIndex]) &&
                (fastStochasticLine[currentIndex].compareTo(stochOverboughtLevel) >= 0
                        || fastStochasticLine[currentIndex].compareTo(stochOversoldLevel) <= 0);
    }

    private void defineCCICurrentSignal(int currentIndex) {
        if (nonNull(cciCrossSignals[currentIndex]) && cciCrossSignals[currentIndex] != NEUTRAL) {
            cciCurrentSignal = cciCrossSignals[currentIndex];
        }
        if (isCCIInExtremeZone(currentIndex)) {
            cciCurrentSignal = null;
        }
    }

    private boolean isCCIInExtremeZone(int currentIndex) {
        return nonNull(cciResult[currentIndex]) &&
                (cciResult[currentIndex].compareTo(cciOverboughtLevel) >= 0
                        || cciResult[currentIndex].compareTo(cciOversoldLevel) <= 0);
    }

    private void defineEntry(int currentIndex) {
        defineLongEntry(currentIndex);
        defineShortEntry(currentIndex);
    }

    private void defineLongEntry(int currentIndex) {
        if (isRequired(ENTRY_LONG) && isLongEntry()) {
            result[currentIndex].getPositions().add(ENTRY_LONG);
            resetCurrentIndicatorSignals();
        }
    }

    private boolean isRequired(Position entryLong) {
        return positions.contains(entryLong);
    }

    private boolean isLongEntry() {
        return isEntry(BUY);
    }

    private void defineShortEntry(int currentIndex) {
        if (isRequired(ENTRY_SHORT) && isShortEntry()) {
            result[currentIndex].getPositions().add(ENTRY_SHORT);
            resetCurrentIndicatorSignals();
        }
    }

    private boolean isShortEntry() {
        return isEntry(SELL);
    }

    private boolean isEntry(Signal signal) {
        return nonNull(stochCurrentSignal) && nonNull(cciCurrentSignal)
                && stochCurrentSignal == signal && cciCurrentSignal == signal;
    }

    private void resetCurrentIndicatorSignals() {
        stochCurrentSignal = null;
        cciCurrentSignal = null;
    }

}
