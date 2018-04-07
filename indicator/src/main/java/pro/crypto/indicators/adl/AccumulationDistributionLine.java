package pro.crypto.indicators.adl;

import pro.crypto.helper.MathHelper;
import pro.crypto.helper.MoneyFlowVolumesCalculator;
import pro.crypto.model.Indicator;
import pro.crypto.model.IndicatorType;
import pro.crypto.model.request.ADLRequest;
import pro.crypto.model.result.ADLResult;
import pro.crypto.model.tick.Tick;

import java.math.BigDecimal;

import static java.util.Objects.isNull;
import static pro.crypto.model.IndicatorType.ACCUMULATION_DISTRIBUTION_LINE;

public class AccumulationDistributionLine implements Indicator<ADLResult> {

    private final Tick[] originalData;

    private ADLResult[] result;

    public AccumulationDistributionLine(ADLRequest request) {
        this.originalData = request.getOriginalData();
        checkOriginalData(originalData);
    }

    @Override
    public void calculate() {
        result = new ADLResult[originalData.length];
        BigDecimal[] moneyFlowVolumes = MoneyFlowVolumesCalculator.calculate(originalData);
        calculateAccumulationDistributionLine(moneyFlowVolumes);
    }

    @Override
    public IndicatorType getType() {
        return ACCUMULATION_DISTRIBUTION_LINE;
    }

    @Override
    public ADLResult[] getResult() {
        if (isNull(result)) {
            calculate();
        }
        return result;
    }

    private void calculateAccumulationDistributionLine(BigDecimal[] moneyFlowVolumes) {
        fillInStartIndicatorPosition(moneyFlowVolumes);
        fillInRemainPositions(moneyFlowVolumes);
    }

    private void fillInRemainPositions(BigDecimal[] moneyFlowVolumes) {
        for (int i = 1; i < originalData.length; i++) {
            result[i] = calculateAccumulationDistributionValue(moneyFlowVolumes, i);
        }
    }

    private void fillInStartIndicatorPosition(BigDecimal[] moneyFlowVolumes) {
        result[0] = new ADLResult(originalData[0].getTickTime(), MathHelper.scaleAndRound(moneyFlowVolumes[0]));
    }

    private ADLResult calculateAccumulationDistributionValue(BigDecimal[] moneyFlowVolumes, int currentIndex) {
        return new ADLResult(
                originalData[currentIndex].getTickTime(),
                MathHelper.sum(moneyFlowVolumes[currentIndex], result[currentIndex - 1].getIndicatorValue())
        );
    }

}
