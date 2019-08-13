package pro.crypto.indicator.adl;

import pro.crypto.helper.MathHelper;
import pro.crypto.helper.MoneyFlowVolumesCalculator;
import pro.crypto.model.Indicator;
import pro.crypto.model.indicator.IndicatorType;
import pro.crypto.model.tick.Tick;
import pro.crypto.request.IndicatorRequest;

import java.math.BigDecimal;
import java.util.stream.IntStream;

import static java.util.Objects.isNull;
import static pro.crypto.model.indicator.IndicatorType.ACCUMULATION_DISTRIBUTION_LINE;

public class AccumulationDistributionLine implements Indicator<ADLResult> {

    private final Tick[] originalData;

    private ADLResult[] result;

    public AccumulationDistributionLine(IndicatorRequest creationRequest) {
        ADLRequest request = (ADLRequest) creationRequest;
        this.originalData = request.getOriginalData();
        checkOriginalData(originalData);
    }

    @Override
    public IndicatorType getType() {
        return ACCUMULATION_DISTRIBUTION_LINE;
    }

    @Override
    public void calculate() {
        result = new ADLResult[originalData.length];
        BigDecimal[] moneyFlowVolumes = MoneyFlowVolumesCalculator.calculate(originalData);
        calculateAccumulationDistributionLine(moneyFlowVolumes);
    }

    @Override
    public ADLResult[] getResult() {
        if (isNull(result)) {
            calculate();
        }
        return result;
    }

    private void calculateAccumulationDistributionLine(BigDecimal[] moneyFlowVolumes) {
        IntStream.range(0, result.length)
                .forEach(idx -> result[idx] = calculateAccumulationDistributionValue(moneyFlowVolumes, idx));
    }

    private ADLResult calculateAccumulationDistributionValue(BigDecimal[] moneyFlowVolumes, int currentIndex) {
        return currentIndex == 0
                ? new ADLResult(originalData[0].getTickTime(), MathHelper.scaleAndRound(moneyFlowVolumes[0]))
                : calculateAccumulationDistribution(moneyFlowVolumes, currentIndex);
    }

    private ADLResult calculateAccumulationDistribution(BigDecimal[] moneyFlowVolumes, int currentIndex) {
        return new ADLResult(
                originalData[currentIndex].getTickTime(),
                MathHelper.sum(moneyFlowVolumes[currentIndex], result[currentIndex - 1].getIndicatorValue())
        );
    }

}
