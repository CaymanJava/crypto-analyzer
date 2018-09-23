package pro.crypto.helper;

import pro.crypto.model.tick.PriceType;
import pro.crypto.model.tick.Tick;

import java.math.BigDecimal;
import java.util.stream.Stream;

public class PriceExtractor {

    public static BigDecimal[] extract(Tick[] ticks, PriceType priceType) {
        return Stream.of(ticks)
                .map(tick -> tick.getPriceByType(priceType))
                .toArray(BigDecimal[]::new);
    }

}
