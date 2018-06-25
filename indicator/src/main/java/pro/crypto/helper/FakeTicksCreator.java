package pro.crypto.helper;

import pro.crypto.exception.WrongIncomingParametersException;
import pro.crypto.model.tick.Tick;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Objects;
import java.util.stream.IntStream;
import java.util.stream.Stream;

public class FakeTicksCreator {

    public static Tick[] createWithCloseOnly(BigDecimal[] values) {
        return Stream.of(values)
                .filter(Objects::nonNull)
                .map(FakeTicksCreator::buildTickWithClose)
                .toArray(Tick[]::new);
    }

    public static Tick[] createWithCloseAndTime(BigDecimal[] values, LocalDateTime[] tickTimes) {
        if (values.length != tickTimes.length) {
            throw new WrongIncomingParametersException("Array of price values and tick's times should have the same length");
        }
        return IntStream.range(0, values.length)
                .mapToObj(idx -> buildTickWithCloseAndTime(values[idx], tickTimes[idx]))
                .toArray(Tick[]::new);
    }

    private static Tick buildTickWithClose(BigDecimal value) {
        return Tick.builder()
                .close(value)
                .build();
    }

    private static Tick buildTickWithCloseAndTime(BigDecimal value, LocalDateTime time) {
        return Tick.builder()
                .close(value)
                .tickTime(time)
                .build();
    }

}
