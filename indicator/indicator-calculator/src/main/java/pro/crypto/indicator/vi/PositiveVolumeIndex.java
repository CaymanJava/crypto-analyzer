package pro.crypto.indicator.vi;

import pro.crypto.model.indicator.IndicatorType;

import java.math.BigDecimal;

import static pro.crypto.model.indicator.IndicatorType.POSITIVE_VOLUME_INDEX;

public class PositiveVolumeIndex extends VolumeIndex {

    PositiveVolumeIndex(VIRequest request) {
        super(request);
    }

    @Override
    public IndicatorType getType() {
        return POSITIVE_VOLUME_INDEX;
    }

    @Override
    BigDecimal calculateVolumeIndexValue(BigDecimal[] positiveVolumeIndexes, int currentIndex) {
        return isCurrentVolumeMoreThanPrevious(currentIndex)
                ? calculateVolumeIndexValue(positiveVolumeIndexes[currentIndex - 1], currentIndex)
                : positiveVolumeIndexes[currentIndex - 1];
    }

}
