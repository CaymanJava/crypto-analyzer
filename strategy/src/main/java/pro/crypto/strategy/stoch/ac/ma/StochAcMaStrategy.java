package pro.crypto.strategy.stoch.ac.ma;

import pro.crypto.helper.PeakValleyFinder;
import pro.crypto.helper.SignalArrayMerger;
import pro.crypto.helper.StaticLineCrossAnalyzer;
import pro.crypto.indicator.ac.ACRequest;
import pro.crypto.indicator.ac.ACResult;
import pro.crypto.indicator.ac.AccelerationDecelerationOscillator;
import pro.crypto.indicator.ma.MARequest;
import pro.crypto.indicator.ma.MAResult;
import pro.crypto.indicator.ma.MovingAverageFactory;
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
import pro.crypto.model.tick.PriceType;
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
import static pro.crypto.model.Signal.SELL;
import static pro.crypto.model.StrategyType.STOCH_AC_MA;

public class StochAcMaStrategy implements Strategy<StochAcMaResult> {

    private final static int ALLOWABLE_CANDLES_AFTER_FIRST_SIGNAL = 2;
    private final static int ALLOWABLE_CANDLES_AFTER_SECOND_SIGNAL = 4;

    private final Tick[] originalData;
    private final IndicatorType stochMovingAverageType;
    private final int stochFastPeriod;
    private final int stochSlowPeriod;
    private final BigDecimal stochOversoldLevel;
    private final BigDecimal stochOverboughtLevel;
    private final int acSlowPeriod;
    private final int acFastPeriod;
    private final int acSmoothedPeriod;
    private final int maPeriod;
    private final IndicatorType movingAverageType;
    private final PriceType maPriceType;
    private final Set<Position> positions;

    private Boolean[] peaks;
    private Boolean[] valleys;
    private ACResult[] acResults;
    private Signal[] fastStochLineCrossSignals;
    private MAResult[] maResults;
    private int entryLongStep = 1;
    private int entryShortStep = 1;
    private int candlesAfterLastBuySignal = 0;
    private int candlesAfterLastSellSignal = 0;
    private int lastPeakIndex = 0;
    private int lastValleyIndex = 0;
    private StochAcMaResult[] result;

    public StochAcMaStrategy(StrategyRequest strategyRequest) {
        StochAcMaRequest request = (StochAcMaRequest) strategyRequest;
        this.originalData = request.getOriginalData();
        this.stochMovingAverageType = request.getStochMovingAverageType();
        this.stochFastPeriod = request.getStochFastPeriod();
        this.stochSlowPeriod = request.getStochSlowPeriod();
        this.stochOversoldLevel = extractStochOversoldLevel(request);
        this.stochOverboughtLevel = extractStochOverboughtLevel(request);
        this.acSlowPeriod = request.getAcSlowPeriod();
        this.acFastPeriod = request.getAcFastPeriod();
        this.acSmoothedPeriod = request.getAcSmoothedPeriod();
        this.maPeriod = request.getMaPeriod();
        this.movingAverageType = request.getMovingAverageType();
        this.maPriceType = request.getMaPriceType();
        this.positions = request.getPositions();
        checkPositions(positions);
    }

    @Override
    public StrategyType getType() {
        return STOCH_AC_MA;
    }

    @Override
    public void analyze() {
        initResultArray();
        findPeaksAndValleys();
        calculateAccelerationDecelerationOscillator();
        calculateACStochasticFastLineCrossSignals();
        calculateMovingAverage();
        findEntries();
    }

    @Override
    public StochAcMaResult[] getResult() {
        if (isNull(result)) {
            analyze();
        }
        return result;
    }

    private BigDecimal extractStochOversoldLevel(StochAcMaRequest request) {
        return ofNullable(request.getStochOversoldLevel())
                .map(BigDecimal::new)
                .orElse(new BigDecimal(20));
    }

    private BigDecimal extractStochOverboughtLevel(StochAcMaRequest request) {
        return ofNullable(request.getStochOverboughtLevel())
                .map(BigDecimal::new)
                .orElse(new BigDecimal(80));
    }

