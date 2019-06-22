package pro.crypto.strategy.pivot.rsi.macd.ma;

import pro.crypto.helper.DynamicLineCrossAnalyzer;
import pro.crypto.indicator.ma.MARequest;
import pro.crypto.indicator.ma.MAResult;
import pro.crypto.indicator.ma.MovingAverageFactory;
import pro.crypto.indicator.macd.MACDRequest;
import pro.crypto.indicator.macd.MACDResult;
import pro.crypto.indicator.macd.MovingAverageConvergenceDivergence;
import pro.crypto.indicator.pivot.PivotPointFactory;
import pro.crypto.indicator.pivot.PivotRequest;
import pro.crypto.indicator.pivot.PivotResult;
import pro.crypto.indicator.rsi.RSIRequest;
import pro.crypto.indicator.rsi.RSIResult;
import pro.crypto.indicator.rsi.RelativeStrengthIndex;
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

import static java.math.BigDecimal.ZERO;
import static java.util.Arrays.stream;
import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;
import static java.util.Optional.ofNullable;
import static pro.crypto.helper.PriceVolumeExtractor.extractPrices;
import static pro.crypto.model.IndicatorType.FLOOR_PIVOT_POINTS;
import static pro.crypto.model.Position.ENTRY_LONG;
import static pro.crypto.model.Position.ENTRY_SHORT;
import static pro.crypto.model.Signal.BUY;
import static pro.crypto.model.Signal.SELL;
import static pro.crypto.model.StrategyType.PIVOT_RSI_MACD_MA;
import static pro.crypto.model.tick.PriceType.CLOSE;

public class PivotRsiMacdMaStrategy implements Strategy<PivotRsiMacdMaResult> {

    private final Tick[] originalData;
    private final Tick[] oneDayTickData;
    private final IndicatorType rsiMaType;
    private final int rsiPeriod;
    private final BigDecimal rsiSignalLine;
    private final IndicatorType macdMaType;
    private final PriceType macdPriceType;
    private final int macdFastPeriod;
    private final int macdSlowPeriod;
    private final int macdSignalPeriod;
    private final IndicatorType maType;
    private final PriceType maPriceType;
    private final int maPeriod;
    private final Set<Position> positions;

    private PivotResult[] originalDataPivotPoints;
    private BigDecimal[] pivotResults;
    private Signal[] pivotPriceCrossSignals;
    private RSIResult[] rsiResults;
    private MACDResult[] macdResults;
    private MAResult[] maResults;
    private boolean lookingLongEntry;
    private boolean lookingShortEntry;
    private PivotRsiMacdMaResult[] result;

    public PivotRsiMacdMaStrategy(StrategyRequest strategyRequest) {
        PivotRsiMacdMaRequest request = (PivotRsiMacdMaRequest) strategyRequest;
        this.originalData = request.getOriginalData();
        this.oneDayTickData = request.getOneDayTickData();
        this.rsiMaType = request.getRsiMaType();
        this.rsiPeriod = request.getRsiPeriod();
        this.rsiSignalLine = extractRSISignalLine(request);
        this.macdMaType = request.getMacdMaType();
        this.macdPriceType = request.getMacdPriceType();
        this.macdFastPeriod = request.getMacdFastPeriod();
        this.macdSlowPeriod = request.getMacdSlowPeriod();
        this.macdSignalPeriod = request.getMacdSignalPeriod();
        this.maType = request.getMaType();
        this.maPriceType = request.getMaPriceType();
        this.maPeriod = request.getMaPeriod();
        this.positions = request.getPositions();
        checkPositions(positions);
    }

    @Override
    public StrategyType getType() {
        return PIVOT_RSI_MACD_MA;
    }

    @Override
    public void analyze() {
        initResultArray();
        calculatePivotPoints();
        calculateRelativeStrengthIndex();
        calculateMovingAverageDivergenceConvergence();
        calculateMovingAverage();
        findEntries();
    }

