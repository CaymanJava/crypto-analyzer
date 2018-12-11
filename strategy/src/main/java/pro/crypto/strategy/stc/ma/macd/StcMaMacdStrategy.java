package pro.crypto.strategy.stc.ma.macd;

import pro.crypto.analyzer.stc.STCAnalyzer;
import pro.crypto.analyzer.stc.STCAnalyzerRequest;
import pro.crypto.analyzer.stc.STCAnalyzerResult;
import pro.crypto.helper.PeakValleyFinder;
import pro.crypto.indicator.ma.MARequest;
import pro.crypto.indicator.ma.MAResult;
import pro.crypto.indicator.ma.MovingAverageFactory;
import pro.crypto.indicator.macd.MACDRequest;
import pro.crypto.indicator.macd.MACDResult;
import pro.crypto.indicator.macd.MovingAverageConvergenceDivergence;
import pro.crypto.indicator.stc.STCRequest;
import pro.crypto.indicator.stc.STCResult;
import pro.crypto.indicator.stc.SchaffTrendCycle;
import pro.crypto.model.AnalyzerRequest;
import pro.crypto.model.IndicatorRequest;
import pro.crypto.model.IndicatorType;
import pro.crypto.model.Position;
import pro.crypto.model.Strategy;
import pro.crypto.model.StrategyRequest;
import pro.crypto.model.StrategyType;
import pro.crypto.model.tick.PriceType;
import pro.crypto.model.tick.Tick;

import java.util.HashSet;
import java.util.Set;
import java.util.stream.IntStream;

import static java.math.BigDecimal.ZERO;
import static java.util.Arrays.stream;
import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;
import static pro.crypto.model.Position.ENTRY_LONG;
import static pro.crypto.model.Position.ENTRY_SHORT;
import static pro.crypto.model.Position.EXIT_LONG;
import static pro.crypto.model.Position.EXIT_SHORT;
import static pro.crypto.model.Signal.BUY;
import static pro.crypto.model.Signal.SELL;
import static pro.crypto.model.StrategyType.STC_MA_MACD;

public class StcMaMacdStrategy implements Strategy<StcMaMacdResult> {

    private final Tick[] originalData;
    private final PriceType stcPriceType;
    private final int stcPeriod;
    private final int stcShortCycle;
    private final int stcLongCycle;
    private final Double stcOversoldLevel;
    private final Double stcOverboughtLevel;
    private final IndicatorType stcMaType;
    private final IndicatorType maType;
    private final PriceType maPriceType;
    private final int maPeriod;
    private final IndicatorType macdMaType;
    private final PriceType macdPriceType;
    private final int macdFastPeriod;
    private final int macdSlowPeriod;
    private final int macdSignalPeriod;
    private final Set<Position> positions;

    private Boolean[] peaks;
    private Boolean[] valleys;
    private STCAnalyzerResult[] stcAnalyzerResults;
    private MACDResult[] macdResults;
    private MAResult[] maResults;
    private int lastPeakIndex = 0;
    private int lastValleyIndex = 0;
    private StcMaMacdResult[] result;

    public StcMaMacdStrategy(StrategyRequest strategyRequest) {
        StcMaMacdRequest request = (StcMaMacdRequest) strategyRequest;
        this.originalData = request.getOriginalData();
        this.stcPriceType = request.getStcPriceType();
        this.stcPeriod = request.getStcPeriod();
        this.stcShortCycle = request.getStcShortCycle();
        this.stcLongCycle = request.getStcLongCycle();
        this.stcOversoldLevel = request.getStcOversoldLevel();
        this.stcOverboughtLevel = request.getStcOverboughtLevel();
        this.stcMaType = request.getStcMaType();
        this.maType = request.getMaType();
        this.maPriceType = request.getMaPriceType();
        this.maPeriod = request.getMaPeriod();
        this.macdMaType = request.getMacdMaType();
        this.macdPriceType = request.getMacdPriceType();
        this.macdFastPeriod = request.getMacdFastPeriod();
        this.macdSlowPeriod = request.getMacdSlowPeriod();
        this.macdSignalPeriod = request.getMacdSignalPeriod();
        this.positions = request.getPositions();
    }

    @Override
    public StrategyType getType() {
        return STC_MA_MACD;
    }

    @Override
    public void analyze() {
        initResultArray();
        findPeaksAndValleys();
        calculateAndAnalyzeSchaffTrendCycle();
        calculateMovingAverage();
        calculateMovingAverageConvergenceDivergence();
        findEntries();
        findExits();
    }

    @Override
    public StcMaMacdResult[] getResult() {
        if (isNull(result)) {
            analyze();
        }
        return result;
    }

    private void initResultArray() {
        result = stream(originalData)
                .map(originalDatum -> StcMaMacdResult.builder()
                        .time(originalDatum.getTickTime())
                        .positions(new HashSet<>())
                        .build())
                .toArray(StcMaMacdResult[]::new);
    }

    private void findPeaksAndValleys() {
        peaks = PeakValleyFinder.findPeaks(originalData);
        valleys = PeakValleyFinder.findValleys(originalData);
    }

    private void calculateAndAnalyzeSchaffTrendCycle() {
        STCResult[] stcResult = new SchaffTrendCycle(buildSTCRequest()).getResult();
        stcAnalyzerResults = new STCAnalyzer(buildSTCAnalyzerRequest(stcResult)).getResult();
    }

