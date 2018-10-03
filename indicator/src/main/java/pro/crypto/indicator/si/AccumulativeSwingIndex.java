package pro.crypto.indicator.si;

import pro.crypto.exception.WrongIncomingParametersException;
import pro.crypto.helper.IndicatorResultExtractor;
import pro.crypto.helper.MathHelper;
import pro.crypto.model.Indicator;
import pro.crypto.model.IndicatorRequest;
import pro.crypto.model.IndicatorType;
import pro.crypto.model.SimpleIndicatorResult;
import pro.crypto.model.tick.Tick;

import java.math.BigDecimal;
import java.util.stream.IntStream;

import static java.lang.String.format;
import static java.util.Locale.ENGLISH;
import static java.util.Objects.isNull;
import static pro.crypto.model.IndicatorType.ACCUMULATIVE_SWING_INDEX;

public class AccumulativeSwingIndex implements Indicator<SIResult> {

    private final Tick[] originalData;
    private final double limitMoveValue;

    private SIResult[] result;

    public AccumulativeSwingIndex(IndicatorRequest creationRequest) {
        SIRequest request = (SIRequest) creationRequest;
        this.originalData = request.getOriginalData();
        this.limitMoveValue = request.getLimitMoveValue();
        checkIncomingData();
    }

    @Override
    public IndicatorType getType() {
        return ACCUMULATIVE_SWING_INDEX;
    }

    @Override
    public void calculate() {
        result = new SIResult[originalData.length];
        BigDecimal[] swingIndexes = calculateSwingIndexes();
        calculateAccumulativeSwingIndexResult(swingIndexes);
    }

    @Override
    public SIResult[] getResult() {
        if (isNull(result)) {
            calculate();
        }
        return result;
    }

    private void checkIncomingData() {
        checkOriginalData(originalData);
        checkLimitMoveValue();
    }

    private void checkLimitMoveValue() {
        if (limitMoveValue <= 0) {
            throw new WrongIncomingParametersException(format(ENGLISH, "Limit move value should be more than 0 {indicator: {%s}, shift: {%.2f}}",
                    getType().toString(), limitMoveValue));
        }
    }

    private BigDecimal[] calculateSwingIndexes() {
        return IndicatorResultExtractor.extractIndicatorValue(calculateSwingIndex());
    }

    private SimpleIndicatorResult[] calculateSwingIndex() {
        return new SwingIndex(buildSIRequest()).getResult();
    }

    private IndicatorRequest buildSIRequest() {
        return SIRequest.builder()
                .originalData(originalData)
                .limitMoveValue(limitMoveValue)
                .build();
    }

    private void calculateAccumulativeSwingIndexResult(BigDecimal[] swingIndexes) {
        fillInInitialValues(swingIndexes[1]);
        IntStream.range(2, result.length)
                .forEach(idx -> result[idx] = new SIResult(
                        originalData[idx].getTickTime(),
                        calculateAccumulativeSwingIndex(swingIndexes[idx], idx))
                );
    }

    private void fillInInitialValues(BigDecimal firstSwingIndex) {
        result[0] = new SIResult(originalData[0].getTickTime(), null);
        result[1] = new SIResult(originalData[1].getTickTime(), firstSwingIndex);
    }

    private BigDecimal calculateAccumulativeSwingIndex(BigDecimal swingIndex, int currentIndex) {
        return MathHelper.scaleAndRound(swingIndex.add(result[currentIndex - 1].getIndicatorValue()));
    }

}
