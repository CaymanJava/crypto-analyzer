package pro.crypto.indicators.adl;

import pro.crypto.helper.MathHelper;
import pro.crypto.helper.MoneyFlowVolumesCounter;
import pro.crypto.model.Indicator;
import pro.crypto.model.IndicatorType;
import pro.crypto.model.request.ADLCreationRequest;
import pro.crypto.model.result.ADLResult;
import pro.crypto.model.tick.Tick;

import java.math.BigDecimal;

import static java.util.Objects.isNull;
import static pro.crypto.model.IndicatorType.ACCUMULATION_DISTRIBUTION_LINE;

public class AccumulationDistributionLine implements Indicator<ADLResult> {

    private final Tick[] originalData;

    private ADLResult[] result;

    public AccumulationDistributionLine(ADLCreationRequest request) {
        this.originalData = request.getOriginalData();
        checkOriginalData(originalData);
    }

    @Override
    public void calculate() {
        result = new ADLResult[originalData.length];
        BigDecimal[] moneyFlowVolumes = MoneyFlowVolumesCounter.countMoneyFlowVolumes(originalData);
        countAccumulationDistributionLine(moneyFlowVolumes);
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

    private void countAccumulationDistributionLine(BigDecimal[] moneyFlowVolumes) {
        fillInStartIndicatorPosition(moneyFlowVolumes);
        fillInRemainPositions(moneyFlowVolumes);
    }

    private void fillInRemainPositions(BigDecimal[] moneyFlowVolumes) {
        for (int i = 1; i < originalData.length; i++) {
            result[i] = countAccumulationDistributionValue(moneyFlowVolumes, i);
        }
    }

    private void fillInStartIndicatorPosition(BigDecimal[] moneyFlowVolumes) {
        result[0] = new ADLResult(originalData[0].getTickTime(), MathHelper.scaleAndRound(moneyFlowVolumes[0]));
    }

    private ADLResult countAccumulationDistributionValue(BigDecimal[] moneyFlowVolumes, int currentIndex) {
        return new ADLResult(
                originalData[currentIndex].getTickTime(),
                MathHelper.scaleAndRound(moneyFlowVolumes[currentIndex].add(result[currentIndex - 1].getIndicatorValue()))
        );
    }

}
