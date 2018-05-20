package pro.crypto.indicators.atrb;

import pro.crypto.exception.WrongIncomingParametersException;
import pro.crypto.helper.MathHelper;
import pro.crypto.helper.PriceExtractor;
import pro.crypto.indicators.atr.AverageTrueRange;
import pro.crypto.model.Indicator;
import pro.crypto.model.IndicatorType;
import pro.crypto.model.request.ATRBRequest;
import pro.crypto.model.request.ATRRequest;
import pro.crypto.model.result.ATRBResult;
import pro.crypto.model.result.ATRResult;
import pro.crypto.model.tick.PriceType;
import pro.crypto.model.tick.Tick;

import java.math.BigDecimal;
import java.util.stream.Stream;

import static java.lang.String.format;
import static java.util.Locale.ENGLISH;
import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;
import static pro.crypto.model.IndicatorType.AVERAGE_TRUE_RANGE_BANDS;

public class AverageTrueRangeBands implements Indicator<ATRBResult> {

    private final Tick[] originalData;
    private final int period;
    private final double shift;
    private final PriceType priceType;

    private ATRBResult[] result;

    public AverageTrueRangeBands(ATRBRequest request) {
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
        BigDecimal[] middleBandValues = PriceExtractor.extractValuesByType(originalData, priceType);
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
        checkShift();
    }

    private void checkShift() {
        if (shift < 0) {
            throw new WrongIncomingParametersException(format(ENGLISH, "Shift should be more or equals 0 {indicator: {%s}, shift: {%.2f}}",
                    getType().toString(), shift));
        }
    }

    private BigDecimal[] calculateAverageTrueRangeValues() {
        return extractATRValues(calculateAverageTrueRange());
    }

    private ATRResult[] calculateAverageTrueRange() {
        return new AverageTrueRange(buildATRRequest()).getResult();
    }

    private ATRRequest buildATRRequest() {
        return ATRRequest.builder()
                .originalData(originalData)
                .period(period)
                .build();
    }

    private BigDecimal[] extractATRValues(ATRResult[] result) {
        return Stream.of(result)
                .map(ATRResult::getIndicatorValue)
                .toArray(BigDecimal[]::new);
    }

    private void buildAverageTrueRangeBandsResult(BigDecimal[] atrValues, BigDecimal[] middleBandValues) {
        for (int currentIndex = 0; currentIndex < result.length; currentIndex++) {
            result[currentIndex] = buildATRBResult(atrValues[currentIndex], middleBandValues[currentIndex], currentIndex);
        }
    }

    private ATRBResult buildATRBResult(BigDecimal atrValue, BigDecimal middleBandValue, int currentIndex) {
        return nonNull(atrValue)
                ? calculateAndBuildATRBResult(atrValue, middleBandValue, currentIndex)
                : buildEmptyATRBResult(currentIndex);
    }

    private ATRBResult calculateAndBuildATRBResult(BigDecimal atrValue, BigDecimal middleBandValue, int currentIndex) {
        return new ATRBResult(
                originalData[currentIndex].getTickTime(),
                calculateUpperBand(atrValue, middleBandValue),
                middleBandValue,
                calculateLowerBand(atrValue, middleBandValue)
        );
    }

    private BigDecimal calculateLowerBand(BigDecimal atrValue, BigDecimal middleBandValue) {
        return MathHelper.scaleAndRound(middleBandValue.subtract(atrValue.multiply(new BigDecimal(shift))));
    }

    private BigDecimal calculateUpperBand(BigDecimal atrValue, BigDecimal middleBandValue) {
        return MathHelper.scaleAndRound(middleBandValue.add(atrValue.multiply(new BigDecimal(shift))));
    }

    private ATRBResult buildEmptyATRBResult(int currentIndex) {
        return new ATRBResult(originalData[currentIndex].getTickTime(), null, null, null);
    }

}
