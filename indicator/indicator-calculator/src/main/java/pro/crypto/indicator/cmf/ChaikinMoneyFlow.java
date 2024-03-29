package pro.crypto.indicator.cmf;

import pro.crypto.helper.MathHelper;
import pro.crypto.helper.MoneyFlowVolumesCalculator;
import pro.crypto.helper.PriceVolumeExtractor;
import pro.crypto.model.Indicator;
import pro.crypto.model.indicator.IndicatorType;
import pro.crypto.model.tick.Tick;
import pro.crypto.request.IndicatorRequest;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.stream.IntStream;

import static java.util.Objects.isNull;
import static pro.crypto.model.indicator.IndicatorType.CHAIKIN_MONEY_FLOW;

public class ChaikinMoneyFlow implements Indicator<CMFResult> {

    private final Tick[] originalData;
    private final int period;

    private CMFResult[] result;

    public ChaikinMoneyFlow(IndicatorRequest creationRequest) {
        CMFRequest request = (CMFRequest) creationRequest;
        this.originalData = request.getOriginalData();
        this.period = request.getPeriod();
        checkIncomingData();
    }

    @Override
    public IndicatorType getType() {
        return CHAIKIN_MONEY_FLOW;
    }

    @Override
    public void calculate() {
        result = new CMFResult[originalData.length];
        BigDecimal[] moneyFlowVolumes = MoneyFlowVolumesCalculator.calculate(originalData);
        BigDecimal[] moneyFlowSum = calculateMoneyFlowSum(moneyFlowVolumes);
        BigDecimal[] volumesSum = calculateVolumesSum();
        calculateChaikinMoneyFlowValues(moneyFlowSum, volumesSum);
    }

    @Override
    public CMFResult[] getResult() {
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

    private BigDecimal[] calculateMoneyFlowSum(BigDecimal[] moneyFlowVolumes) {
        return IntStream.range(0, originalData.length)
                .mapToObj(idx -> calculateSum(moneyFlowVolumes, idx))
                .toArray(BigDecimal[]::new);
    }

    private BigDecimal[] calculateVolumesSum() {
        BigDecimal[] baseVolumes = PriceVolumeExtractor.extractBaseVolumes(originalData);
        return IntStream.range(0, originalData.length)
                .mapToObj(idx -> calculateSum(baseVolumes, idx))
                .toArray(BigDecimal[]::new);
    }

    private BigDecimal calculateSum(BigDecimal[] values, int currentIndex) {
        return currentIndex >= period - 1
                ? calculateSumValue(values, currentIndex)
                : null;
    }

    private BigDecimal calculateSumValue(BigDecimal[] values, int currentIndex) {
        return MathHelper.sum(Arrays.copyOfRange(values, currentIndex - period + 1, currentIndex + 1));
    }

    private void calculateChaikinMoneyFlowValues(BigDecimal[] moneyFlowSum, BigDecimal[] volumesSum) {
        IntStream.range(0, result.length)
                .forEach(idx -> result[idx] = new CMFResult(
                        originalData[idx].getTickTime(),
                        MathHelper.divide(moneyFlowSum[idx], volumesSum[idx])
                ));
    }

}
