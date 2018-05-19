package pro.crypto.helper;

import pro.crypto.model.tick.PriceType;
import pro.crypto.model.tick.Tick;

import java.math.BigDecimal;
import java.util.stream.Stream;

import static pro.crypto.model.tick.PriceType.*;

public class PriceExtractor {

    public static BigDecimal[] extractCloseValues(Tick[] ticks) {
        return extractValues(ticks, CLOSE);
    }

    public static BigDecimal[] extractHighValues(Tick[] ticks) {
        return extractValues(ticks, HIGH);
    }

    public static BigDecimal[] extractLowValues(Tick[] ticks) {
        return extractValues(ticks, LOW);
    }

    private static BigDecimal[] extractValues(Tick[] ticks, PriceType priceType) {
        return Stream.of(ticks)
                .map(tick -> tick.getPriceByType(priceType))
                .toArray(BigDecimal[]::new);
    }

}
