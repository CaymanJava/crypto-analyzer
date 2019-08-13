package pro.crypto.strategy.ha.macd.psar;

import org.junit.Test;
import pro.crypto.model.strategy.Position;
import pro.crypto.request.StrategyRequest;
import pro.crypto.response.StrategyResult;
import pro.crypto.strategy.StrategyBaseTest;

import static com.google.common.collect.Sets.newHashSet;
import static org.junit.Assert.assertArrayEquals;
import static pro.crypto.model.indicator.IndicatorType.EXPONENTIAL_MOVING_AVERAGE;
import static pro.crypto.model.tick.PriceType.CLOSE;

public class HaMacdPsarStrategyTest extends StrategyBaseTest {

    @Test
    public void testHaMacdPsarStrategy() {
        StrategyResult[] expectedResult = loadStrategyExpectedResult("ha_macd_psar.json", HaMacdPsarResult[].class);
        HaMacdPsarResult[] actualResult = new HaMacdPsarStrategy(buildHaMacdPsarRequest()).getResult();
        assertArrayEquals(expectedResult, actualResult);
    }

    private StrategyRequest buildHaMacdPsarRequest() {
        return HaMacdPsarRequest.builder()
                .originalData(originalData)
                .macdMaType(EXPONENTIAL_MOVING_AVERAGE)
                .macdPriceType(CLOSE)
                .macdFastPeriod(12)
                .macdSlowPeriod(26)
                .macdSignalPeriod(9)
                .psarMinAccelerationFactor(0.02)
                .psarMaxAccelerationFactor(0.2)
                .positions(newHashSet(Position.ENTRY_LONG, Position.ENTRY_SHORT))
                .build();
    }

}
