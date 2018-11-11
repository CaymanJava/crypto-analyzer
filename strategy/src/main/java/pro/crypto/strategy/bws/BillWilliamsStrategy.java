package pro.crypto.strategy.bws;

import pro.crypto.analyzer.ac.ACAnalyzer;
import pro.crypto.analyzer.ac.ACAnalyzerResult;
import pro.crypto.analyzer.ao.AOAnalyzer;
import pro.crypto.analyzer.ao.AOAnalyzerResult;
import pro.crypto.helper.SignalArrayMerger;
import pro.crypto.indicator.ac.ACRequest;
import pro.crypto.indicator.ac.ACResult;
import pro.crypto.indicator.ac.AccelerationDecelerationOscillator;
import pro.crypto.indicator.alligator.Alligator;
import pro.crypto.indicator.alligator.AlligatorRequest;
import pro.crypto.indicator.alligator.AlligatorResult;
import pro.crypto.indicator.ao.AORequest;
import pro.crypto.indicator.ao.AOResult;
import pro.crypto.indicator.ao.AwesomeOscillator;
import pro.crypto.indicator.fractal.Fractal;
import pro.crypto.indicator.fractal.FractalRequest;
import pro.crypto.indicator.fractal.FractalResult;
import pro.crypto.model.*;
import pro.crypto.model.result.SignalResult;
import pro.crypto.model.tick.Tick;
import pro.crypto.model.tick.TimeFrame;

import java.math.BigDecimal;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import static java.math.BigDecimal.ZERO;
import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;
import static pro.crypto.helper.MathHelper.scaleAndRound;
import static pro.crypto.model.Position.*;
import static pro.crypto.model.Signal.BUY;
import static pro.crypto.model.Signal.SELL;
import static pro.crypto.model.StrategyType.BILL_WILLIAMS_STRATEGY;

public class BillWilliamsStrategy implements Strategy<BWSResult> {

    private final Tick[] originalData;
    private final int acSlowPeriod;
    private final int acFastPeriod;
    private final int acSmoothedPeriod;
    private final int alligatorJawPeriod;
    private final int alligatorJawOffset;
    private final int alligatorTeethPeriod;
    private final int alligatorTeethOffset;
    private final int alligatorLipsPeriod;
    private final int alligatorLipsOffset;
    private final TimeFrame alligatorTimeFrame;
    private final int aoSlowPeriod;
    private final int aoFastPeriod;
    private final Set<Position> positions;

    private BigDecimal highPriceLastUpFractal;
    private BigDecimal lowPriceLastDownFractal;
    private boolean lookingLongEntryConfirmation;
    private boolean lookingShortEntryConfirmation;
    private ACResult[] acResults;
    private AOResult[] aoResults;
    private BWSResult[] result;

    public BillWilliamsStrategy(StrategyRequest strategyRequest) {
        BWSRequest request = (BWSRequest) strategyRequest;
        this.originalData = request.getOriginalData();
        this.acSlowPeriod = request.getAcSlowPeriod();
        this.acFastPeriod = request.getAcFastPeriod();
        this.acSmoothedPeriod = request.getAcSmoothedPeriod();
        this.alligatorJawPeriod = request.getAlligatorJawPeriod();
        this.alligatorJawOffset = request.getAlligatorJawOffset();
        this.alligatorTeethPeriod = request.getAlligatorTeethPeriod();
        this.alligatorTeethOffset = request.getAlligatorTeethOffset();
        this.alligatorLipsPeriod = request.getAlligatorLipsPeriod();
        this.alligatorLipsOffset = request.getAlligatorLipsOffset();
        this.alligatorTimeFrame = request.getAlligatorTimeFrame();
        this.aoSlowPeriod = request.getAoSlowPeriod();
        this.aoFastPeriod = request.getAoFastPeriod();
        this.positions = request.getPositions();
        checkPositions(positions);
    }

    @Override
    public StrategyType getType() {
        return BILL_WILLIAMS_STRATEGY;
    }

