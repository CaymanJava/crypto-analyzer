package pro.crypto.strategy.macd.cci;

import pro.crypto.helper.DynamicLineCrossAnalyzer;
import pro.crypto.helper.SignalArrayMerger;
import pro.crypto.helper.StaticLineCrossAnalyzer;
import pro.crypto.indicator.cci.CCIRequest;
import pro.crypto.indicator.cci.CommodityChannelIndex;
import pro.crypto.indicator.macd.MACDRequest;
import pro.crypto.indicator.macd.MACDResult;
import pro.crypto.indicator.macd.MovingAverageConvergenceDivergence;
import pro.crypto.model.Strategy;
import pro.crypto.model.analyzer.Signal;
import pro.crypto.model.indicator.IndicatorType;
import pro.crypto.model.strategy.Position;
import pro.crypto.model.strategy.StrategyType;
import pro.crypto.model.tick.PriceType;
import pro.crypto.model.tick.Tick;
import pro.crypto.request.IndicatorRequest;
import pro.crypto.request.StrategyRequest;

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
import static pro.crypto.helper.IndicatorResultExtractor.extractIndicatorValues;
import static pro.crypto.helper.IndicatorResultExtractor.extractSignalLineValues;
import static pro.crypto.model.analyzer.Signal.BUY;
import static pro.crypto.model.analyzer.Signal.SELL;
import static pro.crypto.model.strategy.Position.ENTRY_LONG;
import static pro.crypto.model.strategy.Position.ENTRY_SHORT;
import static pro.crypto.model.strategy.Position.EXIT_LONG;
import static pro.crypto.model.strategy.Position.EXIT_SHORT;
import static pro.crypto.model.strategy.StrategyType.MACD_CCI;

public class MacdCciStrategy implements Strategy<MacdCciResult> {

    private final Tick[] originalData;
    private final IndicatorType macdMaType;
    private final PriceType macdPriceType;
    private final int macdFastPeriod;
    private final int macdSlowPeriod;
    private final int macdSignalPeriod;
    private final int cciPeriod;
    private final BigDecimal cciOversoldLevel;
    private final BigDecimal cciOverboughtLevel;
    private final Set<Position> positions;

    private MACDResult[] macdResults;
    private BigDecimal[] cciMacdResults;

    private Signal[] cciEntrySignals;
    private Signal[] exitSignals;
    private MacdCciResult[] result;

    public MacdCciStrategy(StrategyRequest strategyRequest) {
        MacdCciRequest request = (MacdCciRequest) strategyRequest;
        this.originalData = request.getOriginalData();
        this.macdMaType = request.getMacdMaType();
        this.macdPriceType = request.getMacdPriceType();
        this.macdFastPeriod = request.getMacdFastPeriod();
        this.macdSlowPeriod = request.getMacdSlowPeriod();
        this.macdSignalPeriod = request.getMacdSignalPeriod();
        this.cciPeriod = request.getCciPeriod();
        this.cciOversoldLevel = extractCCIOversoldLevel(request);
        this.cciOverboughtLevel = extractCCIOverboughtLevel(request);
        this.positions = request.getPositions();
        checkPositions(positions);
    }

    @Override
    public StrategyType getType() {
        return MACD_CCI;
    }

    @Override
    public void analyze() {
        initResultArray();
        calculateMovingAverageConvergenceDivergence();
        calculateMACDCommodityChannelIndex();
        findCCIEntrySignals();
        findExitSignals();
        findEntries();
        findExits();
        addIndicatorsResults();
    }

    @Override
    public MacdCciResult[] getResult() {
        if (isNull(result)) {
            analyze();
        }
        return result;
    }

    private BigDecimal extractCCIOversoldLevel(MacdCciRequest request) {
        return ofNullable(request.getCciOversoldLevel())
                .map(BigDecimal::new)
                .orElse(new BigDecimal(-100));
    }

