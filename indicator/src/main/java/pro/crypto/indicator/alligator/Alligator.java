package pro.crypto.indicator.alligator;

import pro.crypto.helper.FakeTicksCreator;
import pro.crypto.helper.IndicatorResultExtractor;
import pro.crypto.helper.MedianPriceCalculator;
import pro.crypto.indicator.ma.MARequest;
import pro.crypto.indicator.ma.MovingAverageFactory;
import pro.crypto.model.Indicator;
import pro.crypto.model.IndicatorRequest;
import pro.crypto.model.IndicatorType;
import pro.crypto.model.Shift;
import pro.crypto.model.SimpleIndicatorResult;
import pro.crypto.model.tick.Tick;
import pro.crypto.model.tick.TimeFrame;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import static java.util.Objects.isNull;
import static pro.crypto.model.IndicatorType.ALLIGATOR;
import static pro.crypto.model.IndicatorType.DISPLACED_MOVING_AVERAGE;
import static pro.crypto.model.IndicatorType.SMOOTHED_MOVING_AVERAGE;
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

    public Alligator(IndicatorRequest creationRequest) {
        AlligatorRequest request = (AlligatorRequest) creationRequest;
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
        BigDecimal[] medianPrices = MedianPriceCalculator.calculate(originalData);
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
        checkPeriod(jawPeriod);
        checkDisplaced(jawOffset);
        checkOriginalDataSize(originalData, teethPeriod);
        checkPeriod(teethPeriod);
        checkDisplaced(teethOffset);
        checkOriginalDataSize(originalData, lipsPeriod);
        checkPeriod(lipsPeriod);
        checkDisplaced(lipsOffset);
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
        return IndicatorResultExtractor.extractIndicatorValues(calculateMovingAverage(medianPrices, period, displaced));
    }

    private SimpleIndicatorResult[] calculateMovingAverage(BigDecimal[] medianPrices, int period, int displaced) {
        return MovingAverageFactory.create(buildMARequest(medianPrices, period, displaced)).getResult();
    }

    private IndicatorRequest buildMARequest(BigDecimal[] medianPrices, int period, int displaced) {
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

    private void buildAlligatorResult(BigDecimal[] jawValues, BigDecimal[] teethValues, BigDecimal[] lipsValues) {
        IntStream.range(0, result.length)
                .forEach(idx -> result[idx] = new AlligatorResult(originalData[idx].getTickTime(), jawValues[idx],
                        teethValues[idx], lipsValues[idx]));
    }

}
