package pro.crypto.strategy.lrsi.ma.psar;

import pro.crypto.analyzer.psar.PSARAnalyzer;
import pro.crypto.analyzer.psar.PSARAnalyzerResult;
import pro.crypto.analyzer.rsi.RSIAnalyzer;
import pro.crypto.analyzer.rsi.RSIAnalyzerRequest;
import pro.crypto.analyzer.rsi.RSIAnalyzerResult;
import pro.crypto.indicator.ma.MARequest;
import pro.crypto.indicator.ma.MAResult;
import pro.crypto.indicator.ma.MovingAverageFactory;
import pro.crypto.indicator.psar.PSARRequest;
import pro.crypto.indicator.psar.PSARResult;
import pro.crypto.indicator.psar.ParabolicStopAndReverse;
import pro.crypto.indicator.rsi.LRSIRequest;
import pro.crypto.indicator.rsi.LaguerreRelativeStrengthIndex;
import pro.crypto.indicator.rsi.RSIResult;
import pro.crypto.model.*;
import pro.crypto.model.tick.PriceType;
import pro.crypto.model.tick.Tick;

import java.util.HashSet;
import java.util.Set;
import java.util.stream.IntStream;

import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;
import static pro.crypto.model.Position.*;
import static pro.crypto.model.SecurityLevel.OVERBOUGHT;
import static pro.crypto.model.SecurityLevel.OVERSOLD;
import static pro.crypto.model.Signal.BUY;
import static pro.crypto.model.Signal.SELL;
import static pro.crypto.model.StrategyType.LRSI_MA_PSAR;

public class LrsiMaPsarStrategy implements Strategy<LrsiMaPsarResult> {

    private final Tick[] originalData;
    private final double lrsiGamma;
    private final Double lrsiOversoldLevel;
    private final Double lrsiOverboughtLevel;
    private final IndicatorType maType;
    private final PriceType maPriceType;
    private final int maPeriod;
    private final double psarMinAccelerationFactor;
    private final double psarMaxAccelerationFactor;
    private final Set<Position> positions;

    private boolean lookingLongEntry;
    private boolean lookingShortEntry;
    private RSIAnalyzerResult[] lrsiAnalyzerResults;
    private MAResult[] maResults;
    private PSARResult[] psarResults;
    private PSARAnalyzerResult[] psarAnalyzerResults;
    private LrsiMaPsarResult[] result;

    public LrsiMaPsarStrategy(StrategyRequest strategyRequest) {
        LrsiMaPsarRequest request = (LrsiMaPsarRequest) strategyRequest;
        this.originalData = request.getOriginalData();
        this.lrsiGamma = request.getLrsiGamma();
        this.lrsiOversoldLevel = request.getLrsiOversoldLevel();
        this.lrsiOverboughtLevel = request.getLrsiOverboughtLevel();
        this.maType = request.getMaType();
        this.maPriceType = request.getMaPriceType();
        this.maPeriod = request.getMaPeriod();
        this.psarMinAccelerationFactor = request.getPsarMinAccelerationFactor();
        this.psarMaxAccelerationFactor = request.getPsarMaxAccelerationFactor();
        this.positions = request.getPositions();
        checkPositions(positions);
    }

    @Override
    public StrategyType getType() {
        return LRSI_MA_PSAR;
    }

    @Override
    public void analyze() {
        initResultArray();
        calculateAndAnalyzerLaguerreRelativeStrengthIndex();
        calculateMovingAverage();
        calculateParabolicStopAndReverse();
        findEntries();
        findExits();
    }

    @Override
    public LrsiMaPsarResult[] getResult() {
        if (isNull(result)) {
            analyze();
        }
        return result;
    }

    private void initResultArray() {
        result = IntStream.range(0, originalData.length)
                .mapToObj(idx -> LrsiMaPsarResult.builder()
                        .time(originalData[idx].getTickTime())
                        .positions(new HashSet<>())
                        .build())
                .toArray(LrsiMaPsarResult[]::new);
    }

    private void calculateAndAnalyzerLaguerreRelativeStrengthIndex() {
        RSIResult[] lrsiResults = new LaguerreRelativeStrengthIndex(buildLRSIRequest()).getResult();
        lrsiAnalyzerResults = new RSIAnalyzer(buildLRSIAnalyzerRequest(lrsiResults)).getResult();
    }

    private IndicatorRequest buildLRSIRequest() {
        return LRSIRequest.builder()
                .originalData(originalData)
                .gamma(lrsiGamma)
                .build();
    }

    private AnalyzerRequest buildLRSIAnalyzerRequest(RSIResult[] lrsiResults) {
        return RSIAnalyzerRequest.builder()
                .originalData(originalData)
                .indicatorResults(lrsiResults)
                .oversoldLevel(lrsiOversoldLevel)
                .overboughtLevel(lrsiOverboughtLevel)
                .build();
    }

    private void calculateMovingAverage() {
        maResults = MovingAverageFactory.create(buildMARequest()).getResult();
    }

