package pro.crypto.indicators.obv;

import pro.crypto.helper.MathHelper;
import pro.crypto.model.Indicator;
import pro.crypto.model.IndicatorType;
import pro.crypto.model.request.OBVRequest;
import pro.crypto.model.result.OBVResult;
import pro.crypto.model.tick.Tick;

import java.math.BigDecimal;
import java.util.stream.IntStream;

import static java.util.Objects.isNull;
import static pro.crypto.model.IndicatorType.ON_BALANCE_VOLUME;

public class OnBalanceVolume implements Indicator<OBVResult> {

    private final Tick[] originalData;

    private OBVResult[] result;

    public OnBalanceVolume(OBVRequest request) {
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
        calculateOnBalanceVolumesValues();
    }

    @Override
    public OBVResult[] getResult() {
        if (isNull(result)) {
            calculate();
        }
        return result;
    }

    private void calculateOnBalanceVolumesValues() {
        fillInFirstIndicatorPosition();
        fillInRemainPositions();
    }

    private void fillInFirstIndicatorPosition() {
        result[0] = new OBVResult(originalData[0].getTickTime(), MathHelper.scaleAndRound(originalData[0].getBaseVolume()));
    }

    private void fillInRemainPositions() {
        IntStream.range(1, result.length)
                .forEach(this::setOBVResult);
    }

    private void setOBVResult(int currentIndex) {
        result[currentIndex] = calculateOnBalanceVolumeValue(currentIndex);
    }

    private OBVResult calculateOnBalanceVolumeValue(int currentIndex) {
        int priceComparing = compareCurrentCloseWithPrevious(currentIndex);
        if (priceComparing == 0) {
            return buildSamePriceResult(currentIndex);
        }
        if (priceComparing < 0) {
            return buildFallingPriceResult(currentIndex);
        }
        return buildRisingPriceResult(currentIndex);
    }

    private OBVResult buildSamePriceResult(int currentIndex) {
        return new OBVResult(
                originalData[currentIndex].getTickTime(),
                MathHelper.scaleAndRound(result[currentIndex - 1].getIndicatorValue().subtract(new BigDecimal(1)))
        );
    }

    private OBVResult buildFallingPriceResult(int currentIndex) {
        return new OBVResult(
                originalData[currentIndex].getTickTime(),
                MathHelper.scaleAndRound(result[currentIndex - 1].getIndicatorValue().subtract(originalData[currentIndex].getBaseVolume()))
        );
    }

    private OBVResult buildRisingPriceResult(int currentIndex) {
        return new OBVResult(
                originalData[currentIndex].getTickTime(),
                MathHelper.scaleAndRound(result[currentIndex - 1].getIndicatorValue().add(originalData[currentIndex].getBaseVolume()))
        );
    }

    private int compareCurrentCloseWithPrevious(int currentIndex) {
        return originalData[currentIndex].getClose().compareTo(originalData[currentIndex - 1].getClose());
    }

}
