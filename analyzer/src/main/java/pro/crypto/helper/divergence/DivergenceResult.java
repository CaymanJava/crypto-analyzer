package pro.crypto.helper.divergence;

import lombok.*;
import pro.crypto.model.Signal;

import static pro.crypto.model.Signal.BUY;
import static pro.crypto.model.Signal.SELL;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class DivergenceResult {

    private DivergenceType divergenceType;

    private DivergenceClass divergenceClass;

    private int indexFrom;

    private int indexTo;

    public Signal recognizeSignal() {
        switch (divergenceType) {
            case BEARISH:
                return SELL;
            case BULLISH:
                return BUY;
            default:
                return null;
        }
    }

}
