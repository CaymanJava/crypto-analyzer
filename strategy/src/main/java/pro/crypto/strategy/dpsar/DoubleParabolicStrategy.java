package pro.crypto.strategy.dpsar;

import pro.crypto.helper.MathHelper;
import pro.crypto.indicator.ma.MARequest;
import pro.crypto.indicator.ma.MAResult;
import pro.crypto.indicator.ma.MovingAverageFactory;
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

import static java.math.BigDecimal.ZERO;
import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;
import static pro.crypto.model.Position.ENTRY_LONG;
import static pro.crypto.model.Position.ENTRY_SHORT;
import static pro.crypto.model.StrategyType.DOUBLE_PARABOLIC;

public class DoubleParabolicStrategy implements Strategy<DPsarResult> {

    private final Tick[] originalData;
    private final int movingAveragePeriod;
    private final IndicatorType movingAverageType;
    private final PriceType movingAveragePriceType;
    private final IndicatorType macdMovingAverageType;
    private final PriceType macdPriceType;
    private final int macdSlowPeriod;
    private final int macdFastPeriod;
    private final int macdSignalPeriod;
    private final double psarMinAccelerationFactor;
    private final double psarMaxAccelerationFactor;
    private final double pswMinAccelerationFactor;
    private final double pswMaxAccelerationFactor;
    private final Set<Position> positions;

    private MAResult[] maResult;
    private PSARResult[] psarResult;
    private MACDResult[] macdResult;
    private PSARResult[] pswResult;
    private DPsarResult[] result;

    public DoubleParabolicStrategy(StrategyRequest strategyRequest) {
        DPsarRequest request = (DPsarRequest) strategyRequest;
        this.originalData = request.getOriginalData();
        this.movingAveragePeriod = request.getMovingAveragePeriod();
        this.movingAverageType = request.getMovingAverageType();
        this.movingAveragePriceType = request.getMovingAveragePriceType();
        this.macdMovingAverageType = request.getMacdMovingAverageType();
        this.macdPriceType = request.getMacdPriceType();
        this.macdSlowPeriod = request.getMacdSlowPeriod();
        this.macdFastPeriod = request.getMacdFastPeriod();
        this.macdSignalPeriod = request.getMacdSignalPeriod();
        this.psarMinAccelerationFactor = request.getPsarMinAccelerationFactor();
        this.psarMaxAccelerationFactor = request.getPsarMaxAccelerationFactor();
        this.pswMinAccelerationFactor = request.getPswMinAccelerationFactor();
        this.pswMaxAccelerationFactor = request.getPswMaxAccelerationFactor();
        this.positions = request.getPositions();
        checkPositions(positions);
    }

    @Override
    public StrategyType getType() {
        return DOUBLE_PARABOLIC;
    }

    @Override
    public void analyze() {
        initResultArray();
        calculateMovingAverage();
        calculateParabolicSAR();
        calculateMACD();
        calculateParabolicSW();
        findEntries();
    }

    @Override
    public DPsarResult[] getResult() {
        if (isNull(result)) {
            analyze();
        }
        return result;
    }

    private void initResultArray() {
        result = IntStream.range(0, originalData.length)
                .mapToObj(idx -> DPsarResult.builder()
                        .time(originalData[idx].getTickTime())
                        .positions(new HashSet<>())
                        .build())
                .toArray(DPsarResult[]::new);
    }

    private void calculateMovingAverage() {
        maResult = MovingAverageFactory.create(buildMARequest()).getResult();
    }

    private IndicatorRequest buildMARequest() {
        return MARequest.builder()
                .originalData(originalData)
                .period(movingAveragePeriod)
                .priceType(movingAveragePriceType)
                .indicatorType(movingAverageType)
                .build();
    }

    private void calculateParabolicSAR() {
        psarResult = new ParabolicStopAndReverse(buildPSARRequest()).getResult();
    }

    private IndicatorRequest buildPSARRequest() {
        return PSARRequest.builder()
                .originalData(originalData)
                .minAccelerationFactor(psarMinAccelerationFactor)
                .maxAccelerationFactor(psarMaxAccelerationFactor)
                .build();
    }

    private void calculateMACD() {
        macdResult = new MovingAverageConvergenceDivergence(buildMACDRequest()).getResult();
    }

    private IndicatorRequest buildMACDRequest() {
        return MACDRequest.builder()
                .originalData(originalData)
                .movingAverageType(macdMovingAverageType)
                .priceType(macdPriceType)
                .slowPeriod(macdSlowPeriod)
                .fastPeriod(macdFastPeriod)
                .signalPeriod(macdSignalPeriod)
                .build();
    }

    private void calculateParabolicSW() {
        pswResult = new PSARResult[originalData.length];
        PSARResult[] parabolicSWResults = new ParabolicStopAndReverse(buildPSWRequest()).getResult();
        System.arraycopy(parabolicSWResults, 0, pswResult, macdFastPeriod + macdSignalPeriod + 1, parabolicSWResults.length);
    }

