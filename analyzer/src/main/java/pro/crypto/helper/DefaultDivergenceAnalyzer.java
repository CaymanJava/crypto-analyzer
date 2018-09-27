package pro.crypto.helper;

import pro.crypto.helper.divergence.Divergence;
import pro.crypto.helper.divergence.DivergenceRequest;
import pro.crypto.helper.divergence.DivergenceResult;
import pro.crypto.model.Signal;
import pro.crypto.model.tick.Tick;

import java.math.BigDecimal;
import java.util.stream.Stream;

public class DefaultDivergenceAnalyzer {

    public Signal[] analyze(Tick[] originalData, BigDecimal[] indicatorValues) {
        DivergenceResult[] divergences = new Divergence(buildDivergenceRequest(originalData, indicatorValues)).find();
        Signal[] signalStrengths = new Signal[indicatorValues.length];
        Stream.of(divergences)
                .filter(divergence -> isPriceConfirm(divergence, originalData))
                .forEach(divergence -> signalStrengths[divergence.getIndexTo() + 1] = divergence.recognizeSignal());
        return signalStrengths;
    }

    private DivergenceRequest buildDivergenceRequest(Tick[] originalData, BigDecimal[] indicatorValues) {
        return DivergenceRequest.builder()
                .originalData(originalData)
                .indicatorValues(indicatorValues)
                .build();
    }

    private boolean isPriceConfirm(DivergenceResult divergenceResult, Tick[] originalData) {
        switch (divergenceResult.getDivergenceType()) {
            case BEARISH:
                return isPriceExist(divergenceResult.getIndexTo(), originalData)
                        && isLastPriceLowerPrevious(divergenceResult.getIndexTo(), originalData);
            case BULLISH:
                return isPriceExist(divergenceResult.getIndexTo(), originalData)
                        && !isLastPriceLowerPrevious(divergenceResult.getIndexTo(), originalData);
            default:
                return false;
        }
    }

    private boolean isLastPriceLowerPrevious(int indexTo, Tick[] originalData) {
        return originalData[indexTo + 1].getClose().compareTo(originalData[indexTo].getClose()) < 0;
    }

    private boolean isPriceExist(int indexTo, Tick[] originalData) {
        return indexTo < originalData.length - 1;
    }

}
