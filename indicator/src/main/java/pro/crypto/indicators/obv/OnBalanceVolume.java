package pro.crypto.indicators.obv;

import pro.crypto.model.Indicator;
import pro.crypto.model.IndicatorType;
import pro.crypto.model.request.OBVCreationRequest;
import pro.crypto.model.result.OBVResult;
import pro.crypto.model.tick.Tick;

import java.math.BigDecimal;

import static java.util.Objects.isNull;
import static pro.crypto.helper.MathHelper.scaleAndRoundValue;
import static pro.crypto.model.IndicatorType.ON_BALANCE_VOLUME;

public class OnBalanceVolume implements Indicator<OBVResult> {

    private final Tick[] originalData;

    private OBVResult[] result;

    public OnBalanceVolume(OBVCreationRequest request) {
        this.originalData = request.getOriginalData();
        checkOriginalData(originalData);
    }


    @Override
    public IndicatorType getType() {
        return ON_BALANCE_VOLUME;
    }

    @Override
    public void calculate() {
        result = new OBVResult[originalData.length];
        countOnBalanceVolumesValues();
    }

    @Override
    public OBVResult[] getResult() {
        if (isNull(result)) {
            calculate();
        }
        return result;
    }

    private void countOnBalanceVolumesValues() {
        fillInFirstIndicatorPosition();
        fillInRemainPositions();
    }

    private void fillInFirstIndicatorPosition() {
        result[0] = new OBVResult(originalData[0].getTickTime(), scaleAndRoundValue(originalData[0].getBaseVolume()));
    }

    private void fillInRemainPositions() {
        for (int i = 1; i < originalData.length; i++) {
            result[i] = countOnBalanceVolumeValue(i);
        }
    }

    private OBVResult countOnBalanceVolumeValue(int currentIndex) {
        int priceComparing = compareCurrentCloseWithPrevious(currentIndex);
        if (priceComparing == 0) {
            return buildSamePriceResult(currentIndex);
        } else if (priceComparing < 0) {
            return buildFallingPriceResult(currentIndex);
        }
        return buildRisingPriceResult(currentIndex);
    }

    private OBVResult buildSamePriceResult(int currentIndex) {
        return new OBVResult(
                originalData[currentIndex].getTickTime(),
                scaleAndRoundValue(result[currentIndex - 1].getIndicatorValue().subtract(new BigDecimal(1)))
        );
    }

    private OBVResult buildFallingPriceResult(int currentIndex) {
        return new OBVResult(
                originalData[currentIndex].getTickTime(),
                scaleAndRoundValue(result[currentIndex - 1].getIndicatorValue().subtract(originalData[currentIndex].getBaseVolume()))
        );
    }

    private OBVResult buildRisingPriceResult(int currentIndex) {
        return new OBVResult(
                originalData[currentIndex].getTickTime(),
                scaleAndRoundValue(result[currentIndex - 1].getIndicatorValue().add(originalData[currentIndex].getBaseVolume()))
        );
    }

    private int compareCurrentCloseWithPrevious(int currentIndex) {
        return originalData[currentIndex].getClose().compareTo(originalData[currentIndex - 1].getClose());
    }

}
