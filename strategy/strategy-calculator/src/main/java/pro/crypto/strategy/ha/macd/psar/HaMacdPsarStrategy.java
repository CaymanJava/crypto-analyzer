package pro.crypto.strategy.ha.macd.psar;

import pro.crypto.analyzer.psar.PSARAnalyzer;
import pro.crypto.analyzer.psar.PSARAnalyzerResult;
import pro.crypto.indicator.ha.HARequest;
import pro.crypto.indicator.ha.HAResult;
import pro.crypto.indicator.ha.HeikenAshi;
import pro.crypto.indicator.macd.MACDRequest;
import pro.crypto.indicator.macd.MACDResult;
import pro.crypto.indicator.macd.MovingAverageConvergenceDivergence;
import pro.crypto.indicator.psar.PSARRequest;
import pro.crypto.indicator.psar.PSARResult;
import pro.crypto.indicator.psar.ParabolicStopAndReverse;
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
import java.util.stream.Stream;

import static java.math.BigDecimal.ZERO;
import static java.util.Arrays.stream;
import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;
import static pro.crypto.model.analyzer.Signal.BUY;
import static pro.crypto.model.analyzer.Signal.SELL;
import static pro.crypto.model.strategy.Position.ENTRY_LONG;
import static pro.crypto.model.strategy.Position.ENTRY_SHORT;
import static pro.crypto.model.strategy.StrategyType.HA_MACD_PSAR;

/**
 * http://fox-trader.ru/strategiya-torgovli-heiken-ashi-macd.html
 */
public class HaMacdPsarStrategy implements Strategy<HaMacdPsarResult> {

    private final Tick[] originalData;
    private final IndicatorType macdMaType;
    private final PriceType macdPriceType;
    private final int macdFastPeriod;
    private final int macdSlowPeriod;
    private final int macdSignalPeriod;
    private final double psarMinAccelerationFactor;
    private final double psarMaxAccelerationFactor;
    private final Set<Position> positions;

    private HAResult[] haResults;
    private MACDResult[] macdResults;
    private PSARResult[] psarResults;
    private PSARAnalyzerResult[] psarAnalyzerResults;
    private HaMacdPsarResult[] result;

    public HaMacdPsarStrategy(StrategyRequest strategyRequest) {
        HaMacdPsarRequest request = (HaMacdPsarRequest) strategyRequest;
        this.originalData = request.getOriginalData();
        this.macdMaType = request.getMacdMaType();
        this.macdPriceType = request.getMacdPriceType();
        this.macdFastPeriod = request.getMacdFastPeriod();
        this.macdSlowPeriod = request.getMacdSlowPeriod();
        this.macdSignalPeriod = request.getMacdSignalPeriod();
        this.psarMinAccelerationFactor = request.getPsarMinAccelerationFactor();
        this.psarMaxAccelerationFactor = request.getPsarMaxAccelerationFactor();
        this.positions = request.getPositions();
        checkPositions(positions);
    }

    @Override
    public StrategyType getType() {
        return HA_MACD_PSAR;
    }

    @Override
    public void analyze() {
        initResultArray();
        calculateHeikenAshi();
        calculateMovingAverageConvergenceDivergence();
        calculateAndAnalyzeParabolic();
        findEntries();
        addIndicatorsResults();
    }

    @Override
    public HaMacdPsarResult[] getResult() {
        if (isNull(result)) {
            analyze();
        }
        return result;
    }

    private void initResultArray() {
        result = stream(originalData)
                .map(tick -> HaMacdPsarResult.builder()
                        .tick(tick)
                        .positions(new HashSet<>())
                        .build())
                .toArray(HaMacdPsarResult[]::new);
    }

    private void calculateHeikenAshi() {
        haResults = new HeikenAshi(buildHARequest()).getResult();
    }

    private IndicatorRequest buildHARequest() {
        return HARequest.builder()
                .originalData(originalData)
                .build();
    }

    private void calculateMovingAverageConvergenceDivergence() {
        macdResults = new MovingAverageConvergenceDivergence(buildMACDResult()).getResult();
    }

    private IndicatorRequest buildMACDResult() {
        return MACDRequest.builder()
                .originalData(originalData)
                .movingAverageType(macdMaType)
                .priceType(macdPriceType)
                .fastPeriod(macdFastPeriod)
                .slowPeriod(macdSlowPeriod)
                .signalPeriod(macdSignalPeriod)
                .build();
    }

    private void calculateAndAnalyzeParabolic() {
        Tick[] haTicks = builtTicksFromHaResult();
        psarResults = new ParabolicStopAndReverse(buildPSARRequest(haTicks)).getResult();
        psarAnalyzerResults = new PSARAnalyzer(new AnalyzerRequest(haTicks, psarResults)).getResult();
    }