    private BigDecimal extractCCIOverboughtLevel(MacdCciRequest request) {
        return ofNullable(request.getCciOverboughtLevel())
                .map(BigDecimal::new)
                .orElse(new BigDecimal(100));
    }

    private void initResultArray() {
        result = stream(originalData)
                .map(tick -> MacdCciResult.builder()
                        .tick(tick)
                        .positions(new HashSet<>())
                        .build())
                .toArray(MacdCciResult[]::new);
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

    private void calculateMACDCommodityChannelIndex() {
        cciMacdResults = new BigDecimal[originalData.length];
        BigDecimal[] macdCciValues = extractIndicatorValues(new CommodityChannelIndex(buildCCIRequest()).getResult());
        System.arraycopy(macdCciValues, 0, cciMacdResults, macdSlowPeriod - 1, macdCciValues.length);
    }

    private IndicatorRequest buildCCIRequest() {
        return CCIRequest.builder()
                .originalData(buildTicksFromMacd())
                .period(cciPeriod)
                .build();
    }

    private Tick[] buildTicksFromMacd() {
        return Stream.of(macdResults)
                .filter(macdResult -> nonNull(macdResult.getIndicatorValue()))
                .map(this::buildTickFromMacd)
                .toArray(Tick[]::new);
    }

    private Tick buildTickFromMacd(MACDResult macdResult) {
        return Tick.builder()
                .tickTime(macdResult.getTime())
                .high(macdResult.getIndicatorValue())
                .low(macdResult.getIndicatorValue())
                .close(macdResult.getIndicatorValue())
                .build();
    }

    private void findEntries() {
        if (isRequired(ENTRY_LONG) || isRequired(ENTRY_SHORT)) {
            IntStream.range(0, originalData.length)
                    .forEach(this::findEntry);
        }
    }

    private void findCCIEntrySignals() {
        Signal[] buySignals = findCCILongEntrySignals();
        Signal[] sellSignals = findCCIShortEntrySignals();
        cciEntrySignals = SignalArrayMerger.mergeSignals(buySignals, sellSignals);
    }

    private Signal[] findCCIShortEntrySignals() {
        return new StaticLineCrossAnalyzer(cciMacdResults, cciOversoldLevel)
                .withRemovingFalsePositive(BUY)
                .analyze();
    }

    private Signal[] findCCILongEntrySignals() {
        return new StaticLineCrossAnalyzer(cciMacdResults, cciOverboughtLevel)
                .withRemovingFalsePositive(SELL)
                .analyze();
    }

    private void findEntry(int currentIndex) {
        if (isPossibleDefineEntry(currentIndex)) {
            defineEntry(currentIndex);
        }
    }

    private boolean isPossibleDefineEntry(int currentIndex) {
        return currentIndex > 0
                && nonNull(cciEntrySignals[currentIndex - 1])
                && nonNull(exitSignals[currentIndex - 1])
                && nonNull(exitSignals[currentIndex])
                && nonNull(macdResults[currentIndex - 1].getIndicatorValue())
                && nonNull(macdResults[currentIndex].getIndicatorValue());
    }

    private void defineEntry(int currentIndex) {
        defineLongEntry(currentIndex);
        defineShortEntry(currentIndex);
    }

    private void defineLongEntry(int currentIndex) {
        if (isRequired(ENTRY_LONG) && isLongEntry(currentIndex)) {
            result[currentIndex].getPositions().add(ENTRY_LONG);
        }
    }

    private boolean isLongEntry(int currentIndex) {
        return isCCILongEntryCondition(currentIndex)
                && isMACDLongEntryCondition(currentIndex);
    }

    private boolean isCCILongEntryCondition(int currentIndex) {
        return cciEntrySignals[currentIndex - 1] == BUY
                && exitSignals[currentIndex - 1] != SELL
                && exitSignals[currentIndex] != SELL;
    }

    private boolean isMACDLongEntryCondition(int currentIndex) {
        return macdResults[currentIndex - 1].getIndicatorValue().compareTo(ZERO) > 0
                && macdResults[currentIndex].getIndicatorValue().compareTo(ZERO) > 0;
    }

    private void defineShortEntry(int currentIndex) {
        if (isRequired(EXIT_SHORT) && isShortEntry(currentIndex)) {
            result[currentIndex].getPositions().add(ENTRY_SHORT);
        }
    }

    private boolean isShortEntry(int currentIndex) {
        return isCCIShortEntryCondition(currentIndex)
                && isMACDShortEntryCondition(currentIndex);
    }

    private boolean isCCIShortEntryCondition(int currentIndex) {
        return cciEntrySignals[currentIndex - 1] == SELL
                && exitSignals[currentIndex - 1] != BUY
                && exitSignals[currentIndex] != BUY;
    }

    private boolean isMACDShortEntryCondition(int currentIndex) {
        return macdResults[currentIndex - 1].getIndicatorValue().compareTo(ZERO) < 0
                && macdResults[currentIndex].getIndicatorValue().compareTo(ZERO) < 0;
    }

    private void findExits() {
        if (isRequired(EXIT_LONG) || isRequired(EXIT_SHORT)) {
            IntStream.range(0, originalData.length)
                    .forEach(this::findExit);
        }
    }

    private void findExitSignals() {
        Signal[] securityLevelsCrossSignals = findSecurityLevelsCrossSignals();
        Signal[] indicatorsCrossSignals = findIndicatorsCrossSignals();
        exitSignals = SignalArrayMerger.mergeSignals(securityLevelsCrossSignals, indicatorsCrossSignals);
    }

    private Signal[] findSecurityLevelsCrossSignals() {
        Signal[] buySignals = findExitShortCciSignals();
        Signal[] sellSignals = findExitLongCciSignals();
        return SignalArrayMerger.mergeSignals(buySignals, sellSignals);
    }

    private Signal[] findExitShortCciSignals() {
        return new StaticLineCrossAnalyzer(cciMacdResults, cciOversoldLevel)
                .withRemovingFalsePositive(SELL)
                .analyze();
    }

    private Signal[] findExitLongCciSignals() {
        return new StaticLineCrossAnalyzer(cciMacdResults, cciOverboughtLevel)
                .withRemovingFalsePositive(BUY)
                .analyze();
    }

    private Signal[] findIndicatorsCrossSignals() {
        return new DynamicLineCrossAnalyzer(cciMacdResults, extractSignalLineValues(macdResults))
                .analyze();
    }

    private void findExit(int currentIndex) {
        if (isRequired(EXIT_LONG) && isLongExit(currentIndex)) {
            result[currentIndex].getPositions().add(EXIT_LONG);
        }

        if (isRequired(EXIT_SHORT) && isShortExit(currentIndex)) {
            result[currentIndex].getPositions().add(EXIT_SHORT);
        }
    }

    private boolean isRequired(Position entryLong) {
        return positions.contains(entryLong);
    }

    private boolean isLongExit(int currentIndex) {
        return exitSignals[currentIndex] == SELL;
    }

    private boolean isShortExit(int currentIndex) {
        return exitSignals[currentIndex] == BUY;
    }

    private void addIndicatorsResults() {
        IntStream.range(0, result.length)
                .forEach(this::addIndicatorsResult);
    }

    private void addIndicatorsResult(int index) {
        MacdCciResult macdCciResult = result[index];
        addMacdResult(macdResults[index], macdCciResult);
        addCciMacdResult(cciMacdResults[index], macdCciResult);
    }

    private void addMacdResult(MACDResult macdResult, MacdCciResult macdCciResult) {
        macdCciResult.setMacdValue(macdResult.getIndicatorValue());
        macdCciResult.setMacdSignalLineValue(macdResult.getSignalLineValue());
        macdCciResult.setMacdBarChartValue(macdResult.getBarChartValue());
    }

    private void addCciMacdResult(BigDecimal cciMacdResult, MacdCciResult macdCciResult) {
        macdCciResult.setCciMacdValue(cciMacdResult);
    }

}
