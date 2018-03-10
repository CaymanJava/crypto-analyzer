package pro.crypto.indicators.tick.generator;

import pro.crypto.model.tick.Tick;

import java.time.LocalDateTime;

public class FifteenMinTickWithClosePriceOnlyGenerator extends AbstractGenerator {

    public FifteenMinTickWithClosePriceOnlyGenerator(LocalDateTime startDateTime) {
        this.startDateTime = startDateTime;
    }

    public Tick[] generate() {
        return new Tick[]{
                generateTick(6.5),
                generateTick(5.8),
                generateTick(7.2),
                generateTick(7.1),
                generateTick(6.9),
                generateTick(6.8),
                generateTick(5.9),
                generateTick(5.8),
                generateTick(6.2),
                generateTick(6.4),
                generateTick(6.3),
                generateTick(6.5),
                generateTick(6.6),
                generateTick(5.0),
                generateTick(7.0),
                generateTick(7.1),
                generateTick(7.4)
        };
    }

    private Tick generateTick(double close) {
        Tick tick = generateTickWithCloseOnly(close);
        plusFifteenMinutes();
        return tick;
    }

}
