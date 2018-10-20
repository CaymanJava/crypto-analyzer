package pro.crypto.indicator.atrb;

import pro.crypto.helper.IndicatorResultExtractor;
import pro.crypto.helper.MathHelper;
import pro.crypto.helper.PriceVolumeExtractor;
import pro.crypto.indicator.atr.ATRRequest;
import pro.crypto.indicator.atr.AverageTrueRange;
import pro.crypto.model.Indicator;
import pro.crypto.model.IndicatorRequest;
import pro.crypto.model.IndicatorType;
import pro.crypto.model.SimpleIndicatorResult;
import pro.crypto.model.tick.PriceType;
import pro.crypto.model.tick.Tick;

import java.math.BigDecimal;
import java.util.function.BiFunction;
import java.util.stream.IntStream;

import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;
import static pro.crypto.model.IndicatorType.AVERAGE_TRUE_RANGE_BANDS;

public class AverageTrueRangeBands implements Indicator<ATRBResult> {

    private final Tick[] originalData;
    private final int period;
    private final double shift;
    private final PriceType priceType;

    private ATRBResult[] result;

    public AverageTrueRangeBands(IndicatorRequest creationRequest) {
        ATRBRequest request = (ATRBRequest) creationRequest;
        this.originalData = request.getOriginalData();
        this.period = request.getPeriod();
        this.shift = request.getShift();
        this.priceType = request.getPriceType();
        checkIncomingData();
    }

    @Override
    public IndicatorType getType() {
        return AVERAGE_TRUE_RANGE_BANDS;
    }

    @Override
    public void calculate() {
        result = new ATRBResult[originalData.length];
        BigDecimal[] atrValues = calculateAverageTrueRangeValues();
        BigDecimal[] middleBandValues = PriceVolumeExtractor.extract(originalData, priceType);
        buildAverageTrueRangeBandsResult(atrValues, middleBandValues);
    }

    @Override
    public ATRBResult[] getResult() {
        if (isNull(result)) {
            calculate();
        }
        return result;
    }

    private void checkIncomingData() {
        checkOriginalData(originalData);
        checkOriginalDataSize(originalData, period);
        checkPeriod(period);
        checkPriceType(priceType);
        checkShift(shift);
    }

    private BigDecimal[] calculateAverageTrueRangeValues() {
        return IndicatorResultExtractor.extractIndicatorValues(calculateAverageTrueRange());
    }

    private SimpleIndicatorResult[] calculateAverageTrueRange() {
        return new AverageTrueRange(buildATRRequest()).getResult();
    }

    private IndicatorRequest buildATRRequest() {
        return ATRRequest.builder()
                .originalData(originalData)
                .period(period)
                .build();
    }

    private void buildAverageTrueRangeBandsResult(BigDecimal[] atrValues, BigDecimal[] middleBandValues) {
        IntStream.range(0, result.length)
                .forEach(idx -> result[idx] = buildATRBResult(atrValues[idx], middleBandValues[idx], idx));
    }

    private ATRBResult buildATRBResult(BigDecimal atrValue, BigDecimal middleBandValue, int currentIndex) {
        return nonNull(atrValue)
                ? calculateAndBuildATRBResult(atrValue, middleBandValue, currentIndex)
                : buildEmptyATRBResult(currentIndex);
    }

    private ATRBResult calculateAndBuildATRBResult(BigDecimal atrValue, BigDecimal middleBandValue, int currentIndex) {
        return new ATRBResult(
                originalData[currentIndex].getTickTime(),
                calculateBandValue(atrValue, middleBandValue, BigDecimal::add),
                middleBandValue,
                calculateBandValue(atrValue, middleBandValue, BigDecimal::subtract)
        );
    }

    private BigDecimal calculateBandValue(BigDecimal atrValue, BigDecimal middleBandValue, BiFunction<BigDecimal, BigDecimal, BigDecimal> bandFunction) {
        return MathHelper.scaleAndRound(bandFunction.apply(middleBandValue, atrValue.multiply(new BigDecimal(shift))));
    }

    private ATRBResult buildEmptyATRBResult(int currentIndex) {
        return new ATRBResult(originalData[currentIndex].getTickTime(), null, null, null);
    }

}