    @Override
    public void analyze() {
        initResultArray();
        calculateConfirmationIndicators();
        findEntries();
        findExits();
    }

    @Override
    public BWSResult[] getResult() {
        if (isNull(result)) {
            analyze();
        }
        return result;
    }

    private void initResultArray() {
        result = IntStream.range(0, originalData.length)
                .mapToObj(idx -> BWSResult.builder()
                        .time(originalData[idx].getTickTime())
                        .positions(new HashSet<>())
                        .build())
                .toArray(BWSResult[]::new);
    }

    private void calculateConfirmationIndicators() {
        acResults = calculateAccelerationDecelerationOscillator();
        aoResults = calculateAwesomeOscillator();
    }

    private ACResult[] calculateAccelerationDecelerationOscillator() {
        return new AccelerationDecelerationOscillator(buildACRequest()).getResult();
    }

    private IndicatorRequest buildACRequest() {
        return ACRequest.builder()
                .originalData(originalData)
                .fastPeriod(acFastPeriod)
                .slowPeriod(acSlowPeriod)
                .smoothedPeriod(acSmoothedPeriod)
                .build();
    }

    private AOResult[] calculateAwesomeOscillator() {
        return new AwesomeOscillator(buildAORequest()).getResult();
    }

    private IndicatorRequest buildAORequest() {
        return AORequest.builder()
                .originalData(originalData)
                .fastPeriod(aoFastPeriod)
                .slowPeriod(aoSlowPeriod)
                .build();
    }


    private void findEntries() {
        if (entryPositionsRequired()) {
            AlligatorResult[] alligatorResults = calculateAlligator();
            FractalResult[] fractalResults = calculateFractalsWithAlligatorFilter(alligatorResults);
            recognizeEntries(fractalResults, alligatorResults);
        }
    }

    private boolean entryPositionsRequired() {
        return positions.contains(ENTRY_LONG) || positions.contains(ENTRY_SHORT);
    }

    private FractalResult[] calculateFractalsWithAlligatorFilter(AlligatorResult[] alligatorResults) {
        FractalResult[] fractalResults = new Fractal(buildFractalRequest()).getResult();
        return filterFractalsWithAlligator(fractalResults, alligatorResults);
    }

    private IndicatorRequest buildFractalRequest() {
        return FractalRequest.builder()
                .originalData(originalData)
                .build();
    }

    private AlligatorResult[] calculateAlligator() {
        return new Alligator(buildAlligatorRequest()).getResult();
    }

    private IndicatorRequest buildAlligatorRequest() {
        return AlligatorRequest.builder()
                .originalData(originalData)
                .jawPeriod(alligatorJawPeriod)
                .jawOffset(alligatorJawOffset)
                .teethPeriod(alligatorTeethPeriod)
                .teethOffset(alligatorTeethOffset)
                .lipsPeriod(alligatorLipsPeriod)
                .lipsOffset(alligatorLipsOffset)
                .timeFrame(alligatorTimeFrame)
                .build();
    }

    private FractalResult[] filterFractalsWithAlligator(FractalResult[] fractalResults, AlligatorResult[] alligatorResults) {
        return IntStream.range(0, fractalResults.length)
                .mapToObj(idx -> filterFractalWithAlligator(fractalResults[idx], alligatorResults[idx], idx))
                .toArray(FractalResult[]::new);
    }

    private FractalResult filterFractalWithAlligator(FractalResult fractalResult, AlligatorResult alligatorResult, int currentIndex) {
        if (isNull(alligatorResult.getTeethValue()) || fractalResult.isUpFractal() && !teethUnderLowPrice(alligatorResult.getTeethValue(), currentIndex)) {
            fractalResult.setUpFractal(false);
        }

        if (isNull(alligatorResult.getTeethValue()) || fractalResult.isDownFractal() && !teethOverHighPrice(alligatorResult.getTeethValue(), currentIndex)) {
            fractalResult.setDownFractal(false);
        }

        return fractalResult;
    }