    private IndicatorRequest buildPSWRequest() {
        return PSARRequest.builder()
                .originalData(buildTicksFromMacdResult())
                .minAccelerationFactor(pswMinAccelerationFactor)
                .maxAccelerationFactor(pswMaxAccelerationFactor)
                .build();
    }

    private Tick[] buildTicksFromMacdResult() {
        return IntStream.range(0, macdResult.length)
                .filter(idx -> nonNull(macdResult[idx].getSignalLineValue()))
                .mapToObj(this::buildTickFromMacdResult)
                .toArray(Tick[]::new);
    }

    private Tick buildTickFromMacdResult(int currentIndex) {
        return Tick.builder()
                .tickTime(macdResult[currentIndex].getTime())
                .high(macdResult[currentIndex].getSignalLineValue())
                .low(macdResult[currentIndex].getSignalLineValue())
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
        return currentIndex > 0
                && nonNull(maResult[currentIndex - 1].getIndicatorValue())
                && nonNull(psarResult[currentIndex - 1].getIndicatorValue())
                && nonNull(macdResult[currentIndex - 1].getSignalLineValue())
                && nonNull(pswResult[currentIndex - 1])
                && nonNull(pswResult[currentIndex - 1].getIndicatorValue());
    }

    private void defineEntry(int currentIndex) {
        if (positions.contains(ENTRY_LONG) && isLongEntry(currentIndex)) {
            result[currentIndex].getPositions().add(ENTRY_LONG);
            defineLongStopLose(currentIndex);
        }

        if (positions.contains(ENTRY_SHORT) && isShortEntry(currentIndex)) {
            result[currentIndex].getPositions().add(ENTRY_SHORT);
            defineShortStopLose(currentIndex);
        }
    }

    private boolean isLongEntry(int currentIndex) {
        return isClosePriceHigherMa(currentIndex - 1)
                && isLowPriceHigherParabolicSAR(currentIndex - 1)
                && isMacdSignalLineHigherZero(currentIndex - 1)
                && isMacdSignalLineHigherParabolicSW(currentIndex - 1);
    }

    private boolean isClosePriceHigherMa(int index) {
        return originalData[index].getClose().compareTo(maResult[index].getIndicatorValue()) > 0;
    }

    private boolean isLowPriceHigherParabolicSAR(int index) {
        return originalData[index].getLow().compareTo(psarResult[index].getIndicatorValue()) > 0;
    }

    private boolean isMacdSignalLineHigherZero(int index) {
        return macdResult[index].getSignalLineValue().compareTo(ZERO) > 0;
    }

    private boolean isMacdSignalLineHigherParabolicSW(int index) {
        return macdResult[index].getSignalLineValue().compareTo(pswResult[index].getIndicatorValue()) > 0;
    }

    private void defineLongStopLose(int currentIndex) {
        if (currentIndex > 1) {
            result[currentIndex].setStopLose(MathHelper.min(
                    originalData[currentIndex - 2].getLow(),
                    originalData[currentIndex - 1].getLow(),
                    originalData[currentIndex].getLow()));
        } else {
            result[currentIndex].setStopLose(MathHelper.min(
                    originalData[currentIndex - 1].getLow(),
                    originalData[currentIndex].getLow()));
        }
    }

    private boolean isShortEntry(int currentIndex) {
        return isClosePriceLowerMa(currentIndex - 1)
                && isHighPriceLowerParabolicSAR(currentIndex - 1)
                && isMacdSignalLineLowerZero(currentIndex - 1)
                && isMacdSignalLineLowerParabolicSW(currentIndex - 1);
    }

    private boolean isClosePriceLowerMa(int index) {
        return originalData[index].getClose().compareTo(maResult[index].getIndicatorValue()) < 0;
    }

    private boolean isHighPriceLowerParabolicSAR(int index) {
        return originalData[index].getHigh().compareTo(psarResult[index].getIndicatorValue()) < 0;
    }

    private boolean isMacdSignalLineLowerZero(int index) {
        return macdResult[index].getSignalLineValue().compareTo(ZERO) < 0;
    }

    private boolean isMacdSignalLineLowerParabolicSW(int index) {
        return macdResult[index].getSignalLineValue().compareTo(pswResult[index].getIndicatorValue()) < 0;
    }

    private void defineShortStopLose(int currentIndex) {
        if (currentIndex > 1) {
            result[currentIndex].setStopLose(MathHelper.max(
                    originalData[currentIndex - 2].getHigh(),
                    originalData[currentIndex - 1].getHigh(),
                    originalData[currentIndex].getHigh()));
        } else {
            result[currentIndex].setStopLose(MathHelper.max(
                    originalData[currentIndex - 1].getHigh(),
                    originalData[currentIndex].getHigh()));
        }
    }

}