    private IndicatorRequest buildPSARRequest(Tick[] ticks) {
        return PSARRequest.builder()
                .originalData(ticks)
                .minAccelerationFactor(psarMinAccelerationFactor)
                .maxAccelerationFactor(psarMaxAccelerationFactor)
                .build();
    }

    private Tick[] builtTicksFromHaResult() {
        return Stream.of(haResults)
                .map(this::generateTickFromHAResult)
                .toArray(Tick[]::new);
    }

    private Tick generateTickFromHAResult(HAResult haResult) {
        return Tick.builder()
                .open(haResult.getOpen())
                .high(haResult.getHigh())
                .low(haResult.getLow())
                .close(haResult.getClose())
                .tickTime(haResult.getTime())
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
            defineEntry(currentIndex);
        }
    }

    private boolean isPossibleDefineEntry(int currentIndex) {
        return currentIndex > 0
                && nonNull(psarAnalyzerResults[currentIndex - 1].getSignal())
                && nonNull(psarAnalyzerResults[currentIndex].getSignal())
                && nonNull(macdResults[currentIndex - 1].getIndicatorValue())
                && nonNull(macdResults[currentIndex].getIndicatorValue())
                && nonNull(haResults[currentIndex - 1].getClose())
                && nonNull(haResults[currentIndex - 1].getOpen());
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
        return isPRARBuyCondition(currentIndex)
                && isMACDBuyCondition(currentIndex)
                && isHABuyCondition(currentIndex);
    }

    private boolean isPRARBuyCondition(int currentIndex) {
        return psarAnalyzerResults[currentIndex - 1].getSignal() == BUY
                && psarAnalyzerResults[currentIndex].getSignal() != SELL;
    }

    private boolean isMACDBuyCondition(int currentIndex) {
        // This is not a mistake. At this strategy we are waiting for MACD < 0 to buy.
        return macdResults[currentIndex - 1].getIndicatorValue()
                .compareTo(ZERO) < 0;
    }

    private boolean isHABuyCondition(int currentIndex) {
        return haResults[currentIndex - 1].getClose()
                .compareTo(haResults[currentIndex - 1].getOpen()) > 0;
    }

    private void defineShortEntry(int currentIndex) {
        if (isRequired(ENTRY_SHORT) && isShortEntry(currentIndex)) {
            result[currentIndex].getPositions().add(ENTRY_SHORT);
        }
    }

    private boolean isRequired(Position position) {
        return positions.contains(position);
    }

    private boolean isShortEntry(int currentIndex) {
        return isPRARSellCondition(currentIndex)
                && isMACDSellCondition(currentIndex)
                && isHASellCondition(currentIndex);
    }

    private boolean isPRARSellCondition(int currentIndex) {
        return psarAnalyzerResults[currentIndex - 1].getSignal() == SELL
                && psarAnalyzerResults[currentIndex].getSignal() != BUY;
    }

    private boolean isMACDSellCondition(int currentIndex) {
        // This is not a mistake. At this strategy we are waiting for MACD > 0 to sell.
        return macdResults[currentIndex - 1].getIndicatorValue()
                .compareTo(ZERO) > 0;
    }

    private boolean isHASellCondition(int currentIndex) {
        return haResults[currentIndex - 1].getClose()
                .compareTo(haResults[currentIndex - 1].getOpen()) < 0;
    }

    private void addIndicatorsResults() {
        IntStream.range(0, result.length)
                .forEach(this::addIndicatorsResult);
    }

    private void addIndicatorsResult(int index) {
        HaMacdPsarResult haMacdPsarResult = result[index];
        addHaResult(haResults[index], haMacdPsarResult);
        addMacdResult(macdResults[index], haMacdPsarResult);
        addPsarResult(psarResults[index], haMacdPsarResult);
    }

    private void addHaResult(HAResult haResult, HaMacdPsarResult haMacdPsarResult) {
        haMacdPsarResult.setHaOpen(haResult.getOpen());
        haMacdPsarResult.setHaHigh(haResult.getHigh());
        haMacdPsarResult.setHaLow(haResult.getLow());
        haMacdPsarResult.setHaClose(haResult.getClose());
    }

    private void addMacdResult(MACDResult macdResult, HaMacdPsarResult haMacdPsarResult) {
        haMacdPsarResult.setMacdValue(macdResult.getIndicatorValue());
        haMacdPsarResult.setMacdSignalLineValue(macdResult.getSignalLineValue());
        haMacdPsarResult.setMacdBarChartValue(macdResult.getBarChartValue());
    }

    private void addPsarResult(PSARResult psarResult, HaMacdPsarResult haMacdPsarResult) {
        haMacdPsarResult.setPsarValue(psarResult.getIndicatorValue());
    }

}
