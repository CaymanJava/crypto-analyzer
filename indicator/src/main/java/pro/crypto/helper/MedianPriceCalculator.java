package pro.crypto.helper;

import pro.crypto.model.tick.Tick;

import java.math.BigDecimal;
import java.util.stream.Stream;

public class MedianPriceCalculator {

    public static BigDecimal[] calculate(Tick[] data) {
        return Stream.of(data)
                .map(MedianPriceCalculator::calculateMedianPrice)
                .toArray(BigDecimal[]::new);
    }

    private static BigDecimal calculateMedianPrice(Tick tick) {
        return MathHelper.divide(tick.getHigh().add(tick.getLow()), new BigDecimal(2));
    }

}
