package pro.crypto.indicator.asi;

import pro.crypto.exception.WrongIncomingParametersException;
import pro.crypto.helper.FakeTicksCreator;
import pro.crypto.helper.IndicatorResultExtractor;
import pro.crypto.helper.MathHelper;
import pro.crypto.indicator.ma.MARequest;
import pro.crypto.indicator.ma.MAResult;
import pro.crypto.indicator.ma.MovingAverageFactory;
import pro.crypto.indicator.si.SIRequest;
import pro.crypto.indicator.si.SwingIndex;
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
import static pro.crypto.model.tick.PriceType.CLOSE;

/**
* Indicator is made for visual analyzing and signal line crossing
*/
public class AccumulativeSwingIndex implements Indicator<ASIResult> {

    private final Tick[] originalData;
    private final double limitMoveValue;
    private final int movingAveragePeriod;
    private final IndicatorType movingAverageType;

    private BigDecimal[] accumulativeSwingIndexes;
    private ASIResult[] result;

    public AccumulativeSwingIndex(IndicatorRequest creationRequest) {
        ASIRequest request = (ASIRequest) creationRequest;
        this.originalData = request.getOriginalData();
        this.limitMoveValue = request.getLimitMoveValue();
        this.movingAveragePeriod = request.getMovingAveragePeriod();
        this.movingAverageType = request.getMovingAverageType();
        checkIncomingData();
    }

    @Override
    public IndicatorType getType() {
        return ACCUMULATIVE_SWING_INDEX;
    }

    @Override
    public void calculate() {
        BigDecimal[] swingIndexes = calculateSwingIndexes();
        calculateAccumulativeSwingIndexes(swingIndexes);
        BigDecimal[] signalLineValues = calculateSignalLineValues(accumulativeSwingIndexes);
        calculateAccumulativeSwingIndexResult(signalLineValues);
    }

    @Override
    public ASIResult[] getResult() {
        if (isNull(result)) {
            calculate();
        }
        return result;
    }

    private void checkIncomingData() {
        checkOriginalData(originalData);
        checkLimitMoveValue();
        checkOriginalDataSize(originalData, movingAveragePeriod);
        checkPeriod(movingAveragePeriod);
        checkMovingAverageType(movingAverageType);
    }

    private void checkLimitMoveValue() {
        if (limitMoveValue <= 0) {
            throw new WrongIncomingParametersException(format(ENGLISH, "Limit move value should be more than 0 {indicator: {%s}, shift: {%.2f}}",
                    getType().toString(), limitMoveValue));
        }
    }

    private BigDecimal[] calculateSwingIndexes() {
        return IndicatorResultExtractor.extractIndicatorValues(calculateSwingIndex());
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

    private void calculateAccumulativeSwingIndexes(BigDecimal[] swingIndexes) {
        accumulativeSwingIndexes = new BigDecimal[originalData.length];
        fillInInitialValues(swingIndexes[1]);
        IntStream.range(2, originalData.length)
                .forEach(idx -> accumulativeSwingIndexes[idx] = calculateAccumulativeSwingIndex(swingIndexes[idx], idx));
    }

    private void fillInInitialValues(BigDecimal firstSwingIndex) {
        accumulativeSwingIndexes[0] = null;
        accumulativeSwingIndexes[1] = firstSwingIndex;
    }

    private BigDecimal calculateAccumulativeSwingIndex(BigDecimal swingIndex, int currentIndex) {
        return MathHelper.scaleAndRound(swingIndex.add(accumulativeSwingIndexes[currentIndex - 1]));
    }

    private BigDecimal[] calculateSignalLineValues(BigDecimal[] accumulativeSwingIndexes) {
        BigDecimal[] movingAverageValues = IndicatorResultExtractor.extractIndicatorValues(calculateMovingAverage(accumulativeSwingIndexes));
        BigDecimal[] result = new BigDecimal[originalData.length];
        System.arraycopy(movingAverageValues, 0, result, 1, movingAverageValues.length);
        return result;
    }

    private MAResult[] calculateMovingAverage(BigDecimal[] accumulativeSwingIndexes) {
        return MovingAverageFactory.create(buildMARequest(accumulativeSwingIndexes)).getResult();
    }

    private IndicatorRequest buildMARequest(BigDecimal[] accumulativeSwingIndexes) {
        return MARequest.builder()
                .originalData(FakeTicksCreator.createWithCloseOnly(accumulativeSwingIndexes))
                .priceType(CLOSE)
                .period(movingAveragePeriod)
                .indicatorType(movingAverageType)
                .build();
    }

    private void calculateAccumulativeSwingIndexResult(BigDecimal[] signalLineValues) {
        result = IntStream.range(0, originalData.length)
                .mapToObj(idx -> new ASIResult(originalData[idx].getTickTime(), accumulativeSwingIndexes[idx], signalLineValues[idx]))
                .toArray(ASIResult[]::new);
    }

}
