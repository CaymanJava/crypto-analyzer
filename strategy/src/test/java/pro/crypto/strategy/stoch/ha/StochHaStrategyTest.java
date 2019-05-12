package pro.crypto.strategy.stoch.ha;

import org.junit.Test;
import pro.crypto.model.StrategyRequest;
import pro.crypto.model.StrategyResult;
import pro.crypto.strategy.StrategyBaseTest;

import static com.google.common.collect.Sets.newHashSet;
import static org.junit.Assert.assertArrayEquals;
import static pro.crypto.model.IndicatorType.MODIFIED_MOVING_AVERAGE;
import static pro.crypto.model.Position.ENTRY_LONG;
import static pro.crypto.model.Position.ENTRY_SHORT;

public class StochHaStrategyTest extends StrategyBaseTest {

    @Test
    public void testStochHaStrategy() {
        StrategyResult[] expectedResult = loadStrategyExpectedResult("stoch_ha.json", StochHaResult[].class);
        StochHaResult[] actualResult = new StochHaStrategy(buildStochHaRequest()).getResult();
        assertArrayEquals(expectedResult, actualResult);
    }

    private StrategyRequest buildStochHaRequest() {
        return StochHaRequest.builder()
                .originalData(originalData)
                .stochMovingAverageType(MODIFIED_MOVING_AVERAGE)
                .stochFastPeriod(5)
                .stochSlowPeriod(3)
                .stochOversoldLevel(30.0)
                .stochOverboughtLevel(70.0)
                .positions(newHashSet(ENTRY_LONG, ENTRY_SHORT))
                .build();
    }

}
