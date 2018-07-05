package pro.crypto.indicator.tick.generator;

import pro.crypto.model.tick.Tick;

import java.time.LocalDateTime;

import static pro.crypto.helper.MathHelper.toBigDecimal;

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
                .close(toBigDecimal(close))
                .tickTime(this.startDateTime)
                .build();
    }

    Tick generateFullTick(double open, double high, double low, double close, double volume) {
        return Tick.builder()
                .open(toBigDecimal(open))
                .high(toBigDecimal(high))
                .low(toBigDecimal(low))
                .close(toBigDecimal(close))
                .baseVolume(toBigDecimal(volume))
                .tickTime(this.startDateTime)
                .build();
    }

}
