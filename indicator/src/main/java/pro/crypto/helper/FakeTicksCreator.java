package pro.crypto.helper;

import pro.crypto.exception.WrongIncomingParametersException;
import pro.crypto.model.tick.Tick;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Stream;

public class FakeTicksCreator {

    public static Tick[] createWithCloseOnly(BigDecimal[] values) {
        List<Tick> fakeTicks = new ArrayList<>();
        Stream.of(values)
                .filter(Objects::nonNull)
                .forEach(indicatorValue -> {
                    fakeTicks.add(Tick.builder()
                            .close(indicatorValue)
                            .build());
                });
        return fakeTicks.toArray(new Tick[fakeTicks.size()]);
    }

    public static Tick[] createWithCloseAndTime(BigDecimal[] values, LocalDateTime[] tickTimes) {
        if (values.length != tickTimes.length) {
            throw new WrongIncomingParametersException("Array of price values and tick's times should have the same length");
        }
        Tick[] fakeTicks = new Tick[values.length];
        for (int i = 0; i < fakeTicks.length; i++) {
            fakeTicks[i] = Tick.builder()
                    .tickTime(tickTimes[i])
                    .close(values[i])
                    .build();
        }
        return fakeTicks;
    }

}
