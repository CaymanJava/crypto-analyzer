package pro.crypto.strategy.cci.rsi.atr;

import pro.crypto.helper.PeakValleyFinder;
import pro.crypto.helper.StaticLineCrossAnalyzer;
import pro.crypto.indicator.atr.ATRRequest;
import pro.crypto.indicator.atr.ATRResult;
import pro.crypto.indicator.atr.AverageTrueRange;
import pro.crypto.indicator.cci.CCIRequest;
import pro.crypto.indicator.cci.CCIResult;
import pro.crypto.indicator.cci.CommodityChannelIndex;
import pro.crypto.indicator.rsi.RSIRequest;
import pro.crypto.indicator.rsi.RSIResult;
import pro.crypto.indicator.rsi.RelativeStrengthIndex;
import pro.crypto.model.*;
import pro.crypto.model.tick.Tick;

import java.math.BigDecimal;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.IntStream;

import static java.math.BigDecimal.ZERO;
import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;
import static java.util.Optional.ofNullable;
import static pro.crypto.helper.IndicatorResultExtractor.extractIndicatorValues;
import static pro.crypto.model.Position.ENTRY_LONG;
import static pro.crypto.model.Position.ENTRY_SHORT;
import static pro.crypto.model.Signal.BUY;
import static pro.crypto.model.Signal.SELL;
import static pro.crypto.model.StrategyType.CCI_RSI_ATR;

public class CciRsiAtrStrategy implements Strategy<CciRsiAtrResult> {

    private final Tick[] originalData;
    private final int cciPeriod;
    private final BigDecimal cciSignalLine;
    private final IndicatorType rsiMaType;
    private final int rsiPeriod;
    private final BigDecimal rsiSignalLine;
    private final int atrPeriod;
    private final IndicatorType atrMaType;
    private final int atrMaPeriod;
    private final Set<Position> positions;

    private Boolean[] peaks;
    private Boolean[] valleys;
    private Signal[] cciCrossSignals;
    private RSIResult[] rsiResults;
    private ATRResult[] atrResults;
    private boolean lookingLongEntry;
    private boolean lookingShortEntry;
    private int lastPeakIndex = 0;
    private int lastValleyIndex = 0;
    private CciRsiAtrResult[] result;

    public CciRsiAtrStrategy(StrategyRequest strategyRequest) {
        CciRsiAtrRequest request = (CciRsiAtrRequest) strategyRequest;
        this.originalData = request.getOriginalData();
        this.cciPeriod = request.getCciPeriod();
        this.cciSignalLine = extractCCISignalLine(request);
        this.rsiMaType = request.getRsiMaType();
        this.rsiPeriod = request.getRsiPeriod();
        this.rsiSignalLine = extractRSISignalLine(request);
        this.atrPeriod = request.getAtrPeriod();
        this.atrMaType = request.getAtrMaType();
        this.atrMaPeriod = request.getAtrMaPeriod();
        this.positions = request.getPositions();
        checkPositions(positions);
    }

    @Override
    public StrategyType getType() {
        return CCI_RSI_ATR;
    }

    @Override
    public void analyze() {
        initResultArray();
        findPeaksAndValleys();
        findCCICrossSignals();
        calculateRelativeStrengthIndex();
        calculateAverageTrueRange();
        findEntries();
    }

    @Override
    public CciRsiAtrResult[] getResult() {
        if (isNull(result)) {
            analyze();
        }
        return result;
    }

    private BigDecimal extractCCISignalLine(CciRsiAtrRequest request) {
        return ofNullable(request.getCciSignalLine())
                .map(BigDecimal::new)
                .orElse(ZERO);
    }

    private BigDecimal extractRSISignalLine(CciRsiAtrRequest request) {
        return ofNullable(request.getRsiSignalLine())
                .map(BigDecimal::new)
                .orElse(new BigDecimal(50));
    }

    private void initResultArray() {
        result = IntStream.range(0, originalData.length)
                .mapToObj(idx -> CciRsiAtrResult.builder()
                        .time(originalData[idx].getTickTime())
                        .positions(new HashSet<>())
                        .build())
                .toArray(CciRsiAtrResult[]::new);
    }

    private void findPeaksAndValleys() {
        peaks = PeakValleyFinder.findPeaks(originalData);
        valleys = PeakValleyFinder.findValleys(originalData);
    }

    private void findCCICrossSignals() {
        CCIResult[] result = calculateCommodityChannelIndex();
        cciCrossSignals = new StaticLineCrossAnalyzer(extractIndicatorValues(result), cciSignalLine).analyze();
    }

