package pro.crypto.indicators.tick.generator;

import pro.crypto.model.tick.Tick;

import java.time.LocalDateTime;

public class OneDayTickWithClosePriceGenerator extends AbstractGenerator {

    public OneDayTickWithClosePriceGenerator(LocalDateTime startDateTime) {
        this.startDateTime = startDateTime;
    }

    @Override
    public Tick[] generate() {
        return new Tick[]{
                generateTick(5.4),
                generateTick(5.6),
                generateTick(6.1),
                generateTick(6.4),
                generateTick(6.5),
                generateTick(6.44),
                generateTick(6.2),
                generateTick(6.1),
                generateTick(6),
                generateTick(6),
                generateTick(5.9),
                generateTick(6.4),
                generateTick(6.52),
                generateTick(6.45),
                generateTick(6.43),
                generateTick(6.12),
                generateTick(6.15),
                generateTick(6.66),
                generateTick(6.78),
                generateTick(6.56),
                generateTick(6.88),
                generateTick(6.99),
                generateTick(7),
                generateTick(7.3),
                generateTick(7),
                generateTick(7.22),
                generateTick(7.85),
                generateTick(7.54),
                generateTick(7.4),
                generateTick(7.8),
                generateTick(7),
                generateTick(6.8),
                generateTick(6.5),
                generateTick(6.1),
                generateTick(6.55),
                generateTick(6.8),
                generateTick(7.1),
                generateTick(7.33),
                generateTick(7.43),
                generateTick(7.34),
                generateTick(7.55),
                generateTick(7.81),
                generateTick(7.9),
                generateTick(8.4),
                generateTick(8.23),
                generateTick(8.56),
                generateTick(8.1),
                generateTick(7.8),
                generateTick(7.5),
                generateTick(6.05)
        };
    }

    private Tick generateTick(double close) {
        Tick tick = generateTickWithCloseOnly(close);
        plusOneDay();
        return tick;
    }

}
