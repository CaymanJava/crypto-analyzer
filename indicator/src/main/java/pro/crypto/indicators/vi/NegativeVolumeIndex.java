package pro.crypto.indicators.vi;

import pro.crypto.model.IndicatorType;
import pro.crypto.model.request.VIRequest;

import java.math.BigDecimal;

import static pro.crypto.model.IndicatorType.NEGATIVE_VOLUME_INDEX;

public class NegativeVolumeIndex extends VolumeIndex {

    NegativeVolumeIndex(VIRequest request) {
        super(request);
    }

    @Override
    public IndicatorType getType() {
        return NEGATIVE_VOLUME_INDEX;
    }

    @Override
    BigDecimal calculateVolumeIndexValue(BigDecimal[] negativeVolumeIndexes, int currentIndex) {
        return isCurrentVolumeMoreThanPrevious(currentIndex)
                ? negativeVolumeIndexes[currentIndex - 1]
                : calculateVolumeIndexValue(negativeVolumeIndexes[currentIndex - 1], currentIndex);
    }

}
