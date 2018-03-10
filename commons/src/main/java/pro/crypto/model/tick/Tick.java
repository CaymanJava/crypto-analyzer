package pro.crypto.model.tick;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import pro.crypto.exception.UnknownTypeException;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class Tick {

    private BigDecimal open;

    private BigDecimal high;

    private BigDecimal low;

    private BigDecimal close;

    private BigDecimal volume;

    private BigDecimal baseVolume;

    private LocalDateTime tickTime;

    public BigDecimal getPriceByType(PriceType priceType) {
        switch (priceType) {
            case HIGH:
                return high;
            case LOW:
                return low;
            case OPEN:
                return open;
            case CLOSE:
                return close;
            default:
                throw new UnknownTypeException("Unknown price type exception");
        }
    }

}
