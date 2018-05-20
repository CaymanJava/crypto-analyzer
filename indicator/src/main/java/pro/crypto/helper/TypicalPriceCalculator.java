package pro.crypto.helper;

import pro.crypto.model.tick.Tick;

import java.math.BigDecimal;
import java.util.stream.Stream;

import static java.util.Objects.isNull;

public class TypicalPriceCalculator {

    public static BigDecimal[] calculateTypicalPrices(Tick[] data) {
        return Stream.of(data)
                .map(TypicalPriceCalculator::calculateTypicalPrice)
                .toArray(BigDecimal[]::new);
    }

    private static BigDecimal calculateTypicalPrice(Tick tick){
        if (isNull(tick) || isNull(tick.getClose()) || isNull(tick.getHigh()) || isNull(tick.getLow())) {
            return null;
        }
        return MathHelper.divide(tick.getHigh().add(tick.getLow()).add(tick.getClose()), new BigDecimal(3));
    }

}
