package pro.crypto.moving.average;


import pro.crypto.model.tick.Tick;

import java.math.BigDecimal;
import java.time.LocalDateTime;

class FifteenMinTickWithClosePriceOnlyGenerator {

    private LocalDateTime dateTime;

    FifteenMinTickWithClosePriceOnlyGenerator(LocalDateTime dateTime) {
        this.dateTime = dateTime;
    }

    Tick[] generate() {
        return new Tick[]{
                generateTick(generateBigDecimalWithScale(6.5)),
                generateTick(generateBigDecimalWithScale(5.8)),
                generateTick(generateBigDecimalWithScale(7.2)),
                generateTick(generateBigDecimalWithScale(7.1)),
                generateTick(generateBigDecimalWithScale(6.9)),
                generateTick(generateBigDecimalWithScale(6.8)),
                generateTick(generateBigDecimalWithScale(5.9)),
                generateTick(generateBigDecimalWithScale(5.8)),
                generateTick(generateBigDecimalWithScale(6.2)),
                generateTick(generateBigDecimalWithScale(6.4)),
                generateTick(generateBigDecimalWithScale(6.3)),
                generateTick(generateBigDecimalWithScale(6.5)),
                generateTick(generateBigDecimalWithScale(6.6)),
                generateTick(generateBigDecimalWithScale(5.0)),
                generateTick(generateBigDecimalWithScale(7.0)),
                generateTick(generateBigDecimalWithScale(7.1)),
                generateTick(generateBigDecimalWithScale(7.4))
        };
    }

    private BigDecimal generateBigDecimalWithScale(double value) {
        return new BigDecimal(value).setScale(10, BigDecimal.ROUND_HALF_UP);
    }

    private Tick generateTick(BigDecimal close) {
        Tick tick = Tick.builder()
                .close(close)
                .tickTime(this.dateTime)
                .build();
        plusFifteenMinutes();
        return tick;
    }

    private void plusFifteenMinutes() {
        this.dateTime = this.dateTime.plusMinutes(15);
    }

}
