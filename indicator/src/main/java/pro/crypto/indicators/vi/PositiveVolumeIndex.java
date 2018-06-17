package pro.crypto.indicators.vi;

import pro.crypto.model.IndicatorType;
import pro.crypto.model.request.VIRequest;

import java.math.BigDecimal;

import static pro.crypto.model.IndicatorType.POSITIVE_VOLUME_INDEX;

public class PositiveVolumeIndex extends VolumeIndex {

    PositiveVolumeIndex(VIRequest request) {
        super(request);
    }

    @Override
    public IndicatorType getType() {
        return POSITIVE_VOLUME_INDEX;
    }

    @Override
    BigDecimal calculateVolumeIndex(BigDecimal[] positiveVolumeIndexes, int currentIndex) {
        return isCurrentVolumeMoreThanPrevious(currentIndex)
                ? calculateVolumeIndexValue(positiveVolumeIndexes[currentIndex - 1], currentIndex)
                : positiveVolumeIndexes[currentIndex - 1];
    }

}
