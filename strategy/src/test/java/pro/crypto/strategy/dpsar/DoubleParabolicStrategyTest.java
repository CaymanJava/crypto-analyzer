package pro.crypto.strategy.dpsar;

import org.junit.Test;
import pro.crypto.model.Position;
import pro.crypto.model.StrategyRequest;
import pro.crypto.model.StrategyResult;
import pro.crypto.strategy.StrategyBaseTest;

import static com.google.common.collect.Sets.newHashSet;
import static org.junit.Assert.assertArrayEquals;
import static pro.crypto.model.IndicatorType.EXPONENTIAL_MOVING_AVERAGE;
import static pro.crypto.model.tick.PriceType.CLOSE;

public class DoubleParabolicStrategyTest extends StrategyBaseTest {

    @Test
    public void testDoubleParabolicStrategy() throws Exception {
        StrategyResult[] expectedResult = loadStrategyExpectedResult("d_psar.json", DPSARResult[].class);
        DPSARResult[] actualResult = new DoubleParabolicStrategy(buildDPSARRequest()).getResult();
        assertArrayEquals(expectedResult, actualResult);
    }

    private StrategyRequest buildDPSARRequest() {
        return DPSARRequest.builder()
                .originalData(originalData)
                .movingAveragePeriod(14)
                .movingAveragePriceType(CLOSE)
                .movingAverageType(EXPONENTIAL_MOVING_AVERAGE)
                .macdMovingAverageType(EXPONENTIAL_MOVING_AVERAGE)
                .macdPriceType(CLOSE)
                .macdFastPeriod(5)
                .macdSlowPeriod(8)
                .macdSignalPeriod(9)
                .psarMinAccelerationFactor(0.01)
                .psarMaxAccelerationFactor(0.11)
                .pswMinAccelerationFactor(0.01)
                .pswMaxAccelerationFactor(0.11)
                .positions(newHashSet(Position.ENTRY_LONG, Position.ENTRY_SHORT))
                .build();
    }

}