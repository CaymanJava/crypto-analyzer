package pro.crypto.indicators.env;

import pro.crypto.exception.WrongIncomingParametersException;
import pro.crypto.helper.MathHelper;
import pro.crypto.indicators.ma.MovingAverage;
import pro.crypto.indicators.ma.MovingAverageFactory;
import pro.crypto.model.Indicator;
import pro.crypto.model.IndicatorType;
import pro.crypto.model.request.ENVRequest;
import pro.crypto.model.request.MARequest;
import pro.crypto.model.result.ENVResult;
import pro.crypto.model.result.MAResult;
import pro.crypto.model.tick.Tick;

import java.math.BigDecimal;
import java.util.stream.Stream;

import static java.lang.String.format;
import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;
import static pro.crypto.model.IndicatorType.MOVING_AVERAGE_ENVELOPES;
import static pro.crypto.model.IndicatorType.SIMPLE_MOVING_AVERAGE;
import static pro.crypto.model.tick.PriceType.CLOSE;

public class MovingAverageEnvelopes implements Indicator<ENVResult> {

    private final Tick[] originalData;
    private final IndicatorType movingAverageType;
    private final int movingAveragePeriod;
    private final int indentationPercentage;

    private ENVResult[] result;

    public MovingAverageEnvelopes(ENVRequest request) {
        this.originalData = request.getOriginalData();
        this.movingAverageType = isNull(request.getMovingAverageType()) ? SIMPLE_MOVING_AVERAGE : request.getMovingAverageType();
        this.movingAveragePeriod = request.getMovingAveragePeriod();
        this.indentationPercentage = request.getIndentationPercentage();
        checkIncomingData();
    }

    @Override
    public IndicatorType getType() {
        return MOVING_AVERAGE_ENVELOPES;
    }

    @Override
    public void calculate() {
        result = new ENVResult[originalData.length];
        BigDecimal[] basisValues = calculateBasisValues();
        BigDecimal[] upperEnvelopeValues = calculateUpperEnvelopeValues(basisValues);
        BigDecimal[] lowerEnvelopeValues = calculateLowerEnvelopeValues(basisValues);
        buildMovingAverageEnvelopesResult(basisValues, upperEnvelopeValues, lowerEnvelopeValues);
    }

    @Override
    public ENVResult[] getResult() {
        if (isNull(result)) {
            calculate();
        }
        return result;
    }

    private void checkIncomingData() {
        checkOriginalData(originalData);
        checkOriginalDataSize(originalData, movingAveragePeriod);
        checkPeriod(movingAveragePeriod);
        checkPercentage(indentationPercentage);
        checkMovingAverageType(movingAverageType);
    }

    private void checkPercentage(int indentationPercentage) {
        if (indentationPercentage >= 100 || indentationPercentage <= 0) {
            throw new WrongIncomingParametersException(format("Percentage should be in the range (0, 100) {indicator: {%s}, indentationPercentage: {%d}}",
                    getType().toString(), indentationPercentage));
        }
    }

    private BigDecimal[] calculateBasisValues() {
        return Stream.of(buildMARequest().getResult())
                .map(MAResult::getIndicatorValue)
                .toArray(BigDecimal[]::new);
    }

    private MovingAverage buildMARequest() {
        return MovingAverageFactory.create(MARequest.builder()
                .originalData(originalData)
                .priceType(CLOSE)
                .indicatorType(movingAverageType)
                .period(movingAveragePeriod)
                .build());
    }

    private BigDecimal[] calculateUpperEnvelopeValues(BigDecimal[] basisValues) {
        return Stream.of(basisValues)
                .map(this::calculateUpperEnvelope)
                .toArray(BigDecimal[]::new);
    }

    private BigDecimal calculateUpperEnvelope(BigDecimal basis) {
        return nonNull(basis)
                ? calculateUpperEnvelopeValue(basis)
                : null;
    }

    private BigDecimal calculateUpperEnvelopeValue(BigDecimal basis) {
        return MathHelper.divide(basis.multiply(new BigDecimal(100 + indentationPercentage)), new BigDecimal(100));
    }

    private BigDecimal[] calculateLowerEnvelopeValues(BigDecimal[] basisValues) {
        return Stream.of(basisValues)
                .map(this::calculateLowerEnvelope)
                .toArray(BigDecimal[]::new);
    }

    private BigDecimal calculateLowerEnvelope(BigDecimal basis) {
        return nonNull(basis)
                ? calculateLowerEnvelopeValue(basis)
                : null;
    }

    private BigDecimal calculateLowerEnvelopeValue(BigDecimal basis) {
        return MathHelper.divide(basis.multiply(new BigDecimal(100 - indentationPercentage)), new BigDecimal(100));
    }

    private void buildMovingAverageEnvelopesResult(BigDecimal[] basisValues, BigDecimal[] upperEnvelopeValues, BigDecimal[] lowerEnvelopeValues) {
        for (int currentIndex = 0; currentIndex < result.length; currentIndex++) {
            result[currentIndex] = new ENVResult(
                    originalData[currentIndex].getTickTime(),
                    basisValues[currentIndex],
                    upperEnvelopeValues[currentIndex],
                    lowerEnvelopeValues[currentIndex]);
        }
    }

}