    private void initResultArray() {
        result = stream(originalData)
                .map(originalDatum -> StochAcMaResult.builder()
                        .time(originalDatum.getTickTime())
                        .positions(new HashSet<>())
                        .build())
                .toArray(StochAcMaResult[]::new);
    }

    private void findPeaksAndValleys() {
        peaks = PeakValleyFinder.findPeaks(originalData);
        valleys = PeakValleyFinder.findValleys(originalData);
    }

    private void calculateAccelerationDecelerationOscillator() {
        acResults = new AccelerationDecelerationOscillator(buildACRequest()).getResult();
    }

    private IndicatorRequest buildACRequest() {
        return ACRequest.builder()
                .originalData(originalData)
                .slowPeriod(acSlowPeriod)
                .fastPeriod(acFastPeriod)
                .smoothedPeriod(acSmoothedPeriod)
                .build();
    }

    private void calculateACStochasticFastLineCrossSignals() {
        BigDecimal[] fastStochLine = new BigDecimal[originalData.length];
        BigDecimal[] fastStochasticResults = calculateFastStochAC();
        System.arraycopy(fastStochasticResults, 0, fastStochLine, acSlowPeriod + acSmoothedPeriod - 2, fastStochasticResults.length);
        Signal[] oversoldSignals = findStochOversoldSignals(fastStochLine);
        Signal[] overboughtSignals = findStochOverboughtSignals(fastStochLine);
        fastStochLineCrossSignals = SignalArrayMerger.mergeSignals(oversoldSignals, overboughtSignals);
    }

    private BigDecimal[] calculateFastStochAC() {
        return Stream.of(calculateStochasticAC())
                .map(StochResult::getFastStochastic)
                .toArray(BigDecimal[]::new);
    }

    private StochResult[] calculateStochasticAC() {
        return new StochasticOscillator(buildACStochRequest()).getResult();
    }

    private IndicatorRequest buildACStochRequest() {
        return StochRequest.builder()
                .originalData(buildTicksFromAcResult())
                .movingAverageType(stochMovingAverageType)
                .fastStochPeriod(stochFastPeriod)
                .slowStochPeriod(stochSlowPeriod)
                .build();
    }

    private Tick[] buildTicksFromAcResult() {
        return IntStream.range(0, acResults.length)
                .filter(idx -> nonNull(acResults[idx].getIndicatorValue()))
                .mapToObj(this::buildTickFromAcResult)
                .toArray(Tick[]::new);
    }

    private Tick buildTickFromAcResult(int currentIndex) {
        return Tick.builder()
                .tickTime(originalData[currentIndex].getTickTime())
                .high(acResults[currentIndex].getIndicatorValue())
                .low(acResults[currentIndex].getIndicatorValue())
                .close(acResults[currentIndex].getIndicatorValue())
                .build();
    }

    private Signal[] findStochOversoldSignals(BigDecimal[] fastStochLine) {
        return new StaticLineCrossAnalyzer(fastStochLine, stochOversoldLevel)
                .withRemovingFalsePositive(SELL)
                .analyze();
    }

    private Signal[] findStochOverboughtSignals(BigDecimal[] fastStochLine) {
        return new StaticLineCrossAnalyzer(fastStochLine, stochOverboughtLevel)
                .withRemovingFalsePositive(BUY)
                .analyze();
    }

    private void calculateMovingAverage() {
        maResults = MovingAverageFactory.create(buildMARequest()).getResult();
    }

    private IndicatorRequest buildMARequest() {
        return MARequest.builder()
                .originalData(originalData)
                .period(maPeriod)
                .indicatorType(movingAverageType)
                .priceType(maPriceType)
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
        return nonNull(fastStochLineCrossSignals[currentIndex])
                && nonNull(acResults[currentIndex].getIndicatorValue())
                && nonNull(acResults[currentIndex].getIncreased())
                && nonNull(maResults[currentIndex].getIndicatorValue());
    }

    private void defineEntry(int currentIndex) {
        if (isRequired(ENTRY_LONG)) {
            defineLongEntry(currentIndex);
        }
        if (isRequired(ENTRY_SHORT)) {
            defineShortEntry(currentIndex);
        }
    }