    private IndicatorRequest buildMARequest() {
        return MARequest.builder()
                .originalData(originalData)
                .period(maPeriod)
                .indicatorType(maType)
                .priceType(maPriceType)
                .build();
    }

    private void calculateParabolicStopAndReverse() {
        psarResults = new ParabolicStopAndReverse(buildPSARRequest()).getResult();
    }

    private IndicatorRequest buildPSARRequest() {
        return PSARRequest.builder()
                .originalData(originalData)
                .minAccelerationFactor(psarMinAccelerationFactor)
                .maxAccelerationFactor(psarMaxAccelerationFactor)
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
            defineLookingPosition(currentIndex);
            defineEntry(currentIndex);
        }
    }

    private boolean isPossibleDefineEntry(int currentIndex) {
        return currentIndex > 0
                && nonNull(lrsiAnalyzerResults[currentIndex - 1].getSignal())
                && nonNull(lrsiAnalyzerResults[currentIndex].getSecurityLevel())
                && nonNull(maResults[currentIndex - 1].getIndicatorValue())
                && nonNull(maResults[currentIndex].getIndicatorValue())
                && nonNull(psarResults[currentIndex - 1].getIndicatorValue())
                && nonNull(psarResults[currentIndex].getIndicatorValue());
    }

    private void defineEntry(int currentIndex) {
        if (positions.contains(ENTRY_LONG) && lookingLongEntry && isLongEntry(currentIndex)) {
            result[currentIndex].getPositions().add(ENTRY_LONG);
            lookingLongEntry = false;
        }

        if (positions.contains(ENTRY_SHORT) && lookingShortEntry && isShortEntry(currentIndex)) {
            result[currentIndex].getPositions().add(ENTRY_SHORT);
            lookingShortEntry = false;
        }
    }

    private void defineLookingPosition(int currentIndex) {
        if (lrsiAnalyzerResults[currentIndex - 1].getSignal() == BUY) {
            lookingLongEntry = true;
            lookingShortEntry = false;
        }

        if (lrsiAnalyzerResults[currentIndex - 1].getSignal() == SELL) {
            lookingLongEntry = false;
            lookingShortEntry = true;
        }

        if (isLRSIInExtremeZone(currentIndex)) {
            lookingLongEntry = false;
            lookingShortEntry = false;
        }
    }

    private boolean isLRSIInExtremeZone(int currentIndex) {
        return lrsiAnalyzerResults[currentIndex].getSecurityLevel() == OVERBOUGHT
                || lrsiAnalyzerResults[currentIndex].getSecurityLevel() == OVERSOLD;
    }

    private boolean isLongEntry(int currentIndex) {
        return isMABuyCondition(currentIndex)
                && isPSARBuyCondition(currentIndex);
    }

    private boolean isMABuyCondition(int currentIndex) {
        return originalData[currentIndex - 1].getClose()
                .compareTo(maResults[currentIndex - 1].getIndicatorValue()) > 0
                && originalData[currentIndex].getClose()
                .compareTo(maResults[currentIndex].getIndicatorValue()) > 0;
    }

    private boolean isPSARBuyCondition(int currentIndex) {
        return originalData[currentIndex - 1].getLow()
                .compareTo(psarResults[currentIndex - 1].getIndicatorValue()) > 0
                && originalData[currentIndex].getLow()
                .compareTo(psarResults[currentIndex].getIndicatorValue()) > 0;
    }

    private boolean isShortEntry(int currentIndex) {
        return isMASellCondition(currentIndex)
                && isPSARSellCondition(currentIndex);
    }

    private boolean isMASellCondition(int currentIndex) {
        return originalData[currentIndex - 1].getClose()
                .compareTo(maResults[currentIndex - 1].getIndicatorValue()) < 0
                && originalData[currentIndex].getClose()
                .compareTo(maResults[currentIndex].getIndicatorValue()) < 0;
    }

    private boolean isPSARSellCondition(int currentIndex) {
        return originalData[currentIndex - 1].getHigh()
                .compareTo(psarResults[currentIndex - 1].getIndicatorValue()) < 0
                && originalData[currentIndex].getHigh()
                .compareTo(psarResults[currentIndex].getIndicatorValue()) < 0;
    }

    private void findExits() {
        if (positions.contains(EXIT_LONG) || positions.contains(EXIT_SHORT)) {
            analyzerParabolicStopAndReverse();
            IntStream.range(0, originalData.length)
                    .forEach(this::findExit);
        }
    }

    private void analyzerParabolicStopAndReverse() {
        psarAnalyzerResults = new PSARAnalyzer(new AnalyzerRequest(originalData, psarResults)).getResult();
    }

    private void findExit(int currentIndex) {
        switch (psarAnalyzerResults[currentIndex].getSignal()) {
            case BUY:
                result[currentIndex].getPositions().add(EXIT_SHORT);
                break;
            case SELL:
                result[currentIndex].getPositions().add(EXIT_LONG);
                break;
            default:
                /*NOP*/
        }
    }

}
