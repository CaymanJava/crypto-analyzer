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
import pro.crypto.model.*;
import pro.crypto.model.tick.PriceType;
import pro.crypto.model.tick.Tick;

import java.util.HashSet;
import java.util.Set;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import static java.math.BigDecimal.ZERO;
import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;
import static pro.crypto.model.Position.ENTRY_LONG;
import static pro.crypto.model.Position.ENTRY_SHORT;
import static pro.crypto.model.Signal.BUY;
import static pro.crypto.model.Signal.SELL;
import static pro.crypto.model.StrategyType.HA_MACD_PSAR;

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
        calculateMovingAverageDivergenceConvergence();
        calculateAndAnalyzeParabolic();
        findEntries();
    }

    @Override
    public HaMacdPsarResult[] getResult() {
        if (isNull(result)) {
            analyze();
        }
        return result;
    }

    private void initResultArray() {
        result = IntStream.range(0, originalData.length)
                .mapToObj(idx -> HaMacdPsarResult.builder()
                        .time(originalData[idx].getTickTime())
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

    private void calculateMovingAverageDivergenceConvergence() {
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
        PSARResult[] haResults = new ParabolicStopAndReverse(buildPSARRequest()).getResult();
        psarAnalyzerResults = new PSARAnalyzer(new AnalyzerRequest(originalData, haResults)).getResult();
    }

    private IndicatorRequest buildPSARRequest() {
        return PSARRequest.builder()
                .originalData(builtTicksFromHaResult())
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
        if (positions.contains(ENTRY_LONG) || positions.contains(ENTRY_SHORT)) {
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
        if (positions.contains(ENTRY_LONG) && isLongEntry(currentIndex)) {
            result[currentIndex].getPositions().add(ENTRY_LONG);
        }

        if (positions.contains(ENTRY_SHORT) && isShortEntry(currentIndex)) {
            result[currentIndex].getPositions().add(ENTRY_SHORT);
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

}
