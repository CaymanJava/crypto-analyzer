package pro.crypto.indicators.imi;

import pro.crypto.model.BigDecimalTuple;
import pro.crypto.helper.MathHelper;
import pro.crypto.helper.PriceDifferencesCalculator;
import pro.crypto.model.Indicator;
import pro.crypto.model.IndicatorType;
import pro.crypto.model.request.IMIRequest;
import pro.crypto.model.result.IMIResult;
import pro.crypto.model.tick.Tick;

import java.math.BigDecimal;
import java.util.stream.IntStream;

import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;
import static pro.crypto.model.IndicatorType.INTRADAY_MOMENTUM_INDEX;

public class IntradayMomentumIndex implements Indicator<IMIResult> {

    private final Tick[] originalData;
    private final int period;

    private IMIResult[] result;

    public IntradayMomentumIndex(IMIRequest request) {
        this.originalData = request.getOriginalData();
        this.period = request.getPeriod();
        checkIncomingData();
    }

    @Override
    public IndicatorType getType() {
        return INTRADAY_MOMENTUM_INDEX;
    }

    @Override
    public void calculate() {
        result = new IMIResult[originalData.length];
        BigDecimalTuple[] priceDifferences = PriceDifferencesCalculator.calculateOpenCloseDifference(originalData);
        BigDecimalTuple[] priceDifferencesSum = PriceDifferencesCalculator.calculatePriceDifferencesSum(priceDifferences, period);
        calculateIntradayMomentumIndex(priceDifferencesSum);
    }

    @Override
    public IMIResult[] getResult() {
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

    private void calculateIntradayMomentumIndex(BigDecimalTuple[] priceDifferences) {
        IntStream.range(0, result.length)
                .forEach(idx -> result[idx] = new IMIResult(originalData[idx].getTickTime(), calculateIntradayMomentumIndex(priceDifferences[idx])));
    }

    private BigDecimal calculateIntradayMomentumIndex(BigDecimalTuple priceDifference) {
        return nonNull(priceDifference)
                ? calculateIntradayMomentumIndexValue(priceDifference)
                : null;
    }

    private BigDecimal calculateIntradayMomentumIndexValue(BigDecimalTuple priceDifference) {
        return MathHelper.divide(new BigDecimal(100).multiply(priceDifference.getLeft()), priceDifference.getLeft().add(priceDifference.getRight()));
    }

}
