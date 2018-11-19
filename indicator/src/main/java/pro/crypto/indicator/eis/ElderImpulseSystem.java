package pro.crypto.indicator.eis;

import pro.crypto.indicator.ma.MARequest;
import pro.crypto.indicator.ma.MAResult;
import pro.crypto.indicator.ma.MovingAverageFactory;
import pro.crypto.indicator.macd.MACDRequest;
import pro.crypto.indicator.macd.MACDResult;
import pro.crypto.indicator.macd.MovingAverageConvergenceDivergence;
import pro.crypto.model.Indicator;
import pro.crypto.model.IndicatorRequest;
import pro.crypto.model.IndicatorType;
import pro.crypto.model.tick.PriceType;
import pro.crypto.model.tick.Tick;

import java.util.stream.IntStream;

import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;
import static pro.crypto.indicator.eis.BarColor.BLUE;
import static pro.crypto.indicator.eis.BarColor.GREEN;
import static pro.crypto.indicator.eis.BarColor.RED;
import static pro.crypto.model.IndicatorType.ELDER_IMPULSE_SYSTEM;

public class ElderImpulseSystem implements Indicator<EISResult> {

    private final Tick[] originalData;
    private final int maPeriod;
    private final IndicatorType maType;
    private final PriceType maPriceType;
    private final IndicatorType macdMaType;
    private final PriceType macdPriceType;
    private final int macdFastPeriod;
    private final int macdSlowPeriod;
    private final int macdSignalPeriod;

    private MAResult[] maResults;
    private MACDResult[] macdResults;
    private EISResult[] result;

    public ElderImpulseSystem(IndicatorRequest indicatorRequest) {
        EISRequest request = (EISRequest) indicatorRequest;
        this.originalData = request.getOriginalData();
        this.maPeriod = request.getMaPeriod();
        this.maType = request.getMaType();
        this.maPriceType = request.getMaPriceType();
        this.macdMaType = request.getMacdMaType();
        this.macdPriceType = request.getMacdPriceType();
        this.macdFastPeriod = request.getMacdFastPeriod();
        this.macdSlowPeriod = request.getMacdSlowPeriod();
        this.macdSignalPeriod = request.getMacdSignalPeriod();
        checkIncomingData();
    }

    @Override
    public IndicatorType getType() {
        return ELDER_IMPULSE_SYSTEM;
    }

    @Override
    public void calculate() {
        calculateMovingAverage();
        calculateMovingAverageConvergenceDivergence();
        calculateElderImpulseResults();
    }

    @Override
    public EISResult[] getResult() {
        if (isNull(result)) {
            calculate();
        }
        return result;
    }

    private void checkIncomingData() {
        checkOriginalData(originalData);
        checkOriginalDataSize(originalData, maPeriod);
        checkOriginalDataSize(originalData, macdSlowPeriod + macdSignalPeriod);
        checkPriceType(maPriceType);
        checkPriceType(macdPriceType);
        checkMovingAverageType(maType);
        checkMovingAverageType(macdMaType);
        checkPeriod(maPeriod);
        checkPeriod(macdSlowPeriod);
        checkPeriod(macdFastPeriod);
        checkPeriod(macdSignalPeriod);
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

    private void calculateElderImpulseResults() {
        result = IntStream.range(0, originalData.length)
                .mapToObj(this::calculateElderImpulseResult)
                .toArray(EISResult[]::new);
    }

    private EISResult calculateElderImpulseResult(int currentIndex) {
        return isPossibleDefineIndicator(currentIndex)
                ? defineElderImpulseResult(currentIndex)
                : new EISResult(originalData[currentIndex].getTickTime(), BLUE);
    }

    private boolean isPossibleDefineIndicator(int currentIndex) {
        return currentIndex > 0
                && nonNull(maResults[currentIndex - 1].getIndicatorValue())
                && nonNull(maResults[currentIndex].getIndicatorValue())
                && nonNull(macdResults[currentIndex - 1].getIndicatorValue())
                && nonNull(macdResults[currentIndex].getIndicatorValue());
    }

    private EISResult defineElderImpulseResult(int currentIndex) {
        return new EISResult(originalData[currentIndex].getTickTime(), defineBarColor(currentIndex));
    }

    private BarColor defineBarColor(int currentIndex) {
        if (isGreenBar(currentIndex)) {
            return GREEN;
        }

        if (isRedBar(currentIndex)) {
            return RED;
        }

        return BLUE;
    }

    private boolean isGreenBar(int currentIndex) {
        return isGreenMACondition(currentIndex) && isGreenMACDCondition(currentIndex);
    }

    private boolean isGreenMACondition(int currentIndex) {
        return maResults[currentIndex].getIndicatorValue()
                .compareTo(maResults[currentIndex - 1].getIndicatorValue()) > 0;
    }

    private boolean isGreenMACDCondition(int currentIndex) {
        return macdResults[currentIndex].getIndicatorValue()
                .compareTo(macdResults[currentIndex - 1].getIndicatorValue()) > 0;
    }

    private boolean isRedBar(int currentIndex) {
        return isRedMACondition(currentIndex) && isRedMACDCondition(currentIndex);
    }

    private boolean isRedMACondition(int currentIndex) {
        return maResults[currentIndex].getIndicatorValue()
                .compareTo(maResults[currentIndex - 1].getIndicatorValue()) < 0;
    }

    private boolean isRedMACDCondition(int currentIndex) {
        return macdResults[currentIndex].getIndicatorValue()
                .compareTo(macdResults[currentIndex - 1].getIndicatorValue()) < 0;
    }

}
