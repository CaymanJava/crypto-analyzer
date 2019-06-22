package pro.crypto.indicator.pivot;

import pro.crypto.helper.MathHelper;
import pro.crypto.model.Indicator;
import pro.crypto.model.tick.Tick;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Map;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import static java.util.Arrays.stream;
import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;
import static java.util.stream.Collectors.toMap;

public abstract class PivotPoints implements Indicator<PivotResult> {

    protected final Tick[] originalData;
    private final Tick[] resultTickData;

    protected PivotResult[] result;
    BigDecimal pivot;

    private PivotResult[] oneDayPivotPoints;
    private Map<LocalDateTime, PivotResult> oneDayDataPivotPoints;

    PivotPoints(Tick[] originalData, Tick[] resultTickData) {
        this.originalData = originalData;
        this.resultTickData = resultTickData;
        checkOriginalData(this.originalData);
        checkOriginalData(this.resultTickData);
    }

    @Override
    public void calculate() {
        calculatePivotPoints();
    }

    @Override
    public PivotResult[] getResult() {
        if (isNull(result)) {
            calculate();
        }
        return result;
    }

    abstract BigDecimal calculatePivot(int currentIndex);

    abstract BigDecimal calculateFirstResistance(int currentIndex);

    abstract BigDecimal calculateSecondResistance(int currentIndex);

    abstract BigDecimal calculateThirdResistance(int currentIndex);

    abstract BigDecimal calculateFourthResistance(int currentIndex);

    abstract BigDecimal calculateFirstSupport(int currentIndex);

    abstract BigDecimal calculateSecondSupport(int currentIndex);

    abstract BigDecimal calculateThirdSupport(int currentIndex);

    abstract BigDecimal calculateFourthSupport(int currentIndex);

    // (H + L + C) / 3
    BigDecimal calculateDefaultPivot(int currentIndex) {
        return MathHelper.average(originalData[currentIndex - 1].getHigh(), originalData[currentIndex - 1].getLow(), originalData[currentIndex - 1].getClose());
    }

    BigDecimal empty() {
        return null;
    }

    private void calculatePivotPoints() {
        calculateOneDayPivotPoints();
        groupOneDayPivotPointsByDate();
        defineOriginalDataPivotPoints();
    }

    private void calculateOneDayPivotPoints() {
        oneDayPivotPoints = new PivotResult[originalData.length];
        fillInFirstValue();
        IntStream.range(1, oneDayPivotPoints.length)
                .forEach(this::calculatePivotPoint);
    }

    private void groupOneDayPivotPointsByDate() {
        oneDayDataPivotPoints = Stream.of(oneDayPivotPoints)
                .collect(toMap(PivotResult::getTime, p -> p));
    }

    private void defineOriginalDataPivotPoints() {
        initResultArray();
        fillInResultPivot();
    }

    private void initResultArray() {
        result = stream(resultTickData)
                .map(originalData -> new PivotResult(originalData.getTickTime()))
                .toArray(PivotResult[]::new);
    }

    private void fillInResultPivot() {
        IntStream.range(0, result.length)
                .forEach(this::copyDayPivotPoints);
    }

    private void copyDayPivotPoints(int currentIndex) {
        LocalDateTime startDayPivotPoint = result[currentIndex].getTime().with(LocalTime.MIN);
        PivotResult oneDayPivot = oneDayDataPivotPoints.get(startDayPivotPoint);
        if (nonNull(oneDayPivot)) {
            result[currentIndex].copy(oneDayPivot);
        }
    }

    private void fillInFirstValue() {
        oneDayPivotPoints[0] = new PivotResult(originalData[0].getTickTime());
    }

    private void calculatePivotPoint(int currentIndex) {
        pivot = calculatePivot(currentIndex);
        BigDecimal firstResistance = calculateFirstResistance(currentIndex);
        BigDecimal secondResistance = calculateSecondResistance(currentIndex);
        BigDecimal thirdResistance = calculateThirdResistance(currentIndex);
        BigDecimal fourthResistance = calculateFourthResistance(currentIndex);
        BigDecimal firstSupport = calculateFirstSupport(currentIndex);
        BigDecimal secondSupport = calculateSecondSupport(currentIndex);
        BigDecimal thirdSupport = calculateThirdSupport(currentIndex);
        BigDecimal fourthSupport = calculateFourthSupport(currentIndex);
        oneDayPivotPoints[currentIndex] = new PivotResult(originalData[currentIndex].getTickTime(), pivot,
                firstResistance, secondResistance, thirdResistance, fourthResistance,
                firstSupport, secondSupport, thirdSupport, fourthSupport);
    }

}
