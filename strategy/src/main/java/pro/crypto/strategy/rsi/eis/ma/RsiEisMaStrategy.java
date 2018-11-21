package pro.crypto.strategy.rsi.eis.ma;

import pro.crypto.indicator.eis.EISRequest;
import pro.crypto.indicator.eis.EISResult;
import pro.crypto.indicator.eis.ElderImpulseSystem;
import pro.crypto.indicator.ma.MARequest;
import pro.crypto.indicator.ma.MAResult;
import pro.crypto.indicator.ma.MovingAverageFactory;
import pro.crypto.indicator.rsi.RSIRequest;
import pro.crypto.indicator.rsi.RSIResult;
import pro.crypto.indicator.rsi.RelativeStrengthIndex;
import pro.crypto.model.*;
import pro.crypto.model.tick.PriceType;
import pro.crypto.model.tick.Tick;

import java.math.BigDecimal;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.IntStream;

import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;
import static java.util.Optional.ofNullable;
import static pro.crypto.indicator.eis.BarColor.GREEN;
import static pro.crypto.indicator.eis.BarColor.RED;
import static pro.crypto.model.Position.ENTRY_LONG;
import static pro.crypto.model.Position.ENTRY_SHORT;
import static pro.crypto.model.StrategyType.RSI_EIS_MA;

public class RsiEisMaStrategy implements Strategy<RsiEisMaResult> {

    private final Tick[] originalData;
    private final IndicatorType rsiMaType;
    private final int rsiPeriod;
    private final BigDecimal rsiSignalLine;
    private final int eisMaPeriod;
    private final IndicatorType eisMaType;
    private final PriceType eisMaPriceType;
    private final IndicatorType eisMacdMaType;
    private final PriceType eisMacdPriceType;
    private final int eisMacdFastPeriod;
    private final int eisMacdSlowPeriod;
    private final int eisMacdSignalPeriod;
    private final IndicatorType fastMaType;
    private final PriceType fastMaPriceType;
    private final int fastMaPeriod;
    private final IndicatorType slowMaType;
    private final PriceType slowMaPriceType;
    private final int slowMaPeriod;
    private final Set<Position> positions;

    private RSIResult[] rsiResults;
    private EISResult[] eisResults;
    private MAResult[] fastMaResults;
    private MAResult[] slowMaResults;
    private RsiEisMaResult[] result;

    public RsiEisMaStrategy(StrategyRequest strategyRequest) {
        RsiEisMaRequest request = (RsiEisMaRequest) strategyRequest;
        this.originalData = request.getOriginalData();
        this.rsiMaType = request.getRsiMaType();
        this.rsiPeriod = request.getRsiPeriod();
        this.rsiSignalLine = extractRSISignalLine(request);
        this.eisMaPeriod = request.getEisMaPeriod();
        this.eisMaType = request.getEisMaType();
        this.eisMaPriceType = request.getEisMaPriceType();
        this.eisMacdMaType = request.getEisMacdMaType();
        this.eisMacdPriceType = request.getEisMacdPriceType();
        this.eisMacdFastPeriod = request.getEisMacdFastPeriod();
        this.eisMacdSlowPeriod = request.getEisMacdSlowPeriod();
        this.eisMacdSignalPeriod = request.getEisMacdSignalPeriod();
        this.fastMaType = request.getFastMaType();
        this.fastMaPriceType = request.getFastMaPriceType();
        this.fastMaPeriod = request.getFastMaPeriod();
        this.slowMaType = request.getSlowMaType();
        this.slowMaPriceType = request.getSlowMaPriceType();
        this.slowMaPeriod = request.getSlowMaPeriod();
        this.positions = request.getPositions();
        checkPositions(positions);
    }

    @Override
    public StrategyType getType() {
        return RSI_EIS_MA;
    }

    @Override
    public void analyze() {
        initResultArray();
        calculateRelativeStrengthIndex();
        calculateElderImpulseSystem();
        calculateFastMovingAverage();
        calculateSlowMovingAverage();
        findEntries();
    }

    @Override
    public RsiEisMaResult[] getResult() {
        if (isNull(result)) {
            analyze();
        }
        return result;
    }

    private BigDecimal extractRSISignalLine(RsiEisMaRequest request) {
        return ofNullable(request.getRsiSignalLine())
                .map(BigDecimal::new)
                .orElse(new BigDecimal(50));
    }