    private void recognizeEntries(FractalResult[] fractalResults, AlligatorResult[] alligatorResults) {
        IntStream.range(0, fractalResults.length)
                .forEach(idx -> recognizeEntry(fractalResults[idx], alligatorResults[idx], idx));
    }

    private void recognizeEntry(FractalResult fractalResult, AlligatorResult alligatorResult, int currentIndex) {
        refreshLastFractals(fractalResult, currentIndex);
        if (positions.contains(ENTRY_LONG) && nonNull(highPriceLastUpFractal)) {
            findLongEntry(alligatorResult, currentIndex);
        }
        if (positions.contains(ENTRY_SHORT) && nonNull(lowPriceLastDownFractal)) {
            findShortEntry(alligatorResult, currentIndex);
        }
    }

    private void refreshLastFractals(FractalResult fractalResult, int currentIndex) {
        if (fractalResult.isUpFractal()) {
            highPriceLastUpFractal = originalData[currentIndex].getHigh();
            lookingLongEntryConfirmation = false;
        }

        if (fractalResult.isDownFractal()) {
            lowPriceLastDownFractal = originalData[currentIndex].getLow();
            lookingShortEntryConfirmation = false;
        }
    }

    private void findLongEntry(AlligatorResult alligatorResult, int currentIndex) {
        if (isPriceAcceptableForLongEntry(currentIndex) && teethUnderLowPrice(alligatorResult.getTeethValue(), currentIndex)) {
            lookingLongEntryConfirmation = true;
        }
        if (lookingLongEntryConfirmation) {
            lookForLongEntryConfirmation(alligatorResult, currentIndex);
        }
    }

    private void lookForLongEntryConfirmation(AlligatorResult alligatorResult, int currentIndex) {
        if (isAcBuyCondition(currentIndex) && isAoBuyCondition(currentIndex)) {
            setLongEntryResult(alligatorResult, currentIndex);
            resetLongEntryParams();
        }
    }

    private void setLongEntryResult(AlligatorResult alligatorResult, int currentIndex) {
        result[currentIndex].getPositions().add(ENTRY_LONG);
        result[currentIndex].setEntryPrice(scaleAndRound(highPriceLastUpFractal.multiply(new BigDecimal(1.01))));
        result[currentIndex].setStopLose(alligatorResult.getLipsValue());
    }

    private void resetLongEntryParams() {
        highPriceLastUpFractal = null;
        lookingLongEntryConfirmation = false;
    }

    private boolean isAcBuyCondition(int currentIndex) {
        return nonNull(acResults[currentIndex].getIndicatorValue()) &&
                acResults[currentIndex].getIndicatorValue().compareTo(ZERO) > 0
                && nonNull(acResults[currentIndex].getIncreased())
                && acResults[currentIndex].getIncreased();
    }

    private boolean isAoBuyCondition(int currentIndex) {
        return nonNull(aoResults[currentIndex].getIndicatorValue()) &&
                aoResults[currentIndex].getIndicatorValue().compareTo(ZERO) > 0
                && nonNull(aoResults[currentIndex].getIncreased())
                && aoResults[currentIndex].getIncreased();
    }

    private boolean isPriceAcceptableForLongEntry(int currentIndex) {
        return originalData[currentIndex].getHigh().compareTo(highPriceLastUpFractal) >= 0;
    }

    private boolean teethUnderLowPrice(BigDecimal teethValue, int currentIndex) {
        return originalData[currentIndex].getLow().compareTo(teethValue) > 0;
    }

    private void findShortEntry(AlligatorResult alligatorResult, int currentIndex) {
        if (isShortEntryPriceAcceptable(currentIndex) && teethOverHighPrice(alligatorResult.getTeethValue(), currentIndex)) {
            lookingShortEntryConfirmation = true;
        }
        if (lookingShortEntryConfirmation) {
            lookForShortEntryConfirmation(alligatorResult, currentIndex);
        }
    }

