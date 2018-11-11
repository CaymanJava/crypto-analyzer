package pro.crypto.strategy.hastoch;

import org.junit.Test;
import pro.crypto.model.Position;
import pro.crypto.model.StrategyRequest;
import pro.crypto.model.StrategyResult;
import pro.crypto.strategy.StrategyBaseTest;

import static com.google.common.collect.Sets.newHashSet;
import static org.junit.Assert.assertArrayEquals;
import static pro.crypto.model.IndicatorType.MODIFIED_MOVING_AVERAGE;

public class HAStochStrategyTest extends StrategyBaseTest {

    @Test
    public void testHAStochStrategy() throws Exception {
        StrategyResult[] expectedResult = loadStrategyExpectedResult("ha_stoch.json", HAStochResult[].class);
        HAStochResult[] actualResult = new HAStochStrategy(buildHAStochRequest()).getResult();
        assertArrayEquals(expectedResult, actualResult);
    }

    private StrategyRequest buildHAStochRequest() {
        return HAStochRequest.builder()
                .originalData(originalData)
                .stochMovingAverageType(MODIFIED_MOVING_AVERAGE)
                .stochFastPeriod(5)
                .stochSlowPeriod(3)
                .stochOversoldLevel(30.0)
                .stochOverboughtLevel(70.0)
                .positions(newHashSet(Position.values()))
                .build();
    }

}