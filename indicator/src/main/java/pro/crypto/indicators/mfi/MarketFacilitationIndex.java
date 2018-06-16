package pro.crypto.indicators.mfi;

import pro.crypto.helper.MathHelper;
import pro.crypto.model.Indicator;
import pro.crypto.model.IndicatorType;
import pro.crypto.model.request.MFIRequest;
import pro.crypto.model.result.MFIResult;
import pro.crypto.model.tick.Tick;

import java.math.BigDecimal;

import static java.util.Objects.isNull;
import static pro.crypto.model.IndicatorType.MARKET_FACILITATION_INDEX;

public class MarketFacilitationIndex implements Indicator<MFIResult> {

    private final Tick[] originalData;

    private MFIResult[] result;

    public MarketFacilitationIndex(MFIRequest request) {
        this.originalData = request.getOriginalData();
        checkOriginalData(originalData);
    }

    @Override
    public IndicatorType getType() {
        return MARKET_FACILITATION_INDEX;
    }

    @Override
    public void calculate() {
        result = new MFIResult[originalData.length];
        calculateMarketFacilitationIndex();
    }

    @Override
    public MFIResult[] getResult() {
        if (isNull(result)) {
            calculate();
        }
        return result;
    }

    private void calculateMarketFacilitationIndex() {
        for (int currentIndex = 0; currentIndex < result.length; currentIndex++) {
            result[currentIndex] = new MFIResult(originalData[currentIndex].getTickTime(), calculateMarketFacilitationIndexValue(originalData[currentIndex]));
        }
    }

    private BigDecimal calculateMarketFacilitationIndexValue(Tick tick) {
        return MathHelper.divide(tick.getHigh().subtract(tick.getLow()), tick.getBaseVolume());
    }

}