    @Override
    public PivotRsiMacdMaResult[] getResult() {
        if (isNull(result)) {
            analyze();
        }
        return result;
    }

    private BigDecimal extractRSISignalLine(PivotRsiMacdMaRequest request) {
        return ofNullable(request.getRsiSignalLine())
                .map(BigDecimal::new)
                .orElse(new BigDecimal(50));
    }

    private void initResultArray() {
        result = stream(originalData)
                .map(originalData -> PivotRsiMacdMaResult.builder()
                        .time(originalData.getTickTime())
                        .positions(new HashSet<>())
                        .build())
                .toArray(PivotRsiMacdMaResult[]::new);
    }

    private void calculatePivotPoints() {
        calculateOneDayPivotPoints();
        extractPivotResults();
        findPivotPriceCrossSignals();
    }

    private void calculateOneDayPivotPoints() {
        originalDataPivotPoints =  PivotPointFactory.create(buildPivotRequest()).getResult();
    }

    private IndicatorRequest buildPivotRequest() {
        return PivotRequest.builder()
                .originalData(oneDayTickData)
                .resultData(originalData)
                .indicatorType(FLOOR_PIVOT_POINTS)
                .build();
    }

    private void extractPivotResults() {
        pivotResults = IntStream.range(0, originalData.length)
                .mapToObj(idx -> originalDataPivotPoints[idx].getPivot())
                .toArray(BigDecimal[]::new);
    }

    private void findPivotPriceCrossSignals() {
        pivotPriceCrossSignals = new DynamicLineCrossAnalyzer(extractPrices(originalData, CLOSE), pivotResults).analyze();
    }

    private void calculateRelativeStrengthIndex() {
        rsiResults = new RelativeStrengthIndex(buildRSIRequest()).getResult();
    }

    private IndicatorRequest buildRSIRequest() {
        return RSIRequest.builder()
                .originalData(originalData)
                .period(rsiPeriod)
                .movingAverageType(rsiMaType)
                .build();
    }

