package pro.crypto.indicators.alligator;

import pro.crypto.helper.FakeTicksCreator;
import pro.crypto.helper.MathHelper;
import pro.crypto.indicators.ma.MovingAverageFactory;
import pro.crypto.model.Indicator;
import pro.crypto.model.IndicatorType;
import pro.crypto.model.Shift;
import pro.crypto.model.request.AlligatorRequest;
import pro.crypto.model.request.MARequest;
import pro.crypto.model.result.AlligatorResult;
import pro.crypto.model.result.MAResult;
import pro.crypto.model.tick.Tick;
import pro.crypto.model.tick.TimeFrame;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.stream.Stream;

import static java.util.Objects.isNull;
import static pro.crypto.model.IndicatorType.*;
import static pro.crypto.model.ShiftType.RIGHT;
import static pro.crypto.model.tick.PriceType.CLOSE;

public class Alligator implements Indicator<AlligatorResult> {

    private final Tick[] originalData;
    private final int jawPeriod;
    private final int jawOffset;
    private final int teethPeriod;
    private final int teethOffset;
    private final int lipsPeriod;
    private final int lipsOffset;
    private final TimeFrame timeFrame;

    private AlligatorResult[] result;

    public Alligator(AlligatorRequest request) {
        this.originalData = request.getOriginalData();
        this.jawPeriod = request.getJawPeriod();
        this.jawOffset = request.getJawOffset();
        this.teethPeriod = request.getTeethPeriod();
        this.teethOffset = request.getTeethOffset();
        this.lipsPeriod = request.getLipsPeriod();
        this.lipsOffset = request.getLipsOffset();
        this.timeFrame = request.getTimeFrame();
        checkIncomingData();
    }

    @Override
    public IndicatorType getType() {
        return ALLIGATOR;
    }

    @Override
    public void calculate() {
        result = new AlligatorResult[originalData.length];
        BigDecimal[] medianPrices = calculateMedianPrices();
        BigDecimal[] jawValues = calculateJawValues(medianPrices);
        BigDecimal[] teethValues = calculateTeethValues(medianPrices);
        BigDecimal[] lipsValues = calculateLipsValues(medianPrices);
        buildAlligatorResult(jawValues, teethValues, lipsValues);
    }

    @Override
    public AlligatorResult[] getResult() {
        if (isNull(result)) {
            calculate();
        }
        return result;
    }

    private void checkIncomingData() {
        checkOriginalData(originalData);
        checkOriginalDataSize(originalData, jawPeriod);
        checkOriginalDataSize(originalData, teethPeriod);
        checkOriginalDataSize(originalData, lipsPeriod);
        checkPeriod(jawPeriod);
        checkPeriod(teethPeriod);
        checkPeriod(lipsPeriod);
        checkDisplaced(jawOffset);
        checkDisplaced(teethOffset);
        checkDisplaced(lipsOffset);
    }

    private BigDecimal[] calculateMedianPrices() {
        return Stream.of(originalData)
                .map(this::calculateMedianPrice)
                .toArray(BigDecimal[]::new);
    }

    private BigDecimal calculateMedianPrice(Tick tick) {
        return MathHelper.divide(tick.getHigh().add(tick.getLow()), new BigDecimal(2));
    }

    private BigDecimal[] calculateJawValues(BigDecimal[] medianPrices) {
        return calculateDisplacedMovingAverage(medianPrices, jawPeriod, jawOffset);
    }

    private BigDecimal[] calculateTeethValues(BigDecimal[] medianPrices) {
        return calculateDisplacedMovingAverage(medianPrices, teethPeriod, teethOffset);
    }

    private BigDecimal[] calculateLipsValues(BigDecimal[] medianPrices) {
        return calculateDisplacedMovingAverage(medianPrices, lipsPeriod, lipsOffset);
    }

    private BigDecimal[] calculateDisplacedMovingAverage(BigDecimal[] medianPrices, int period, int displaced) {
        return extractMAResult(MovingAverageFactory.create(buildMARequest(medianPrices, period, displaced)).getResult());
    }

    private MARequest buildMARequest(BigDecimal[] medianPrices, int period, int displaced) {
        return MARequest.builder()
                .originalData(createFakeTicks(medianPrices))
                .priceType(CLOSE)
                .indicatorType(DISPLACED_MOVING_AVERAGE)
                .originalIndicatorType(SMOOTHED_MOVING_AVERAGE)
                .period(period)
                .shift(new Shift(RIGHT, displaced, timeFrame))
                .build();
    }

    private Tick[] createFakeTicks(BigDecimal[] medianPrices) {
        return FakeTicksCreator.createWithCloseAndTime(medianPrices, extractTickTimes());
    }

    private LocalDateTime[] extractTickTimes() {
        return Stream.of(originalData)
                .map(Tick::getTickTime)
                .toArray(LocalDateTime[]::new);
    }

    private BigDecimal[] extractMAResult(MAResult[] result) {
        return Stream.of(result)
                .map(MAResult::getIndicatorValue)
                .toArray(BigDecimal[]::new);
    }

    private void buildAlligatorResult(BigDecimal[] jawValues, BigDecimal[] teethValues, BigDecimal[] lipsValues) {
        for (int currentIndex = 0; currentIndex < result.length; currentIndex++) {
            result[currentIndex] = new AlligatorResult(originalData[currentIndex].getTickTime(), jawValues[currentIndex],
                    teethValues[currentIndex], lipsValues[currentIndex]);
        }
    }

}
