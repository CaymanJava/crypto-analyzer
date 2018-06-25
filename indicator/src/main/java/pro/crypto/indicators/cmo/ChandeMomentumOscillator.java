package pro.crypto.indicators.cmo;

import pro.crypto.helper.model.BigDecimalTuple;
import pro.crypto.helper.MathHelper;
import pro.crypto.helper.PriceDifferencesCalculator;
import pro.crypto.model.Indicator;
import pro.crypto.model.IndicatorType;
import pro.crypto.model.request.CMORequest;
import pro.crypto.model.result.CMOResult;
import pro.crypto.model.tick.Tick;

import java.math.BigDecimal;
import java.util.stream.IntStream;

import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;
import static pro.crypto.model.IndicatorType.CHANDE_MOMENTUM_OSCILLATOR;

public class ChandeMomentumOscillator implements Indicator<CMOResult> {

    private final Tick[] originalData;
    private final int period;

    private CMOResult[] result;

    public ChandeMomentumOscillator(CMORequest request) {
        this.originalData = request.getOriginalData();
        this.period = request.getPeriod();
        checkIncomingData();
    }

    @Override
    public IndicatorType getType() {
        return CHANDE_MOMENTUM_OSCILLATOR;
    }

    @Override
    public void calculate() {
        result = new CMOResult[originalData.length];
        BigDecimalTuple[] priceDifferences = PriceDifferencesCalculator.calculateCloseDifference(originalData);
        BigDecimalTuple[] priceDifferenceSumValues = PriceDifferencesCalculator.calculatePriceDifferencesSum(priceDifferences, period);
        calculateChandeMomentumOscillatorResult(priceDifferenceSumValues);
    }

    @Override
    public CMOResult[] getResult() {
        if (isNull(result)) {
            calculate();
        }
        return result;
    }

    private void checkIncomingData() {
        checkOriginalData(originalData);
        checkOriginalDataSize(originalData, period);
        checkPeriod(period);
    }

    private void calculateChandeMomentumOscillatorResult(BigDecimalTuple[] priceDifferenceSumValues) {
        IntStream.range(0, result.length)
                .forEach(idx -> result[idx] = new CMOResult(originalData[idx].getTickTime(),
                        calculateChandeMomentumOscillator(priceDifferenceSumValues[idx])));
    }

    private BigDecimal calculateChandeMomentumOscillator(BigDecimalTuple priceDifferenceSum) {
        return nonNull(priceDifferenceSum)
                ? calculateChandeMomentumOscillatorValues(priceDifferenceSum)
                : null;
    }

    // 100 * (∑ positive movement - ∑ negative movement) / (∑ positive movement + ∑ negative movement)
    private BigDecimal calculateChandeMomentumOscillatorValues(BigDecimalTuple priceDifferenceSum) {
        return MathHelper.divide(new BigDecimal(100)
                        .multiply(priceDifferenceSum.getLeft().subtract(priceDifferenceSum.getRight())),
                priceDifferenceSum.getLeft().add(priceDifferenceSum.getRight())
        );
    }

}
