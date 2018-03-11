package pro.crypto.helper;

import pro.crypto.model.tick.Tick;

import java.math.BigDecimal;

import static java.util.Objects.isNull;

public class TypicalPriceCounter {

    public static BigDecimal countTypicalPrice(Tick tick){
        if (isNull(tick) || isNull(tick.getClose()) || isNull(tick.getHigh()) || isNull(tick.getLow())) {
            return null;
        }
        return MathHelper.divide(tick.getHigh().add(tick.getLow()).add(tick.getClose()), new BigDecimal(3));
    }

}