    private IndicatorRequest buildSTCRequest() {
        return STCRequest.builder()
                .originalData(originalData)
                .priceType(stcPriceType)
                .period(stcPeriod)
                .shortCycle(stcShortCycle)
                .longCycle(stcLongCycle)
                .movingAverageType(stcMaType)
                .build();
    }

    private AnalyzerRequest buildSTCAnalyzerRequest(STCResult[] stcResult) {
        return STCAnalyzerRequest.builder()
                .originalData(originalData)
                .indicatorResults(stcResult)
                .oversoldLevel(stcOversoldLevel)
                .overboughtLevel(stcOverboughtLevel)
                .build();
    }

    private void calculateMovingAverage() {
        maResults = MovingAverageFactory.create(buildMARequest()).getResult();
    }

    private IndicatorRequest buildMARequest() {
        return MARequest.builder()
                .originalData(originalData)
                .indicatorType(maType)
                .period(maPeriod)
                .priceType(maPriceType)
                .build();
    }

    private void calculateMovingAverageConvergenceDivergence() {
        macdResults = new MovingAverageConvergenceDivergence(buildMACDRequest()).getResult();
    }

    private IndicatorRequest buildMACDRequest() {
        return MACDRequest.builder()
                .originalData(originalData)
                .movingAverageType(macdMaType)
                .priceType(macdPriceType)
                .fastPeriod(macdFastPeriod)
                .slowPeriod(macdSlowPeriod)
                .signalPeriod(macdSignalPeriod)
                .build();
    }

    private void findEntries() {
        if (isRequired(ENTRY_LONG) || isRequired(ENTRY_SHORT)) {
            IntStream.range(0, originalData.length)
                    .forEach(this::findEntry);
        }
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
        return currentIndex > 0
                && nonNull(stcAnalyzerResults[currentIndex - 1].getSignal())
                && nonNull(macdResults[currentIndex - 1].getIndicatorValue())
                && nonNull(macdResults[currentIndex].getIndicatorValue())
                && nonNull(maResults[currentIndex - 1].getIndicatorValue())
                && nonNull(maResults[currentIndex].getIndicatorValue());
    }

    private void defineEntry(int currentIndex) {
        defineLongEntry(currentIndex);
        defineShortEntry(currentIndex);
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
        return isSTCBuyCondition(currentIndex)
                && isMABuyCondition(currentIndex)
                && isMACDBuyCondition(currentIndex);
    }

    private boolean isSTCBuyCondition(int currentIndex) {
        return stcAnalyzerResults[currentIndex - 1].getSignal() == BUY;
    }

    private boolean isMABuyCondition(int currentIndex) {
        return originalData[currentIndex - 1].getClose()
                .compareTo(maResults[currentIndex - 1].getIndicatorValue()) > 0
                && originalData[currentIndex].getClose()
                .compareTo(maResults[currentIndex].getIndicatorValue()) > 0;
    }

    private boolean isMACDBuyCondition(int currentIndex) {
        return macdResults[currentIndex - 1].getIndicatorValue().compareTo(ZERO) > 0
                && macdResults[currentIndex].getIndicatorValue().compareTo(ZERO) > 0;
    }

    private void defineShortEntry(int currentIndex) {
        if (isRequired(ENTRY_SHORT) && isShortEntry(currentIndex)) {
            result[currentIndex].getPositions().add(ENTRY_SHORT);
            result[currentIndex].setStopLose(originalData[lastPeakIndex].getHigh());
        }
    }

    private boolean isShortEntry(int currentIndex) {
        return isSTCSellCondition(currentIndex)
                && isMASellCondition(currentIndex)
                && isMACDSelCondition(currentIndex);
    }

    private boolean isSTCSellCondition(int currentIndex) {
        return stcAnalyzerResults[currentIndex - 1].getSignal() == SELL;
    }

    private boolean isMASellCondition(int currentIndex) {
        return originalData[currentIndex - 1].getClose()
                .compareTo(maResults[currentIndex - 1].getIndicatorValue()) < 0
                && originalData[currentIndex].getClose()
                .compareTo(maResults[currentIndex].getIndicatorValue()) < 0;
    }

    private boolean isMACDSelCondition(int currentIndex) {
        return macdResults[currentIndex - 1].getIndicatorValue().compareTo(ZERO) < 0
                && macdResults[currentIndex].getIndicatorValue().compareTo(ZERO) < 0;
    }

    private void findExits() {
        if (isRequired(EXIT_LONG) || isRequired(EXIT_SHORT)) {
            IntStream.range(0, originalData.length)
                    .forEach(this::findExit);
        }
    }

    private void findExit(int currentIndex) {
        findLongExit(currentIndex);
        findShortExit(currentIndex);
    }

    private void findLongExit(int currentIndex) {
        if (isRequired(EXIT_LONG) && isLongExit(currentIndex)) {
            result[currentIndex].getPositions().add(EXIT_LONG);
        }
    }

    private boolean isLongExit(int currentIndex) {
        return stcAnalyzerResults[currentIndex].getSignal() == SELL;
    }

    private void findShortExit(int currentIndex) {
        if (isRequired(EXIT_SHORT) && isShortExit(currentIndex)) {
            result[currentIndex].getPositions().add(EXIT_SHORT);
        }
    }

    private boolean isShortExit(int currentIndex) {
        return stcAnalyzerResults[currentIndex].getSignal() == BUY;
    }

}
