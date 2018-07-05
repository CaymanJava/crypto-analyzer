package pro.crypto.indicator.mfi;

import pro.crypto.helper.MathHelper;
import pro.crypto.model.Indicator;
import pro.crypto.model.IndicatorType;
import pro.crypto.model.tick.Tick;

import java.math.BigDecimal;
import java.util.stream.IntStream;

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
        result = calculateMarketFacilitationIndex();
    }

    @Override
    public MFIResult[] getResult() {
        if (isNull(result)) {
            calculate();
        }
        return result;
    }

    private MFIResult[] calculateMarketFacilitationIndex() {
        return IntStream.range(0, originalData.length)
                .mapToObj(this::buildMarketFacilitationIndexResult)
                .toArray(MFIResult[]::new);
    }

    private MFIResult buildMarketFacilitationIndexResult(int currentIndex) {
        return new MFIResult(originalData[currentIndex].getTickTime(), calculateMarketFacilitationIndexValue(originalData[currentIndex]));
    }

    private BigDecimal calculateMarketFacilitationIndexValue(Tick tick) {
        return MathHelper.divide(tick.getHigh().subtract(tick.getLow()), tick.getBaseVolume());
    }

}