    private void initResultArray() {
        result = IntStream.range(0, originalData.length)
                .mapToObj(idx -> RsiEisMaResult.builder()
                        .time(originalData[idx].getTickTime())
                        .positions(new HashSet<>())
                        .build())
                .toArray(RsiEisMaResult[]::new);
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

    private void calculateElderImpulseSystem() {
        eisResults = new ElderImpulseSystem(buildEISRequest()).getResult();
    }

    private IndicatorRequest buildEISRequest() {
        return EISRequest.builder()
                .originalData(originalData)
                .maPeriod(eisMaPeriod)
                .maType(eisMaType)
                .maPriceType(eisMaPriceType)
                .macdMaType(eisMacdMaType)
                .macdPriceType(eisMacdPriceType)
                .macdFastPeriod(eisMacdFastPeriod)
                .macdSlowPeriod(eisMacdSlowPeriod)
                .macdSignalPeriod(eisMacdSignalPeriod)
                .build();
    }

    private void calculateFastMovingAverage() {
        fastMaResults = MovingAverageFactory.create(buildFastMARequest()).getResult();
    }

    private void calculateSlowMovingAverage() {
        slowMaResults = MovingAverageFactory.create(buildSlowMARequest()).getResult();
    }

    private IndicatorRequest buildFastMARequest() {
        return MARequest.builder()
                .originalData(originalData)
                .period(fastMaPeriod)
                .indicatorType(fastMaType)
                .priceType(fastMaPriceType)
                .build();
    }

    private IndicatorRequest buildSlowMARequest() {
        return MARequest.builder()
                .originalData(originalData)
                .period(slowMaPeriod)
                .indicatorType(slowMaType)
                .priceType(slowMaPriceType)
                .build();
    }

    private void findEntries() {
        IntStream.range(0, originalData.length)
                .forEach(this::findEntry);
    }

    private void findEntry(int currentIndex) {
        if (isPossibleDefineEntry(currentIndex)) {
            defineEntry(currentIndex);
        }
    }

    private boolean isPossibleDefineEntry(int currentIndex) {
        return currentIndex > 1
                && nonNull(slowMaResults[currentIndex - 2].getIndicatorValue())
                && nonNull(fastMaResults[currentIndex - 2].getIndicatorValue())
                && nonNull(slowMaResults[currentIndex - 1].getIndicatorValue())
                && nonNull(fastMaResults[currentIndex - 1].getIndicatorValue())
                && nonNull(rsiResults[currentIndex - 1].getIndicatorValue())
                && nonNull(eisResults[currentIndex - 1].getBarColor());
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
        return isLongMAIntersection(currentIndex)
                && isRSIAboveSignalLine(currentIndex)
                && isGreenEISBar(currentIndex);
    }

    private boolean isLongMAIntersection(int currentIndex) {
        return fastMaResults[currentIndex - 2].getIndicatorValue()
                .compareTo(slowMaResults[currentIndex - 2].getIndicatorValue()) < 0
                && fastMaResults[currentIndex - 1].getIndicatorValue()
                .compareTo(slowMaResults[currentIndex - 1].getIndicatorValue()) > 0;
    }

    private boolean isRSIAboveSignalLine(int currentIndex) {
        return rsiResults[currentIndex - 1].getIndicatorValue().compareTo(rsiSignalLine) > 0;
    }

    private boolean isGreenEISBar(int currentIndex) {
        return eisResults[currentIndex - 1].getBarColor() == GREEN;
    }

    private boolean isShortEntry(int currentIndex) {
        return isShortMAIntersection(currentIndex)
                && isRSIUnderSignalLine(currentIndex)
                && isRedEISBar(currentIndex);
    }

    private boolean isShortMAIntersection(int currentIndex) {
        return fastMaResults[currentIndex - 2].getIndicatorValue()
                .compareTo(slowMaResults[currentIndex - 2].getIndicatorValue()) > 0
                && fastMaResults[currentIndex - 1].getIndicatorValue()
                .compareTo(slowMaResults[currentIndex - 1].getIndicatorValue()) < 0;
    }

    private boolean isRSIUnderSignalLine(int currentIndex) {
        return rsiResults[currentIndex - 1].getIndicatorValue().compareTo(rsiSignalLine) < 0;
    }

    private boolean isRedEISBar(int currentIndex) {
        return eisResults[currentIndex - 1].getBarColor() == RED;
    }

}
