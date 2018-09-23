package pro.crypto.indicator.tick.generator;

import pro.crypto.model.tick.Tick;

import java.time.LocalDateTime;

import static pro.crypto.helper.MathHelper.toBigDecimal;

public abstract class TickAbstractGenerator {

    LocalDateTime startDateTime;

    public abstract Tick[] generate();

    void plusOneDay() {
        this.startDateTime = this.startDateTime.plusDays(1);
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
