package pro.crypto.indicator.bbw;

import pro.crypto.exception.WrongIncomingParametersException;
import pro.crypto.helper.MathHelper;
import pro.crypto.indicator.bb.BollingerBands;
import pro.crypto.model.Indicator;
import pro.crypto.model.IndicatorRequest;
import pro.crypto.model.IndicatorType;
import pro.crypto.indicator.bb.BBRequest;
import pro.crypto.indicator.bb.BBResult;
import pro.crypto.model.tick.PriceType;
import pro.crypto.model.tick.Tick;

import java.math.BigDecimal;
import java.util.stream.IntStream;

import static java.lang.String.format;
import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;
import static pro.crypto.model.IndicatorType.BOLLINGER_BANDS_WIDTH;
import static pro.crypto.model.IndicatorType.SIMPLE_MOVING_AVERAGE;

public class BollingerBandsWidth implements Indicator<BBWResult> {

    private final Tick[] originalData;
    private final int period;
    private final PriceType priceType;
    private final int standardDeviationCoefficient;
    private final IndicatorType movingAverageType;

    private BBWResult[] result;

    public BollingerBandsWidth(IndicatorRequest creationRequest) {
        BBWRequest request = (BBWRequest) creationRequest;
        this.originalData = request.getOriginalData();
        this.period = request.getPeriod();
        this.priceType = request.getPriceType();
        this.standardDeviationCoefficient = request.getStandardDeviationCoefficient();
        this.movingAverageType = isNull(request.getMovingAverageType()) ? SIMPLE_MOVING_AVERAGE : request.getMovingAverageType();
        checkIncomingData();
    }

    @Override
    public IndicatorType getType() {
        return BOLLINGER_BANDS_WIDTH;
    }

    @Override
    public void calculate() {
        result = new BBWResult[originalData.length];
        BBResult[] bollingerBands = calculateBollingerBands();
        calculateBollingerBandsWidth(bollingerBands);
    }

    @Override
    public BBWResult[] getResult() {
        if (isNull(result)) {
            calculate();
        }
        return result;
    }

    private void checkIncomingData() {
        checkOriginalData(originalData);
        checkOriginalDataSize(originalData, period);
        checkPriceType(priceType);
        checkPeriod(period);
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

    private BBResult[] calculateBollingerBands() {
        return new BollingerBands(buildRequest()).getResult();
    }

    private IndicatorRequest buildRequest() {
        return BBRequest.builder()
                .originalData(originalData)
                .period(period)
                .priceType(priceType)
                .standardDeviationCoefficient(standardDeviationCoefficient)
                .movingAverageType(movingAverageType)
                .build();
    }

    private void calculateBollingerBandsWidth(BBResult[] bollingerBands) {
        IntStream.range(0, result.length)
                .forEach(idx -> result[idx] = calculateBollingerBandsWidth(bollingerBands[idx], idx));
    }

    private BBWResult calculateBollingerBandsWidth(BBResult bollingerBandsValue, int currentIndex) {
        return nonNull(bollingerBandsValue.getUpperBand()) && nonNull(bollingerBandsValue.getMiddleBand()) && nonNull(bollingerBandsValue.getLowerBand())
                ? buildBollingerBandsWidthResult(bollingerBandsValue, currentIndex)
                : new BBWResult(originalData[currentIndex].getTickTime(), null);
    }

    private BBWResult buildBollingerBandsWidthResult(BBResult bollingerBandsValue, int currentIndex) {
        return new BBWResult(originalData[currentIndex].getTickTime(), calculateBollingerBandsWidthValue(bollingerBandsValue));
    }

    private BigDecimal calculateBollingerBandsWidthValue(BBResult bollingerBandsValue) {
        return MathHelper.divide(bollingerBandsValue.getUpperBand().subtract(bollingerBandsValue.getLowerBand()), bollingerBandsValue.getMiddleBand());
    }

}
