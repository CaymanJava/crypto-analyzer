package pro.crypto.indicators.bb;

import pro.crypto.exception.WrongIncomingParametersException;
import pro.crypto.helper.BigDecimalTuple;
import pro.crypto.indicators.ma.MovingAverageFactory;
import pro.crypto.indicators.stdev.StandardDeviation;
import pro.crypto.model.Indicator;
import pro.crypto.model.IndicatorType;
import pro.crypto.model.request.BBRequest;
import pro.crypto.model.request.MARequest;
import pro.crypto.model.request.StDevRequest;
import pro.crypto.model.result.BBResult;
import pro.crypto.model.result.MAResult;
import pro.crypto.model.result.StDevResult;
import pro.crypto.model.tick.PriceType;
import pro.crypto.model.tick.Tick;

import java.math.BigDecimal;
import java.util.stream.Stream;

import static java.lang.String.format;
import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;
import static pro.crypto.model.IndicatorType.BOLLINGER_BANDS;
import static pro.crypto.model.IndicatorType.SIMPLE_MOVING_AVERAGE;

public class BollingerBands implements Indicator<BBResult> {

    private final Tick[] originalData;
    private final int period;
    private final PriceType priceType;
    private final int standardDeviationCoefficient;
    private final IndicatorType movingAverageType;

    private BBResult[] result;

    public BollingerBands(BBRequest request) {
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
            throw new WrongIncomingParametersException(format("Standard Deviation Coefficient should be more than 1 " +
                            "{indicator: {%s}, standardDeviationCoefficient: {%d}}",
                    getType().toString(), standardDeviationCoefficient));
        }
    }

    private BigDecimal[] calculateMiddleBand() {
        return extractMovingAverageResults(MovingAverageFactory.create(buildSMARequest()).getResult());
    }

    private MARequest buildSMARequest() {
        return MARequest.builder()
                .originalData(originalData)
                .period(period)
                .priceType(priceType)
                .indicatorType(SIMPLE_MOVING_AVERAGE)
                .build();
    }

    private BigDecimal[] extractMovingAverageResults(MAResult[] movingAverageResult) {
        return Stream.of(movingAverageResult)
                .map(MAResult::getIndicatorValue)
                .toArray(BigDecimal[]::new);
    }

    private BigDecimalTuple[] calculateLowerAndUpperBandValues(BigDecimal[] middleBand) {
        BigDecimal[] standardDeviationValues = calculateStandardDeviation();
        return calculateLowerAndUpperBands(middleBand, standardDeviationValues);
    }

    private BigDecimal[] calculateStandardDeviation() {
        return extractStandardDeviationResult(new StandardDeviation(buildStDevRequest()).getResult());
    }

    private StDevRequest buildStDevRequest() {
        return StDevRequest.builder()
                .originalData(originalData)
                .priceType(priceType)
                .period(period)
                .movingAverageType(movingAverageType)
                .build();
    }

    private BigDecimal[] extractStandardDeviationResult(StDevResult[] result) {
        return Stream.of(result)
                .map(StDevResult::getIndicatorValue)
                .toArray(BigDecimal[]::new);
    }

    private BigDecimalTuple[] calculateLowerAndUpperBands(BigDecimal[] middleBand, BigDecimal[] standardDeviationValues) {
        BigDecimalTuple[] lowerAndUpperValue = new BigDecimalTuple[originalData.length];
        for (int currentIndex = 0; currentIndex < lowerAndUpperValue.length; currentIndex++) {
            lowerAndUpperValue[currentIndex] = nonNull(standardDeviationValues[currentIndex]) && nonNull(middleBand[currentIndex])
                    ? calculateLowerAndUpperBandValues(standardDeviationValues[currentIndex], middleBand[currentIndex])
                    : new BigDecimalTuple(null, null);
        }
        return lowerAndUpperValue;
    }

    private BigDecimalTuple calculateLowerAndUpperBandValues(BigDecimal standardDeviationValue, BigDecimal middleBandValue) {
        return new BigDecimalTuple(
                calculateLowerBandValue(standardDeviationValue, middleBandValue),
                calculateUpperBandValue(standardDeviationValue, middleBandValue)
        );
    }

    private BigDecimal calculateUpperBandValue(BigDecimal standardDeviationValue, BigDecimal middleBandValue) {
        return middleBandValue.add(standardDeviationValue.multiply(new BigDecimal(standardDeviationCoefficient)));
    }

    private BigDecimal calculateLowerBandValue(BigDecimal standardDeviationValue, BigDecimal middleBandValue) {
        return middleBandValue.subtract(standardDeviationValue.multiply(new BigDecimal(standardDeviationCoefficient)));
    }

    private void buildBollingerBandsResult(BigDecimal[] middleBand, BigDecimalTuple[] lowerAndUpperBands) {
        for (int currentIndex = 0; currentIndex < result.length; currentIndex++) {
            result[currentIndex] = new BBResult(
                    originalData[currentIndex].getTickTime(),
                    lowerAndUpperBands[currentIndex].getRight(),
                    middleBand[currentIndex],
                    lowerAndUpperBands[currentIndex].getLeft()
            );
        }
    }

}