    private boolean isRequired(Position entryLong) {
        return positions.contains(entryLong);
    }

    private void defineLongEntry(int currentIndex) {
        switch (entryLongStep) {
            case 1:
                findStochLongEntry(currentIndex);
                break;
            case 2:
                findACLongEntry(currentIndex);
                break;
            case 3:
                findMALongEntry(currentIndex);
                break;
            default:
                entryLongStep = 1;
                break;
        }
    }

    private void findStochLongEntry(int currentIndex) {
        if (fastStochLineCrossSignals[currentIndex] == BUY) {
            entryLongStep = 2;
            entryShortStep = 1;
            findACLongEntry(currentIndex);
        }
    }

    private void findACLongEntry(int currentIndex) {
        if (isACBuyCondition(acResults[currentIndex])) {
            entryLongStep = 3;
            candlesAfterLastBuySignal = 0;
            findMALongEntry(currentIndex);
        } else {
            if (++candlesAfterLastBuySignal > ALLOWABLE_CANDLES_AFTER_FIRST_SIGNAL) {
                entryLongStep = 1;
                candlesAfterLastBuySignal = 0;
            }
        }
    }

    private boolean isACBuyCondition(ACResult acResult) {
        return acResult.getIncreased() && acResult.getIndicatorValue().compareTo(ZERO) > 0;
    }

    private void findMALongEntry(int currentIndex) {
        if (isMABuyCondition(currentIndex)) {
            result[currentIndex].getPositions().add(ENTRY_LONG);
            result[currentIndex].setStopLose(originalData[lastValleyIndex].getLow());
            entryLongStep = 1;
            entryShortStep = 1;
        } else {
            if (++candlesAfterLastBuySignal > ALLOWABLE_CANDLES_AFTER_SECOND_SIGNAL) {
                entryLongStep = 1;
                candlesAfterLastBuySignal = 0;
            }
        }
    }

    private boolean isMABuyCondition(int currentIndex) {
        return maResults[currentIndex].getIndicatorValue()
                .compareTo(originalData[currentIndex].getClose()) < 0;
    }

    private void defineShortEntry(int currentIndex) {
        switch (entryShortStep) {
            case 1:
                findStochShortEntry(currentIndex);
                break;
            case 2:
                findACShortEntry(currentIndex);
                break;
            case 3:
                findMAShortEntry(currentIndex);
                break;
            default:
                entryShortStep = 1;
                break;
        }
    }

    private void findStochShortEntry(int currentIndex) {
        if (fastStochLineCrossSignals[currentIndex] == SELL) {
            entryShortStep = 2;
            entryLongStep = 1;
            findACShortEntry(currentIndex);
        }
    }

    private void findACShortEntry(int currentIndex) {
        if (isACSellCondition(acResults[currentIndex])) {
            entryShortStep = 3;
            candlesAfterLastSellSignal = 0;
            findMAShortEntry(currentIndex);
        } else {
            if (++candlesAfterLastSellSignal > ALLOWABLE_CANDLES_AFTER_FIRST_SIGNAL) {
                entryShortStep = 1;
                candlesAfterLastSellSignal = 0;
            }
        }
    }

    private boolean isACSellCondition(ACResult acResult) {
        return !acResult.getIncreased() && acResult.getIndicatorValue().compareTo(ZERO) < 0;
    }

    private void findMAShortEntry(int currentIndex) {
        if (isMASellCondition(currentIndex)) {
            result[currentIndex].getPositions().add(ENTRY_SHORT);
            result[currentIndex].setStopLose(originalData[lastPeakIndex].getHigh());
            entryShortStep = 1;
            entryLongStep = 1;
        } else {
            if (++candlesAfterLastSellSignal > ALLOWABLE_CANDLES_AFTER_SECOND_SIGNAL) {
                entryShortStep = 1;
                candlesAfterLastSellSignal = 0;
            }
        }
    }

    private boolean isMASellCondition(int currentIndex) {
        return maResults[currentIndex].getIndicatorValue()
                .compareTo(originalData[currentIndex].getClose()) > 0;
    }

}
