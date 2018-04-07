package pro.crypto.helper;

import pro.crypto.model.tick.Tick;

import java.math.BigDecimal;
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

}