    private void calculateMovingAverageDivergenceConvergence() {
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

    private void calculateMovingAverage() {
        maResults = MovingAverageFactory.create(buildMARequest()).getResult();
    }

    private IndicatorRequest buildMARequest() {
        return MARequest.builder()
                .originalData(originalData)
                .period(maPeriod)
                .priceType(maPriceType)
                .indicatorType(maType)
                .build();
    }

    private void findEntries() {
        if (isRequired(ENTRY_LONG) || isRequired(ENTRY_SHORT)) {
            IntStream.range(0, originalData.length)
                    .forEach(this::findEntry);
        }
    }

    private void findEntry(int currentIndex) {
        if (isPossibleDefineEntry(currentIndex)) {
            defineLookingPosition(currentIndex);
            defineEntry(currentIndex);
        }
    }

    private boolean isPossibleDefineEntry(int currentIndex) {
        return currentIndex > 0
                && nonNull(pivotResults[currentIndex - 1])
                && nonNull(pivotResults[currentIndex])
                && nonNull(originalDataPivotPoints[currentIndex].getFirstResistance())
                && nonNull(originalDataPivotPoints[currentIndex].getSecondResistance())
                && nonNull(originalDataPivotPoints[currentIndex].getFirstSupport())
                && nonNull(originalDataPivotPoints[currentIndex].getSecondSupport())
                && nonNull(rsiResults[currentIndex - 1].getIndicatorValue())
                && nonNull(rsiResults[currentIndex].getIndicatorValue())
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
        if (isRequired(ENTRY_LONG) && lookingLongEntry && isLongEntry(currentIndex)) {
            setLongEntryResult(currentIndex);
        }
    }

    private boolean isRequired(Position entryLong) {
        return positions.contains(entryLong);
    }

    private void defineLookingPosition(int currentIndex) {
        if (pivotPriceCrossSignals[currentIndex - 1] == BUY) {
            defineLookingLongEntry();
        }

        if (pivotPriceCrossSignals[currentIndex - 1] == SELL) {
            defineLookingShortEntry();
        }
    }

    private void defineLookingLongEntry() {
        lookingLongEntry = true;
        lookingShortEntry = false;
    }

    private void defineLookingShortEntry() {
        lookingLongEntry = false;
        lookingShortEntry = true;
    }

    private boolean isLongEntry(int currentIndex) {
        return isPivotBuyCondition(currentIndex)
                && isRSIBuyCondition(currentIndex)
                && isMACDBuyCondition(currentIndex)
                && isMABuyCondition(currentIndex);
    }

    private boolean isPivotBuyCondition(int currentIndex) {
        return pivotPriceCrossSignals[currentIndex] != SELL;
    }

    private boolean isRSIBuyCondition(int currentIndex) {
        return rsiResults[currentIndex - 1].getIndicatorValue().compareTo(rsiSignalLine) > 0
                && rsiResults[currentIndex].getIndicatorValue().compareTo(rsiSignalLine) > 0;
    }

    private boolean isMACDBuyCondition(int currentIndex) {
        return macdResults[currentIndex - 1].getIndicatorValue().compareTo(ZERO) > 0
                && macdResults[currentIndex].getIndicatorValue().compareTo(ZERO) > 0;
    }

    private boolean isMABuyCondition(int currentIndex) {
        return originalData[currentIndex - 1].getClose().compareTo(maResults[currentIndex - 1].getIndicatorValue()) > 0
                && originalData[currentIndex].getClose().compareTo(maResults[currentIndex].getIndicatorValue()) > 0;
    }

    private void setLongEntryResult(int currentIndex) {
        result[currentIndex].getPositions().add(ENTRY_LONG);
        result[currentIndex].setStopLose(originalDataPivotPoints[currentIndex].getFirstSupport());
        result[currentIndex].setFirstTakeProfit(originalDataPivotPoints[currentIndex].getFirstResistance());
        result[currentIndex].setSecondTakeProfit(originalDataPivotPoints[currentIndex].getSecondResistance());
        lookingLongEntry = false;
    }

    private void defineShortEntry(int currentIndex) {
        if (isRequired(ENTRY_SHORT) && lookingShortEntry && isShortEntry(currentIndex)) {
            setShortEntryResult(currentIndex);
        }
    }

    private boolean isShortEntry(int currentIndex) {
        return isPivotSellCondition(currentIndex)
                && isRSISellCondition(currentIndex)
                && isMACDSellCondition(currentIndex)
                && isMASellCondition(currentIndex);
    }

    private boolean isPivotSellCondition(int currentIndex) {
        return pivotPriceCrossSignals[currentIndex] != BUY;
    }

    private boolean isRSISellCondition(int currentIndex) {
        return rsiResults[currentIndex - 1].getIndicatorValue().compareTo(rsiSignalLine) < 0
                && rsiResults[currentIndex].getIndicatorValue().compareTo(rsiSignalLine) < 0;
    }

    private boolean isMACDSellCondition(int currentIndex) {
        return macdResults[currentIndex - 1].getIndicatorValue().compareTo(ZERO) < 0
                && macdResults[currentIndex].getIndicatorValue().compareTo(ZERO) < 0;
    }

    private boolean isMASellCondition(int currentIndex) {
        return originalData[currentIndex - 1].getClose().compareTo(maResults[currentIndex - 1].getIndicatorValue()) < 0
                && originalData[currentIndex].getClose().compareTo(maResults[currentIndex].getIndicatorValue()) < 0;
    }

    private void setShortEntryResult(int currentIndex) {
        result[currentIndex].getPositions().add(ENTRY_SHORT);
        result[currentIndex].setStopLose(originalDataPivotPoints[currentIndex].getFirstResistance());
        result[currentIndex].setFirstTakeProfit(originalDataPivotPoints[currentIndex].getFirstSupport());
        result[currentIndex].setSecondTakeProfit(originalDataPivotPoints[currentIndex].getSecondSupport());
        lookingShortEntry = false;
    }

}
