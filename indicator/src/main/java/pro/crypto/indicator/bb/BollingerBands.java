package pro.crypto.indicator.bb;

import pro.crypto.exception.WrongIncomingParametersException;
import pro.crypto.helper.IndicatorResultExtractor;
import pro.crypto.indicator.ma.MARequest;
import pro.crypto.indicator.ma.MovingAverageFactory;
import pro.crypto.indicator.stdev.StDevRequest;
import pro.crypto.indicator.stdev.StandardDeviation;
import pro.crypto.model.BigDecimalTuple;
import pro.crypto.model.Indicator;
import pro.crypto.model.IndicatorRequest;
import pro.crypto.model.IndicatorType;
import pro.crypto.model.tick.PriceType;
import pro.crypto.model.tick.Tick;

import java.math.BigDecimal;
import java.util.function.BiFunction;
import java.util.stream.IntStream;

import static java.lang.String.format;
import static java.util.Locale.ENGLISH;
import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;
import static pro.crypto.model.IndicatorType.BOLLINGER_BANDS;
import static pro.crypto.model.IndicatorType.SIMPLE_MOVING_AVERAGE;

public class BollingerBands implements Indicator<BBResult> {

    private final Tick[] originalData;
    private final int period;
    private final PriceType priceType;
    private final double standardDeviationCoefficient;
    private final IndicatorType movingAverageType;

    private BBResult[] result;

    public BollingerBands(IndicatorRequest creationRequest) {
        BBRequest request = (BBRequest) creationRequest;
        this.originalData = request.getOriginalData();
        this.period = request.getPeriod();
        this.priceType = request.getPriceType();
        this.standardDeviationCoefficient = request.getStandardDeviationCoefficient();
        this.movingAverageType = isNull(request.getMovingAverageType()) ? SIMPLE_MOVING_AVERAGE : request.getMovingAverageType();
        checkIncomingData();
    }

    @Override
    public IndicatorType getType() {
        return BOLLINGER_BANDS;
    }

    @Override
    public void calculate() {
        result = new BBResult[originalData.length];
        BigDecimal[] middleBand = calculateMiddleBand();
        BigDecimalTuple[] lowerAndUpperBands = calculateLowerAndUpperBandValues(middleBand);
        buildBollingerBandsResult(middleBand, lowerAndUpperBands);
    }

    @Override
    public BBResult[] getResult() {
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
        checkMovingAverageType(movingAverageType);
        checkStDevCoefficient();
    }

    private void checkStDevCoefficient() {
        if (standardDeviationCoefficient <= 1) {
            throw new WrongIncomingParametersException(format(ENGLISH, "Standard Deviation Coefficient should be more than 1 " +
                            "{indicator: {%s}, standardDeviationCoefficient: {%.2f}}",
                    getType().toString(), standardDeviationCoefficient));
        }
    }

    private BigDecimal[] calculateMiddleBand() {
        return IndicatorResultExtractor.extractIndicatorValue(MovingAverageFactory.create(buildSMARequest()).getResult());
    }

    private IndicatorRequest buildSMARequest() {
        return MARequest.builder()
                .originalData(originalData)
                .period(period)
                .priceType(priceType)
                .indicatorType(SIMPLE_MOVING_AVERAGE)
                .build();
    }

    private BigDecimalTuple[] calculateLowerAndUpperBandValues(BigDecimal[] middleBand) {
        BigDecimal[] standardDeviationValues = calculateStandardDeviation();
        return calculateLowerAndUpperBands(middleBand, standardDeviationValues);
    }

    private BigDecimal[] calculateStandardDeviation() {
        return IndicatorResultExtractor.extractIndicatorValue(new StandardDeviation(buildStDevRequest()).getResult());
    }

    private IndicatorRequest buildStDevRequest() {
        return StDevRequest.builder()
                .originalData(originalData)
                .priceType(priceType)
                .period(period)
                .movingAverageType(movingAverageType)
                .build();
    }

    private BigDecimalTuple[] calculateLowerAndUpperBands(BigDecimal[] middleBand, BigDecimal[] standardDeviationValues) {
        return IntStream.range(0, originalData.length)
                .mapToObj(idx -> calculateLowerAndUpperBands(middleBand, standardDeviationValues, idx))
                .toArray(BigDecimalTuple[]::new);
    }

    private BigDecimalTuple calculateLowerAndUpperBands(BigDecimal[] middleBand, BigDecimal[] standardDeviationValues, int currentIndex) {
        return nonNull(standardDeviationValues[currentIndex]) && nonNull(middleBand[currentIndex])
                ? calculateLowerAndUpperBandValues(standardDeviationValues[currentIndex], middleBand[currentIndex])
                : new BigDecimalTuple(null, null);
    }

    private BigDecimalTuple calculateLowerAndUpperBandValues(BigDecimal standardDeviationValue, BigDecimal middleBandValue) {
        return new BigDecimalTuple(
                calculateBandValue(standardDeviationValue, middleBandValue, BigDecimal::subtract),
                calculateBandValue(standardDeviationValue, middleBandValue, BigDecimal::add)
        );
    }

    private BigDecimal calculateBandValue(BigDecimal standardDeviationValue, BigDecimal middleBandValue, BiFunction<BigDecimal, BigDecimal, BigDecimal> bandFunction) {
        return bandFunction.apply(middleBandValue, standardDeviationValue.multiply(new BigDecimal(standardDeviationCoefficient)));
    }

    private void buildBollingerBandsResult(BigDecimal[] middleBand, BigDecimalTuple[] lowerAndUpperBands) {
        IntStream.range(0, result.length)
                .forEach(idx -> result[idx] = new BBResult(
                        originalData[idx].getTickTime(),
                        lowerAndUpperBands[idx].getRight(),
                        middleBand[idx],
                        lowerAndUpperBands[idx].getLeft()
                ));
    }

}
