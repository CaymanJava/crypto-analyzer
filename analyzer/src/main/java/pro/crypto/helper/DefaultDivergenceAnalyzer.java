package pro.crypto.helper;

import pro.crypto.helper.divergence.Divergence;
import pro.crypto.helper.divergence.DivergenceRequest;
import pro.crypto.helper.divergence.DivergenceResult;
import pro.crypto.model.analyzer.Signal;
import pro.crypto.model.tick.Tick;

import java.math.BigDecimal;
import java.util.stream.Stream;

import static pro.crypto.model.analyzer.Signal.BUY;
import static pro.crypto.model.analyzer.Signal.SELL;

public class DefaultDivergenceAnalyzer {

    private final Tick[] originalData;
    private final BigDecimal[] indicatorValues;

    public DefaultDivergenceAnalyzer(Tick[] originalData, BigDecimal[] indicatorValues) {
        this.originalData = originalData;
        this.indicatorValues = indicatorValues;
    }

    public Signal[] analyze() {
        DivergenceResult[] divergences = new Divergence(buildDivergenceRequest()).find();
        Signal[] signals = new Signal[indicatorValues.length];
        Stream.of(divergences)
                .filter(this::isPriceConfirm)
                .forEach(divergence -> signals[divergence.getIndexTo() + 1] = recognizeSignal(divergence));
        return signals;
    }

    private DivergenceRequest buildDivergenceRequest() {
        return DivergenceRequest.builder()
                .originalData(originalData)
                .indicatorValues(indicatorValues)
                .build();
    }

    private boolean isPriceConfirm(DivergenceResult divergenceResult) {
        switch (divergenceResult.getDivergenceType()) {
            case BEARISH:
                return isPriceExist(divergenceResult.getIndexTo())
                        && isLastPriceLowerPrevious(divergenceResult.getIndexTo());
            case BULLISH:
                return isPriceExist(divergenceResult.getIndexTo())
                        && !isLastPriceLowerPrevious(divergenceResult.getIndexTo());
            default:
                return false;
        }
    }

    private boolean isLastPriceLowerPrevious(int indexTo) {
        return originalData[indexTo + 1].getClose().compareTo(originalData[indexTo].getClose()) < 0;
    }

    private boolean isPriceExist(int indexTo) {
        return indexTo < originalData.length - 1;
    }

    private Signal recognizeSignal(DivergenceResult result) {
        switch (result.getDivergenceType()) {
            case BEARISH:
                return SELL;
            case BULLISH:
                return BUY;
            default:
                return null;
        }
    }

}