    private CCIResult[] calculateCommodityChannelIndex() {
        return new CommodityChannelIndex(buildCCIRequest()).getResult();
    }

    private IndicatorRequest buildCCIRequest() {
        return CCIRequest.builder()
                .originalData(originalData)
                .period(cciPeriod)
                .build();
    }

    private void calculateRelativeStrengthIndex() {
        rsiResults = new RelativeStrengthIndex(buildRSIRequest()).getResult();
    }

    private IndicatorRequest buildRSIRequest() {
        return RSIRequest.builder()
                .originalData(originalData)
                .movingAverageType(rsiMaType)
                .period(rsiPeriod)
                .build();
    }

    private void calculateAverageTrueRange() {
        atrResults = new AverageTrueRange(buildATRRequest()).getResult();
    }

    private IndicatorRequest buildATRRequest() {
        return ATRRequest.builder()
                .originalData(originalData)
                .period(atrPeriod)
                .movingAverageType(atrMaType)
                .movingAveragePeriod(atrMaPeriod)
                .build();
    }

    private void findEntries() {
        if (positions.contains(ENTRY_LONG) || positions.contains(ENTRY_SHORT)) {
            IntStream.range(0, originalData.length)
                    .forEach(this::findEntry);
        }
    }

    private void findEntry(int currentIndex) {
        refreshPeakAndValley(currentIndex);
        if (isPossibleDefineEntry(currentIndex)) {
            defineLookingPosition(currentIndex);
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

    private void defineLookingPosition(int currentIndex) {
        if (cciCrossSignals[currentIndex - 1] == BUY) {
            lookingLongEntry = true;
            lookingShortEntry = false;
        }

        if (cciCrossSignals[currentIndex - 1] == SELL) {
            lookingLongEntry = false;
            lookingShortEntry = true;
        }
    }

    private boolean isPossibleDefineEntry(int currentIndex) {
        return currentIndex > 0
                && nonNull(cciCrossSignals[currentIndex - 1])
                && nonNull(cciCrossSignals[currentIndex])
                && nonNull(rsiResults[currentIndex - 1].getIndicatorValue())
                && nonNull(rsiResults[currentIndex].getIndicatorValue())
                && nonNull(atrResults[currentIndex - 1].getIndicatorValue())
                && nonNull(atrResults[currentIndex - 1].getSignalLineValue())
                && nonNull(atrResults[currentIndex].getIndicatorValue())
                && nonNull(atrResults[currentIndex].getSignalLineValue());
    }

    private void defineEntry(int currentIndex) {
        if (positions.contains(ENTRY_LONG) && lookingLongEntry && isLongEntry(currentIndex)) {
            result[currentIndex].getPositions().add(ENTRY_LONG);
            result[currentIndex].setStopLose(originalData[lastValleyIndex].getLow());
            lookingLongEntry = false;
        }

        if (positions.contains(ENTRY_SHORT) && lookingShortEntry && isShortEntry(currentIndex)) {
            result[currentIndex].getPositions().add(ENTRY_SHORT);
            result[currentIndex].setStopLose(originalData[lastPeakIndex].getHigh());
            lookingShortEntry = false;
        }
    }

    private boolean isLongEntry(int currentIndex) {
        return isCCIBuyCondition(currentIndex)
                && isRSIBuyCondition(currentIndex)
                && isATRCondition(currentIndex);
    }

    private boolean isCCIBuyCondition(int currentIndex) {
        return cciCrossSignals[currentIndex] != SELL;
    }

    private boolean isRSIBuyCondition(int currentIndex) {
        return rsiResults[currentIndex - 1].getIndicatorValue()
                .compareTo(rsiSignalLine) > 0
                && rsiResults[currentIndex].getIndicatorValue()
                .compareTo(rsiSignalLine) > 0;
    }

    private boolean isShortEntry(int currentIndex) {
        return isCCISellCondition(currentIndex)
                && isRSISellCondition(currentIndex)
                && isATRCondition(currentIndex);
    }

    private boolean isCCISellCondition(int currentIndex) {
        return cciCrossSignals[currentIndex] != BUY;
    }

    private boolean isRSISellCondition(int currentIndex) {
        return rsiResults[currentIndex - 1].getIndicatorValue()
                .compareTo(rsiSignalLine) < 0
                && rsiResults[currentIndex].getIndicatorValue()
                .compareTo(rsiSignalLine) < 0;
    }

    private boolean isATRCondition(int currentIndex) {
        return atrResults[currentIndex - 1].getIndicatorValue()
                .compareTo(atrResults[currentIndex - 1].getSignalLineValue()) > 0;
    }

}
