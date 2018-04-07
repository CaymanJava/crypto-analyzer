package pro.crypto.indicators.cmf;

import pro.crypto.helper.MathHelper;
import pro.crypto.helper.MoneyFlowVolumesCalculator;
import pro.crypto.model.Indicator;
import pro.crypto.model.IndicatorType;
import pro.crypto.model.request.CMFRequest;
import pro.crypto.model.result.CMFResult;
import pro.crypto.model.tick.Tick;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.stream.Stream;

import static java.util.Objects.isNull;
import static pro.crypto.model.IndicatorType.CHAIKIN_MONEY_FLOW;

public class ChaikinMoneyFlow implements Indicator<CMFResult> {

    private final Tick[] originalData;
    private final int period;

    private CMFResult[] result;

    public ChaikinMoneyFlow(CMFRequest request) {
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
        BigDecimal[] moneyFlowSum = new BigDecimal[originalData.length];
        for (int currentIndex = period - 1; currentIndex < moneyFlowSum.length; currentIndex++) {
            moneyFlowSum[currentIndex] = calculateSum(moneyFlowVolumes, currentIndex);
        }
        return moneyFlowSum;
    }

    private BigDecimal[] calculateVolumesSum() {
        BigDecimal[] volumesSum = new BigDecimal[originalData.length];
        BigDecimal[] baseVolumes = extractBaseVolumes();
        for (int currentIndex = period - 1; currentIndex < volumesSum.length; currentIndex++) {
            volumesSum[currentIndex] = calculateSum(baseVolumes, currentIndex);
        }
        return volumesSum;
    }

    private BigDecimal[] extractBaseVolumes() {
        return Stream.of(originalData)
                .map(Tick::getBaseVolume)
                .toArray(BigDecimal[]::new);
    }

    private BigDecimal calculateSum(BigDecimal[] values, int currentIndex) {
        return MathHelper.sum(Arrays.copyOfRange(values, currentIndex - period + 1, currentIndex + 1));
    }

    private void calculateChaikinMoneyFlowValues(BigDecimal[] moneyFlowSum, BigDecimal[] volumesSum) {
        for (int currentIndex = 0; currentIndex < result.length; currentIndex++) {
            result[currentIndex] = new CMFResult(
                    originalData[currentIndex].getTickTime(),
                    MathHelper.divide(moneyFlowSum[currentIndex], volumesSum[currentIndex])
            );
        }
    }

}
