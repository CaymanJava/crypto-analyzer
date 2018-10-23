package pro.crypto.helper;

import pro.crypto.model.tick.PriceType;
import pro.crypto.model.tick.Tick;

import java.math.BigDecimal;
import java.util.stream.Stream;

public class PriceVolumeExtractor {

    public static BigDecimal[] extractPrices(Tick[] ticks, PriceType priceType) {
        return Stream.of(ticks)
                .map(tick -> tick.getPriceByType(priceType))
                .toArray(BigDecimal[]::new);
    }

    public static BigDecimal[] extractBaseVolumes(Tick[] ticks) {
        return Stream.of(ticks)
                .map(Tick::getBaseVolume)
                .toArray(BigDecimal[]::new);
    }

}
