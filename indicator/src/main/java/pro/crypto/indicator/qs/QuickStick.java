package pro.crypto.indicator.qs;

import pro.crypto.helper.FakeTicksCreator;
import pro.crypto.helper.IndicatorResultExtractor;
import pro.crypto.indicator.ma.MARequest;
import pro.crypto.indicator.ma.MovingAverageFactory;
import pro.crypto.model.Indicator;
import pro.crypto.model.IndicatorRequest;
import pro.crypto.model.IndicatorType;
import pro.crypto.model.SimpleIndicatorResult;
import pro.crypto.model.tick.Tick;

import java.math.BigDecimal;
import java.util.stream.IntStream;

import static java.util.Objects.isNull;
import static pro.crypto.model.IndicatorType.QUICK_STICK;
import static pro.crypto.model.tick.PriceType.CLOSE;

public class QuickStick implements Indicator<QSResult> {

    private final Tick[] originalData;
    private final IndicatorType movingAverageType;
    private final int period;

    private QSResult[] result;

    public QuickStick(IndicatorRequest creationRequest) {
        QSRequest request = (QSRequest) creationRequest;
        this.originalData = request.getOriginalData();
        this.movingAverageType = request.getMovingAverageType();
        this.period = request.getPeriod();
        checkIncomingData();
    }

    @Override
    public IndicatorType getType() {
        return QUICK_STICK;
    }

    @Override
    public void calculate() {
        BigDecimal[] closeOpenDifference = calculateCloseOpenDifference();
        BigDecimal[] quickStickValues = calculateQuickStickValues(closeOpenDifference);
        buildQuickStickResult(quickStickValues);
    }

    @Override
    public QSResult[] getResult() {
        if (isNull(result)) {
            calculate();
        }
        return result;
    }

    private void checkIncomingData() {
        checkOriginalData(originalData);
        checkOriginalDataSize(originalData, period);
        checkPeriod(period);
        checkMovingAverageType(movingAverageType);
    }

    private BigDecimal[] calculateCloseOpenDifference() {
        return IntStream.range(0, originalData.length)
                .mapToObj(this::calculateCloseOpenDifference)
                .toArray(BigDecimal[]::new);
    }

    private BigDecimal calculateCloseOpenDifference(int currentIndex) {
        return originalData[currentIndex].getClose().subtract(originalData[currentIndex].getOpen());
    }

    private BigDecimal[] calculateQuickStickValues(BigDecimal[] closeOpenDifference) {
        return IndicatorResultExtractor.extractIndicatorValues(calculateMovingAverage(closeOpenDifference));
    }

    private SimpleIndicatorResult[] calculateMovingAverage(BigDecimal[] closeOpenDifference) {
        return MovingAverageFactory.create(buildMARequest(closeOpenDifference)).getResult();
    }

    private IndicatorRequest buildMARequest(BigDecimal[] closeOpenDifference) {
        return MARequest.builder()
                .originalData(FakeTicksCreator.createWithCloseOnly(closeOpenDifference))
                .period(period)
                .priceType(CLOSE)
                .indicatorType(movingAverageType)
                .build();
    }

    private void buildQuickStickResult(BigDecimal[] quickStickValues) {
        result = IntStream.range(0, originalData.length)
                .mapToObj(idx -> new QSResult(originalData[idx].getTickTime(), quickStickValues[idx]))
                .toArray(QSResult[]::new);
    }

}
