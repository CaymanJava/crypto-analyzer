package pro.crypto.indicator.ha;

import pro.crypto.helper.MathHelper;
import pro.crypto.model.Indicator;
import pro.crypto.model.indicator.IndicatorType;
import pro.crypto.model.tick.Tick;
import pro.crypto.request.IndicatorRequest;

import java.math.BigDecimal;
import java.util.stream.IntStream;

import static java.util.Objects.isNull;
import static pro.crypto.model.indicator.IndicatorType.HEIKEN_ASHI;

public class HeikenAshi implements Indicator<HAResult> {

    private final Tick[] originalData;

    private HAResult[] result;

    public HeikenAshi(IndicatorRequest request) {
        this.originalData = request.getOriginalData();
        checkIncomingData();
    }

    @Override
    public IndicatorType getType() {
        return HEIKEN_ASHI;
    }

    @Override
    public void calculate() {
        initResultArray();
        calculateCandles();
    }

    @Override
    public HAResult[] getResult() {
        if (isNull(result)) {
            calculate();
        }
        return result;
    }

    private void checkIncomingData() {
        checkOriginalData(originalData);
    }

    private void initResultArray() {
        result = new HAResult[originalData.length];
    }

    private void calculateCandles() {
        IntStream.range(0, originalData.length)
                .forEach(this::calculateHeikenAshiCandle);
    }

    private void calculateHeikenAshiCandle(int currentIndex) {
        BigDecimal haClose = calculateHeikenAshiClose(currentIndex);
        BigDecimal haOpen = currentIndex == 0 ? calculateFirstHeikenAshiOpen() : calculateHeikenAshiOpen(currentIndex);
        BigDecimal haHigh = calculateHeikenAshiHigh(haClose, haOpen, currentIndex);
        BigDecimal haLow = calculateHeikenAshiLow(haClose, haOpen, currentIndex);
        result[currentIndex] = new HAResult(originalData[currentIndex].getTickTime(), haOpen, haHigh, haLow, haClose);
    }

    private BigDecimal calculateFirstHeikenAshiOpen() {
        return MathHelper.average(originalData[0].getOpen(), originalData[0].getClose());
    }

    private BigDecimal calculateHeikenAshiOpen(int currentIndex) {
        return MathHelper.average(result[currentIndex - 1].getOpen(), result[currentIndex - 1].getClose());
    }

    private BigDecimal calculateHeikenAshiClose(int index) {
        return MathHelper.average(originalData[index].getOpen(), originalData[index].getHigh(), originalData[index].getLow(), originalData[index].getClose());
    }

    private BigDecimal calculateHeikenAshiHigh(BigDecimal haClose, BigDecimal haOpen, int index) {
        return MathHelper.max(haClose, haOpen, originalData[index].getHigh());
    }

    private BigDecimal calculateHeikenAshiLow(BigDecimal haClose, BigDecimal haOpen, int index) {
        return MathHelper.min(haClose, haOpen, originalData[index].getLow());
    }

}