    private void lookForShortEntryConfirmation(AlligatorResult alligatorResult, int currentIndex) {
        if (isAcSellCondition(currentIndex) && isAoSellCondition(currentIndex)) {
            setShortEntryResult(alligatorResult, currentIndex);
            resetShortEntryParams();
        }
    }

    private void setShortEntryResult(AlligatorResult alligatorResult, int currentIndex) {
        result[currentIndex].getPositions().add(ENTRY_SHORT);
        result[currentIndex].setEntryPrice(scaleAndRound(lowPriceLastDownFractal.multiply(new BigDecimal(0.99))));
        result[currentIndex].setStopLose(alligatorResult.getLipsValue());
    }

    private void resetShortEntryParams() {
        lowPriceLastDownFractal = null;
        lookingShortEntryConfirmation = false;
    }

    private boolean isShortEntryPriceAcceptable(int currentIndex) {
        return originalData[currentIndex].getLow().compareTo(lowPriceLastDownFractal) <= 0;
    }

    private boolean teethOverHighPrice(BigDecimal teethValue, int currentIndex) {
        return originalData[currentIndex].getHigh().compareTo(teethValue) < 0;
    }

    private boolean isAcSellCondition(int currentIndex) {
        return nonNull(acResults[currentIndex].getIndicatorValue()) &&
                acResults[currentIndex].getIndicatorValue().compareTo(ZERO) < 0
                && nonNull(acResults[currentIndex].getIncreased())
                && !acResults[currentIndex].getIncreased();
    }

    private boolean isAoSellCondition(int currentIndex) {
        return nonNull(aoResults[currentIndex].getIndicatorValue()) &&
                aoResults[currentIndex].getIndicatorValue().compareTo(ZERO) < 0
                && nonNull(aoResults[currentIndex].getIncreased())
                && !aoResults[currentIndex].getIncreased();
    }

    private void findExits() {
        if (exitPositionsRequired()) {
            ACAnalyzerResult[] acAnalyzerResult = analyzeAccelerationDecelerationOscillator();
            AOAnalyzerResult[] aoAnalyzerResult = analyzeAwesomeOscillator();
            Signal[] mergedSignals = mergeSignals(acAnalyzerResult, aoAnalyzerResult);
            convertAndSetPositions(mergedSignals);
        }
    }

    private boolean exitPositionsRequired() {
        return positions.contains(EXIT_LONG) || positions.contains(EXIT_SHORT);
    }

    private ACAnalyzerResult[] analyzeAccelerationDecelerationOscillator() {
        return new ACAnalyzer(buildAnalyzerRequest(acResults)).getResult();
    }

    private AOAnalyzerResult[] analyzeAwesomeOscillator() {
        return new AOAnalyzer(buildAnalyzerRequest(aoResults)).getResult();
    }

    private AnalyzerRequest buildAnalyzerRequest(IndicatorResult[] indicatorResults) {
        return new AnalyzerRequest(originalData, indicatorResults);
    }

    private Signal[] mergeSignals(ACAnalyzerResult[] acAnalyzerResult, AOAnalyzerResult[] aoAnalyzerResult) {
        return SignalArrayMerger.mergeSignals(extractSignals(acAnalyzerResult), extractSignals(aoAnalyzerResult));
    }

    private Signal[] extractSignals(SignalResult[] signalResults) {
        return Stream.of(signalResults)
                .map(SignalResult::getSignal)
                .toArray(Signal[]::new);
    }

    private void convertAndSetPositions(Signal[] mergedSignals) {
        IntStream.range(0, mergedSignals.length)
                .forEach(idx -> convertAndSetPosition(mergedSignals[idx], idx));
    }

    private void convertAndSetPosition(Signal mergedSignal, int currentIndex) {
        if (isNull(mergedSignal)) {
            return;
        }

        if (mergedSignal == BUY && positions.contains(EXIT_SHORT)) {
            result[currentIndex].getPositions().add(EXIT_SHORT);
        }

        if (mergedSignal == SELL && positions.contains(EXIT_LONG)) {
            result[currentIndex].getPositions().add(EXIT_LONG);
        }
    }

}
