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
import pro.crypto.model.Strategy;
import pro.crypto.model.indicator.IndicatorType;
import pro.crypto.model.strategy.Position;
import pro.crypto.model.strategy.StrategyType;
import pro.crypto.model.tick.PriceType;
import pro.crypto.model.tick.Tick;
import pro.crypto.request.IndicatorRequest;
import pro.crypto.request.StrategyRequest;

import java.util.HashSet;
import java.util.Set;
import java.util.stream.IntStream;

import static java.math.BigDecimal.ZERO;
import static java.util.Arrays.stream;
import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;
import static pro.crypto.model.analyzer.Signal.BUY;
import static pro.crypto.model.analyzer.Signal.SELL;
import static pro.crypto.model.strategy.Position.ENTRY_LONG;
import static pro.crypto.model.strategy.Position.ENTRY_SHORT;
import static pro.crypto.model.strategy.Position.EXIT_LONG;
import static pro.crypto.model.strategy.Position.EXIT_SHORT;
import static pro.crypto.model.strategy.StrategyType.STC_MA_MACD;

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

    private STCResult[] stcResults;
    private MACDResult[] macdResults;
    private MAResult[] maResults;

    private STCAnalyzerResult[] stcAnalyzerResults;

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
        addIndicatorsResults();
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
                .map(tick -> StcMaMacdResult.builder()
                        .tick(tick)
                        .positions(new HashSet<>())
                        .build())
                .toArray(StcMaMacdResult[]::new);
    }

    private void findPeaksAndValleys() {
        peaks = PeakValleyFinder.findPeaks(originalData);
        valleys = PeakValleyFinder.findValleys(originalData);
    }

    private void calculateAndAnalyzeSchaffTrendCycle() {
        stcResults = new SchaffTrendCycle(buildSTCRequest()).getResult();
        stcAnalyzerResults = new STCAnalyzer(buildSTCAnalyzerRequest(stcResults)).getResult();
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

    private void addIndicatorsResults() {
        IntStream.range(0, result.length)
                .forEach(this::addIndicatorsResult);
    }

    private void addIndicatorsResult(int index) {
        StcMaMacdResult stcMaMacdResult = result[index];
        addStcResult(stcResults[index], stcMaMacdResult);
        addMacdResult(macdResults[index], stcMaMacdResult);
        addMaResult(maResults[index], stcMaMacdResult);
    }

    private void addStcResult(STCResult stcResult, StcMaMacdResult stcMaMacdResult) {
        stcMaMacdResult.setStcResult(stcResult.getIndicatorValue());
    }

    private void addMacdResult(MACDResult macdResult, StcMaMacdResult stcMaMacdResult) {
        stcMaMacdResult.setMacdValue(macdResult.getIndicatorValue());
        stcMaMacdResult.setMacdSignalLineValue(macdResult.getSignalLineValue());
        stcMaMacdResult.setMacdBarChartValue(macdResult.getBarChartValue());
    }

    private void addMaResult(MAResult maResult, StcMaMacdResult stcMaMacdResult) {
        stcMaMacdResult.setMaResult(maResult.getIndicatorValue());
    }

}
