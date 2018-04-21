package pro.crypto.indicators.bbw;

import pro.crypto.exception.WrongIncomingParametersException;
import pro.crypto.helper.MathHelper;
import pro.crypto.indicators.bb.BollingerBands;
import pro.crypto.model.Indicator;
import pro.crypto.model.IndicatorType;
import pro.crypto.model.request.BBRequest;
import pro.crypto.model.request.BBWRequest;
import pro.crypto.model.result.BBResult;
import pro.crypto.model.result.BBWResult;
import pro.crypto.model.tick.PriceType;
import pro.crypto.model.tick.Tick;

import java.math.BigDecimal;

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

    public BollingerBandsWidth(BBWRequest request) {
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
        return new BollingerBands(buildBBRequest()).getResult();
    }

    private BBRequest buildBBRequest() {
        return BBRequest.builder()
                .originalData(originalData)
                .period(period)
                .priceType(priceType)
                .standardDeviationCoefficient(standardDeviationCoefficient)
                .movingAverageType(movingAverageType)
                .build();
    }

    private void calculateBollingerBandsWidth(BBResult[] bollingerBands) {
        for (int currentIndex = 0; currentIndex < result.length; currentIndex++) {
            result[currentIndex] = calculateBollingerBandsWidth(bollingerBands[currentIndex], currentIndex);
        }
    }

    private BBWResult calculateBollingerBandsWidth(BBResult bollingerBandsValue, int currentIndex) {
        return nonNull(bollingerBandsValue.getUpperBand()) && nonNull(bollingerBandsValue.getMiddleBand()) && nonNull(bollingerBandsValue.getLowerBand())
                ? calculateBollingerBandsWidthResult(bollingerBandsValue, currentIndex)
                : new BBWResult(originalData[currentIndex].getTickTime(), null);
    }

    private BBWResult calculateBollingerBandsWidthResult(BBResult bollingerBandsValue, int currentIndex) {
        return new BBWResult(originalData[currentIndex].getTickTime(), calculateBollingerBandsWidthValue(bollingerBandsValue));
    }

    private BigDecimal calculateBollingerBandsWidthValue(BBResult bollingerBandsValue) {
        return MathHelper.divide(bollingerBandsValue.getUpperBand().subtract(bollingerBandsValue.getLowerBand()), bollingerBandsValue.getMiddleBand());
    }

}
