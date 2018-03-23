package pro.crypto.indicators.tick.generator;

import pro.crypto.model.tick.Tick;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public abstract class AbstractGenerator {

    LocalDateTime startDateTime;

    public abstract Tick[] generate();

    void plusFifteenMinutes() {
        this.startDateTime = this.startDateTime.plusMinutes(15);
    }

    void plusOneDay() {
        this.startDateTime = this.startDateTime.plusDays(1);
    }

    Tick generateTickWithCloseOnly(double close) {
        return Tick.builder()
                .close(generateBigDecimalWithScale(close))
                .tickTime(this.startDateTime)
                .build();
    }

    Tick generateFullTick(double open, double high, double low, double close, double volume) {
        return Tick.builder()
                .open(generateBigDecimalWithScale(open))
                .high(generateBigDecimalWithScale(high))
                .low(generateBigDecimalWithScale(low))
                .close(generateBigDecimalWithScale(close))
                .baseVolume(generateBigDecimalWithScale(volume))
                .tickTime(this.startDateTime)
                .build();
    }

    private BigDecimal generateBigDecimalWithScale(double value) {
        return new BigDecimal(value).setScale(10, BigDecimal.ROUND_HALF_UP);
    }

}
